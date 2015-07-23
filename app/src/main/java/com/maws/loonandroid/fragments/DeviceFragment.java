package com.maws.loonandroid.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.activities.MonitorActivity;
import com.maws.loonandroid.activities.ScanDevicesActivity;
import com.maws.loonandroid.adapters.DeviceListAdapter;
import com.maws.loonandroid.contentproviders.DevicePropertyContentProvider;
import com.maws.loonandroid.contentproviders.DeviceContentProvider;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;

import java.util.List;
import java.util.Random;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class DeviceFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private View emptyLayout;
    private RecyclerView sensorsLV;
    private DeviceListAdapter adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public DeviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_device, container, false);
        sensorsLV = (RecyclerView) rootView.findViewById(R.id.sensorsLV);
        final Context context = this.getActivity();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                BLEService service = BLEService.getInstance();
                if(service != null){
                    service.disconnect( adapter.getItem( viewHolder.getAdapterPosition() ).getMacAddress() );
                }
                final DeviceDao dDao = new DeviceDao(context);
                final DevicePropertyDao dpDao = new DevicePropertyDao(context);
                final long deviceId = adapter.getItem( viewHolder.getAdapterPosition()).getId();
                try {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Device")
                            .setMessage("Do you really want to delete this device?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Device currentDevice = dDao.get(deviceId);
                                    dpDao.deleteForDeviceId(currentDevice.getId());
                                    dDao.delete(currentDevice);
                                }})
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialogInterface,int whichButton){
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).show();

                }catch (Exception e) {

                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(sensorsLV);

        // Setup layout manager for items
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        sensorsLV.setLayoutManager(layoutManager);
        emptyLayout = rootView.findViewById(R.id.emptyLayout);

        //i need a loader to listen to alarm changes on the db alerts
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);

        return rootView;
    }

    public void loadSensors(){

        //get database
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        DeviceDao sDao = new DeviceDao(this.getActivity());

        List<Device> devices = sDao.getAll();
        if(devices.size() > 0) {
            adapter = new DeviceListAdapter(this.getActivity(), devices, new DeviceListAdapter.DeviceViewHolderClickListener() {
                @Override
                public void onClick(Device device) {
                    Intent intent = new Intent(getActivity(), MonitorActivity.class);
                    intent.putExtra(MonitorActivity.MONITOR_ID, device.getId());
                    startActivity(intent);
                }
            });
            sensorsLV.setAdapter(adapter);
            sensorsLV.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }else{
            emptyLayout.setVisibility(View.VISIBLE);
            sensorsLV.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!LoonAndroid.demoMode) {
            inflater.inflate(R.menu.sensors, menu);
        }else{
            inflater.inflate(R.menu.sensors_demo, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.action_start_scan:
                Intent scanIntent = new Intent(this.getActivity(), ScanDevicesActivity.class);
                startActivityForResult(scanIntent, MainActivity.REQUEST_SCAN);
                return true;

            case R.id.action_restart_service:
                BLEService service = BLEService.getInstance();
                if(service != null){
                    service.restart();
                }
                return true;

            case R.id.action_remove_sensors:
                removeSensors();
                return true;

            case R.id.action_generate_random_alert:
                generateRandomAlert();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeSensors(){
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        DeviceDao sDao = new DeviceDao(this.getActivity());
        sDao.deleteAll(loonDao.getWritableDatabase());
        loadSensors();
    }

    private void generateRandomAlert(){
        if(adapter == null){
            return;
        }

        //let's count active sensors
        int activeCount = 0;
        for(int i = 0; i < adapter.getItemCount(); i++){
            if(adapter.getItem(i).isActive()){
                activeCount++;
            }else{
                break;
            }
        }

        //let's pick one of the sensors at random
        if(activeCount > 0){

            Random ran = new Random();
            int x = ran.nextInt(activeCount);
            Device sDevice = (Device)adapter.getItem(x);

            //now let's pick a service at random
            int randomService = ran.nextInt( Property.defaultProperties.length );
            DeviceProperty fakeAlert = new DeviceProperty();
            fakeAlert.setDeviceId(sDevice.getId());
            fakeAlert.setPropertyId( Property.defaultProperties[randomService].getId() );
            fakeAlert.setValue("On");
            Util.generateAlarm(this.getActivity(), fakeAlert);
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(id == 0) {
            String[] projection = {
                    DevicePropertyDao.KEY_ID,
                    DevicePropertyDao.KEY_CREATED_AT,
                    DevicePropertyDao.KEY_DISMISSED_DATE
            };
            CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                    DevicePropertyContentProvider.CONTENT_URI, projection, null, null, null);
            return cursorLoader;

        } else if(id == 1){
            String[] projection = {
                    DeviceDao.KEY_ID,
                    DeviceDao.KEY_NAME,
                    DeviceDao.KEY_CODE,
                    DeviceDao.KEY_HARDWARE_ID,
                    DeviceDao.KEY_FIRMWARE_VERSION,
                    DeviceDao.KEY_HARDWARE_VERSION,
                    DeviceDao.KEY_DESCRIPTION,
                    DeviceDao.KEY_MAC_ADDRESS,
                    DeviceDao.KEY_ACTIVE,
                    DeviceDao.KEY_CONNECTED
            };
            CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                    DeviceContentProvider.CONTENT_URI, projection, null, null, null);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadSensors();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loadSensors();
    }

}
