package com.maws.loonandroid.fragments;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.maws.loonandroid.adapters.SensorListAdapter;
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.models.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class SensorFragment extends Fragment {

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

        loadSensors();
        return rootView;
    }

    private void loadSensors(){

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
                showSensorDialog();
                return true;

            case R.id.action_remove_sensors:
                removeSensors();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSensorDialog(){
        //for now, let's create a random sensor and add it to the dialog
        AddSensorDialogFragment newFragment = AddSensorDialogFragment.newInstance(Sensor.createFakeSensor(), new AddSensorDialogFragment.AddSensorDialogListener() {
            @Override
            public void onSensorAdded(Sensor sensor) {
                sensor.setActive(true);
                saveSensor(sensor);
            }

            @Override
            public void onSensorIgnored(Sensor sensor) {
                sensor.setActive(false);
                saveSensor(sensor);
            }
        });
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void saveSensor(Sensor sensor){
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        SensorDao sDao = new SensorDao(this.getActivity());
        sDao.create(sensor, loonDao.getWritableDatabase());
        loadSensors();
    }

    private void removeSensors(){
        LoonMedicalDao loonDao = new LoonMedicalDao(this.getActivity());
        SensorDao sDao = new SensorDao(this.getActivity());
        sDao.deleteAll(loonDao.getWritableDatabase());
        loadSensors();
    }

}
