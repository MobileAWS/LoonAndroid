package com.maws.loonandroid.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.maws.loonandroid.activities.LoginActivity;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.requests.UpLoadRequestHandler;
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
        final Context context = this.getActivity();
        List<Device> devicesWithAlarm = verificationDevicesWithProperties(context);
        emptyLayoutUpload = rootView.findViewById(R.id.emptyLayoutUpload);
        loadEmptyPage(devicesWithAlarm);
        refreshDevicesAdapter(devicesWithAlarm);
        sensorsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.findViewById(R.id.successIV).isShown()) {
                    refreshDevicesAdapter(verificationDevicesWithProperties(context));
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
                if(!Util.isLoginOnline(this.getView().getContext())) {
                    Intent LoginIntent= null;
                    LoginIntent = new Intent(this.getActivity().getApplicationContext(),LoginActivity.class);
                    getActivity().startActivityForResult(LoginIntent, MainActivity.RESQUET_LOGIN_ACTIVITY);
                }
                else{
                    this.uploadInfoToServer(adapter);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void uploadInfoToServer(UploadSensorListAdapter adapter){

        final List<Device> listDevices = adapter.getSelectedItems();
        Context context= this.getActivity();

        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(this.getView().getContext());
        ArrayList<List<DeviceProperty>> listToUpload = selectDevicesWithAlarm( devicePropertyDao,  context ,listDevices);

        int countDevices = 0;
        for(List<DeviceProperty> devicePropertyList:listToUpload ){
            View itemView = Util.getViewByPosition(countDevices,sensorsLV);
            UpLoadRequestHandler uploadRequestHandler = new UpLoadRequestHandler();
            User user =  User.getCurrent(this.getView().getContext());
            uploadRequestHandler.sendDevicePropertiesToServer(this.getView().getContext(), new UpLoadRequestHandler.UploadListener() {
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
                        List<Device> listDevicesAlarm =  verificationDevicesWithProperties(context);
                        refreshDevicesAdapter( verificationDevicesWithProperties(context));
                        loadEmptyPage(listDevicesAlarm);
                    }
                }
            },devicePropertyList,user.getToken(),listDevices.get(countDevices),itemView);
            countDevices++;
        }
    }

    private List<Device> verificationDevicesWithProperties(Context context){
        List<Device> resultDevices = new ArrayList<>();
        DeviceDao sDao = new DeviceDao(this.getActivity());
        List<Device> devicesActives = sDao.getAllActive();
        for(Device device:devicesActives){
            DevicePropertyDao dPDao= new DevicePropertyDao(this.getActivity());
            List<DeviceProperty> devicePropertiesList = dPDao.getAllByIndex(device.getId());
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

    private void setTokenUser(JSONObject response, User user,Context context) throws JSONException {
        if (!response.isNull("error") && response.getString("error").equalsIgnoreCase("true")) {
            CustomToast.showAlert(getView().getContext(), getString(R.string.upload_login_error), CustomToast._TYPE_ERROR);
        } else {

            if (!response.isNull("token") && response.getString("token") != null && !response.isNull("role")) {
                user.setToken(response.getString("token"));
                user.setRole(response.getString("role"));
                User.setCurrent(user, context);
                uploadInfoToServer(adapter);
            }
        }
    }

    private ArrayList<List<DeviceProperty>> selectDevicesWithAlarm(DevicePropertyDao devicePropertyDao, Context context , List<Device> listDevices){
        ArrayList<List<DeviceProperty>> listToUpload = new ArrayList<>();
        for(Device device:listDevices){
            List<DeviceProperty> devicePropertyList = devicePropertyDao.getAllByIndex(device.getId());
            if (devicePropertyList != null && !devicePropertyList.isEmpty()) {
                listToUpload.add(devicePropertyList);
            }
        }
        return listToUpload;
    }

    private  void loadEmptyPage(List<Device> devicesWithAlarm){
        if(devicesWithAlarm != null && devicesWithAlarm.size() > 0){
            emptyLayoutUpload.setVisibility(View.GONE);
        }else {
            emptyLayoutUpload.setVisibility(View.VISIBLE);
        }
    }
    public UploadSensorListAdapter getAdapter(){
        return this.adapter;
    }
}
