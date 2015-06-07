package com.maws.loonandroid.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.activities.MonitorActivity;
import com.maws.loonandroid.activities.ScanDevicesActivity;
import com.maws.loonandroid.adapters.SensorListAdapter;
import com.maws.loonandroid.contentproviders.AlertContentProvider;
import com.maws.loonandroid.contentproviders.SensorContentProvider;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorCharacteristic;
import com.maws.loonandroid.models.SensorService;
import com.maws.loonandroid.services.BLEService;
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

        //i need a loader to listen to alarm changes on the db alerts
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);

        return rootView;
    }

    public void loadSensors(){

        //get database
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        SensorDao sDao = new SensorDao(this.getActivity());

        List<Sensor> sensors = sDao.getAllActive();
        if(sensors.size() > 0) {
            adapter = new SensorListAdapter(this.getActivity(), sensors);
            sensorsLV.setAdapter(adapter);

            sensorsLV.setVisibility(View.VISIBLE);
            activeSensorHeaderTV.setVisibility(View.VISIBLE);
        }else{
            sensorsLV.setVisibility(View.GONE);
            activeSensorHeaderTV.setVisibility(View.GONE);
        }

        List<Sensor> inactiveSensors = sDao.getAllInactive();
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

            //now let's pick a service at random
            int randomService = ran.nextInt(SensorService.serviceNames.values().size());
            Alert fakeAlert = new Alert();
            fakeAlert.setSensorId(sSensor.getId());
            fakeAlert.setSensorServiceId(randomService);
            fakeAlert.setIsOn(ran.nextBoolean());
            Util.generateAlarm(this.getActivity(), fakeAlert);
        }
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(id == 0) {
            String[] projection = {
                    AlertDao.KEY_ID,
                    AlertDao.KEY_SENSOR_SERVICE_ID,
                    AlertDao.KEY_ALERT_DATE,
                    AlertDao.KEY_DISMISSED
            };
            CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                    AlertContentProvider.CONTENT_URI, projection, null, null, null);
            return cursorLoader;

        } else if(id == 1){
            String[] projection = {
                    SensorDao.KEY_ID,
                    SensorDao.KEY_NAME,
                    SensorDao.KEY_CODE,
                    SensorDao.KEY_SERIAL,
                    SensorDao.KEY_VERSION,
                    SensorDao.KEY_DESCRIPTION,
                    SensorDao.KEY_MAC_ADDRESS,
                    SensorDao.KEY_ACTIVE,
                    SensorDao.KEY_CONNECTED
            };
            CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                    SensorContentProvider.CONTENT_URI, projection, null, null, null);
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
