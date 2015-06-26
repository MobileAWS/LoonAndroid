package com.maws.loonandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.LoginActivity;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.requests.UpLoadRequestHandler;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomProgressSpinner;
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

        DeviceDao sDao = new DeviceDao(this.getActivity());
        List<Device> devices = sDao.getAllActive();

        adapter = new UploadSensorListAdapter(this.getActivity(), devices);
        sensorsLV.setAdapter(adapter);

        sensorsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    List<Device> listDevices = adapter.getSelectedItems();
                    uploadInfoToServer(listDevices);
                }else{
                    CustomToast.showAlert(this.getView().getContext(), getString(R.string.upload_login_error), CustomToast._TYPE_ERROR);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void uploadInfoToServer( List<Device> listDevices){
        final CustomProgressSpinner spinner = new CustomProgressSpinner(this.getView().getContext(), this.getView().getContext().getString(R.string.upload_action));
        spinner.show();
        ArrayList<List<DeviceProperty>> listToUpload = new ArrayList<>();
        for(Device device:listDevices){
            DevicePropertyDao devicePropertyDao = new DevicePropertyDao(this.getView().getContext());
            List<DeviceProperty> devicePropertyList = devicePropertyDao.getAllByDeviceId(device.getId());
            if (devicePropertyList != null && !devicePropertyList.isEmpty()) {
                listToUpload.add(devicePropertyList);
            }
        }
        int contDevices = 0;
        for(List<DeviceProperty> devicePropertyList:listToUpload ){

            UpLoadRequestHandler upLoadRequestHandler = new UpLoadRequestHandler();
            User user =User.getCurrent(this.getView().getContext());
            upLoadRequestHandler.sendDevicePropertiesToServer(this.getView().getContext(), new UpLoadRequestHandler.UploadListener() {
                @Override
                public void onFailure(String error,Context context) {
                    try {
                        JSONObject object = new JSONObject(error);
                        spinner.dismiss();
                        CustomToast.showAlert(context, getString(R.string.upload_server_error), CustomToast._TYPE_ERROR);
                    } catch (Exception ex) {
                        spinner.dismiss();
                        CustomToast.showAlert(context, getString(R.string.default_request_error_message), CustomToast._TYPE_ERROR);
                    }
                }

                @Override
                public void onSuccess(JSONObject response, List<DeviceProperty> listDeviceProperties,Context context,int actualPost, int totalDevices) {
                    String responseServer = "";
                    try {
                         responseServer = response.getString("response");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("num Devices",actualPost + " : " + totalDevices);
                    if(actualPost == totalDevices-1){
                        spinner.dismiss();
                    }
                    if("done".equalsIgnoreCase(responseServer)){
                        DevicePropertyDao dPDao = new DevicePropertyDao(context);
                        for(DeviceProperty deviceProperty:listDeviceProperties) {
                            dPDao.delete(deviceProperty);
                        }
                    }
                }
            },devicePropertyList,user.getToken(),listDevices.get(contDevices).getHardwareId(),contDevices,listDevices.size());
            contDevices++;
        }
    }

}
