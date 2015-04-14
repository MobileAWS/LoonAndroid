package com.maws.loonandroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.SensorListAdapter;
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.models.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class SensorFragment extends Fragment {

    private ListView sensorsLV;
    private SensorListAdapter adapter;

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
        List<Sensor> sensors = null;
        if(getActivity() instanceof MainActivity){
            sensors = ((MainActivity)getActivity()).getSensors();
        }else{
            sensors = new ArrayList<Sensor>();
        }
        adapter = new SensorListAdapter(this.getActivity(), sensors);
        sensorsLV.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sensors, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


}
