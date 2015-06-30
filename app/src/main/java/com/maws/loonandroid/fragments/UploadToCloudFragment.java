package com.maws.loonandroid.fragments;

import android.content.Context;
import android.graphics.Color;
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
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.requests.UploadRequestHandler;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class UploadToCloudFragment extends Fragment {

    private ListView sensorsLV;
    private View emptyLayoutUpload;
    private UploadSensorListAdapter adapter;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UploadToCloudFragment newInstance() {
        UploadToCloudFragment fragment = new UploadToCloudFragment();
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public UploadToCloudFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        sensorsLV = (ListView) rootView.findViewById(R.id.sensorsLV);

        List<Device> devicesWithAlarm = verificationDevicesWithProperties();
        emptyLayoutUpload = rootView.findViewById(R.id.emptyLayoutUpload);
        if(devicesWithAlarm != null && devicesWithAlarm.size() > 0){
            emptyLayoutUpload.setVisibility(View.GONE);
        }else {
            emptyLayoutUpload.setVisibility(View.VISIBLE);
        }
        refreshDevicesAdapter(devicesWithAlarm);

        sensorsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view.findViewById(R.id.successIV).isShown()){
                    refreshDevicesAdapter( verificationDevicesWithProperties());
                }
                adapter.toogleItem(position);
            }
        });

        adapter.selectAll();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_start_scan:
                if(Util.isLoginOnline(this.getView().getContext())) {
                    uploadInfoToServer(adapter);
                }else{
                    CustomToast.showAlert(this.getView().getContext(), getString(R.string.upload_login_error), CustomToast._TYPE_ERROR);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void uploadInfoToServer(UploadSensorListAdapter adapter){

        List<Device> listDevices = adapter.getSelectedItems();

        ArrayList<List<DeviceProperty>> listToUpload = new ArrayList<>();
        for(Device device:listDevices){
            DevicePropertyDao devicePropertyDao = new DevicePropertyDao(this.getView().getContext());
            List<DeviceProperty> devicePropertyList = devicePropertyDao.getAllByDeviceId(device.getId());
            if (devicePropertyList != null && !devicePropertyList.isEmpty()) {
                listToUpload.add(devicePropertyList);
            }
        }
        int countDevices = 0;
        for(List<DeviceProperty> devicePropertyList:listToUpload ){
            View itemView = Util.getViewByPosition(countDevices,sensorsLV);
            UploadRequestHandler uploadRequestHandler = new UploadRequestHandler();
            User user =User.getCurrent(this.getView().getContext());
            uploadRequestHandler.sendDevicePropertiesToServer(this.getView().getContext(), new UploadRequestHandler.UploadListener() {
                @Override
                public void onFailure(String error,Context context, View progressBarView) {
                    try {
                        JSONObject object = new JSONObject(error);
                        setUpMessageUpload(progressBarView, Color.RED, "Fail");


                        CustomToast.showAlert(context, getString(R.string.upload_server_error), CustomToast._TYPE_ERROR);
                    } catch (Exception ex) {
                        CustomToast.showAlert(context, getString(R.string.default_request_error_message), CustomToast._TYPE_ERROR);
                    }
                }

                @Override
                public void onSuccess(JSONObject response, List<DeviceProperty> listDeviceProperties,Context context,View progressBarView) {
                    String responseServer = "";
                    try {
                         responseServer = response.getString("response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setUpMessageUpload(progressBarView, Color.green(45),"Done");
                    if("done".equalsIgnoreCase(responseServer)){
                        DevicePropertyDao dPDao = new DevicePropertyDao(context);
                        for(DeviceProperty deviceProperty:listDeviceProperties) {
                            dPDao.delete(deviceProperty);
                        }
                        refreshDevicesAdapter( verificationDevicesWithProperties());
                    }
                }
            },devicePropertyList,user.getToken(),listDevices.get(countDevices).getHardwareId(),itemView);
            countDevices++;
        }
    }

    private List<Device> verificationDevicesWithProperties(){
        List<Device> resultDevices = new ArrayList<>();
        DeviceDao sDao = new DeviceDao(this.getActivity());
        List<Device> devicesActives = sDao.getAllActive();
        for(Device device:devicesActives){
            DevicePropertyDao dPDao= new DevicePropertyDao(this.getActivity());
            List<DeviceProperty> devicePropertiesList = dPDao.getAllByDeviceId(device.getId());
            if(devicePropertiesList != null && devicePropertiesList.size() > 0){
                resultDevices.add(device);
            }
        }
        return resultDevices;
    }

    private void refreshDevicesAdapter(List<Device> devicesWithAlarm){
        adapter = new UploadSensorListAdapter(this.getActivity(), devicesWithAlarm);
        sensorsLV.setAdapter(adapter);
    }


    private void setUpMessageUpload(View progressBarView,int color,String text){
        progressBarView.findViewById(R.id.progressBarUploadIV).setVisibility(View.GONE);
        TextView textView = (TextView) progressBarView.findViewById(R.id.successIV);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setVisibility(View.VISIBLE);
        progressBarView.findViewById(R.id.checkIV).setVisibility(View.INVISIBLE);
        progressBarView.findViewById(R.id.checkIV).setVisibility(View.GONE);
    }
}
