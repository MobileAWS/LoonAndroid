package com.maws.loonandroid.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.StatusListAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.PropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;

import java.util.HashMap;
import java.util.List;


public class SensorsFragment extends Fragment {


    private ExpandableListView devicesLV;
    private LinearLayout emptyMessageLV;
    private StatusListAdapter adapter;

    public static SensorsFragment newInstance() {
        SensorsFragment fragment = new SensorsFragment();

        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public SensorsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_status, container, false);
        Context context = this.getActivity();
        devicesLV =(ExpandableListView) rootView.findViewById(R.id.devicesLV);
        emptyMessageLV = (LinearLayout) rootView.findViewById(R.id.emptyMessageLV);
        DeviceDao dDao = new DeviceDao(this.getActivity());
        PropertyDao pDao = new PropertyDao(this.getActivity());

        List<Device> deviceList = dDao.getAll();
        if(deviceList.size() > 0){
            emptyMessageLV.setVisibility(View.INVISIBLE);
            HashMap<String,List<Property>> hashMapProperties = new HashMap<>();
            hashMapProperties.put("0",Util.convertArrayToList(Property.defaultProperties));
            adapter = new StatusListAdapter(context,deviceList,hashMapProperties);
            devicesLV.setAdapter(adapter);
            for(int i = 0; i < adapter.getGroupCount();i++){
                devicesLV.expandGroup(i);
            }
            devicesLV.setVisibility(View.VISIBLE);
        }else {
            emptyMessageLV.setVisibility(View.VISIBLE);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

}
