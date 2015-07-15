package com.maws.loonandroid.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.StatusListAdapter;
import com.maws.loonandroid.contentproviders.DevicePropertyContentProvider;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;
import java.util.HashMap;
import java.util.List;


public class SensorsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

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
        devicesLV =(ExpandableListView) rootView.findViewById(R.id.devicesLV);
        emptyMessageLV = (LinearLayout) rootView.findViewById(R.id.emptyMessageLV);

        //i need a loader to listen to alarm changes on the db alerts
        getLoaderManager().initLoader(0, null, this);
        emptyMessageLV.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                DevicePropertyDao.KEY_ID,
                DevicePropertyDao.KEY_CREATED_AT,
                DevicePropertyDao.KEY_DISMISSED_DATE
        };
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                DevicePropertyContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    private void loadStatus(){

        DeviceDao dDao = new DeviceDao(this.getActivity());
        List<Device> deviceList = dDao.getAll();
        if(deviceList.size() > 0){
            emptyMessageLV.setVisibility(View.INVISIBLE);
            HashMap<String,List<Property>> hashMapProperties = new HashMap<>();
            hashMapProperties.put("0",Util.convertArrayToList(Property.defaultProperties));
            adapter = new StatusListAdapter(getActivity(), deviceList,hashMapProperties);
            devicesLV.setAdapter(adapter);
            for(int i = 0; i < adapter.getGroupCount();i++){
                devicesLV.expandGroup(i);
            }
            devicesLV.setVisibility(View.VISIBLE);
        }else {
            emptyMessageLV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadStatus();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loadStatus();
    }

}
