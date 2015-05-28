package com.maws.loonandroid.fragments;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.activities.MonitorActivity;
import com.maws.loonandroid.activities.ScanDevicesActivity;
import com.maws.loonandroid.adapters.BluetoothDeviceAdapter;
import com.maws.loonandroid.adapters.SensorListAdapter;
import com.maws.loonandroid.contentproviders.AlertContentProvider;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorService;
import com.maws.loonandroid.util.Util;
import java.util.List;
import java.util.Random;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class SensorFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private View emptyLayout;
    private TextView activeSensorHeaderTV, inactiveSensorHeaderTV;
    private ListView sensorsLV, inactiveSensorsLV;
    private SensorListAdapter adapter, inactiveAdapter;



    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SensorFragment newInstance() {
        SensorFragment fragment = new SensorFragment();
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public SensorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);
        sensorsLV = (ListView) rootView.findViewById(R.id.sensorsLV);
        sensorsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MonitorActivity.class);
                intent.putExtra(MonitorActivity.MONITOR_ID, id);
                startActivity(intent);
            }
        });

        inactiveSensorsLV = (ListView) rootView.findViewById(R.id.inactiveSensorsLV);
        activeSensorHeaderTV = (TextView) rootView.findViewById(R.id.activeSensorHeaderTV);
        inactiveSensorHeaderTV = (TextView) rootView.findViewById(R.id.inactiveSensorHeaderTV);
        emptyLayout = rootView.findViewById(R.id.emptyLayout);

        //i need a loader to listen to alarm changes on the db
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    public void loadSensors(){

        //get database
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        SensorDao sDao = new SensorDao(this.getActivity());

        List<Sensor> sensors = sDao.getAllActive(loonDao.getReadableDatabase());
        if(sensors.size() > 0) {
            adapter = new SensorListAdapter(this.getActivity(), sensors);
            sensorsLV.setAdapter(adapter);

            sensorsLV.setVisibility(View.VISIBLE);
            activeSensorHeaderTV.setVisibility(View.VISIBLE);
        }else{
            sensorsLV.setVisibility(View.GONE);
            activeSensorHeaderTV.setVisibility(View.GONE);
        }

        List<Sensor> inactiveSensors = sDao.getAllInactive(loonDao.getReadableDatabase());
        if(inactiveSensors.size() > 0) {
            inactiveAdapter = new SensorListAdapter(this.getActivity(), inactiveSensors);
            inactiveSensorsLV.setAdapter(inactiveAdapter);

            inactiveSensorsLV.setVisibility(View.VISIBLE);
            inactiveSensorHeaderTV.setVisibility(View.VISIBLE);
        }else{
            inactiveSensorsLV.setVisibility(View.GONE);
            inactiveSensorHeaderTV.setVisibility(View.GONE);
        }

        if(sensors.size() <= 0 && inactiveSensors.size() <= 0){
            emptyLayout.setVisibility(View.VISIBLE);
        }else{
            emptyLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sensors, menu);
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
        SensorDao sDao = new SensorDao(this.getActivity());
        sDao.deleteAll(loonDao.getWritableDatabase());
        loadSensors();
    }

    private void generateRandomAlert(){
        //let's pick one of the sensors at random
        if(adapter != null && adapter.getCount() > 0){

            Random ran = new Random();
            int x = ran.nextInt(adapter.getCount());
            Sensor sSensor = (Sensor)adapter.getItem(x);
            sSensor.loadServices(this.getActivity());

            if(sSensor.getSensorServices().size() > 0) {
                //now, once inside the sensor, i need to randomly pick a service
                SensorService sService = sSensor.getSensorServices().get(ran.nextInt(sSensor.getSensorServices().size()));

                //now we generate a random alert
                Util.generateAlarm(
                        this.getActivity(),
                        sSensor,
                        sService);
            }
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {AlertDao.KEY_ID, AlertDao.KEY_SENSOR_SERVICE_ID, AlertDao.KEY_ALERT_DATE, AlertDao.KEY_DISMISSED};
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                AlertContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
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
