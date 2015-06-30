package com.maws.loonandroid.requests;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;

import com.maws.loonandroid.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Aprada on 6/22/2015.
 */
public class UploadRequestHandler {

    static final public String URL_SERVICE = "device/addproperties";
    private static final String TAG = "UploadRequestHandler";

    /* values json Objectt */
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DEVICE ="device";
    private static final String KEY_DEVICE_ID = "hw_id";
    private static final String KEY_PROPERTY_ID = "key";
    private static final String KEY_PROPERTY_METRIC = "metric";
    private static final String KEY_PROPERTY_VALUE = "value";
    private static final String KEY_PROPERTY_CREATE_AT = "create_at";
    private static final String KEY_PROPERTY_DISMISS = "dismiss_time";
    private static final String KEY_PROPERTY_DISMISS_DURATION = "dismiss_duration";
    private static final String KEY_LIST_PROPERTIES ="properties";
    /*Fin*/


    public void sendDevicePropertiesToServer(final Context context,
                                             final UploadListener listener,final List<DeviceProperty> listDeviceProperties, final String token,final String hardwareID,final View progressBarView){


        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();

        String url = VolleySingleton.SERVER_URL + URL_SERVICE ;

        JSONObject devicePropertiesJson = new JSONObject();
        try {
            devicePropertiesJson=createJsonObjectToSend(devicePropertiesJson,listDeviceProperties,token,hardwareID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       progressBarView.findViewById(R.id.progressBarUploadIV).setVisibility(View.VISIBLE);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,devicePropertiesJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    listener.onSuccess(response,listDeviceProperties,context, progressBarView);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        listener.onFailure( VolleySingleton.getResponseData(error.networkResponse),context ,progressBarView );
                        error.printStackTrace();
                    }
                });
        queue.add(jsObjRequest);
    }
    public interface UploadListener{
        void onFailure(String error,Context context,View view);
        void onSuccess(JSONObject response, List<DeviceProperty> listDeviceProperties,Context context, View view);
    }


    private JSONObject createJsonObjectToSend (JSONObject devicePropertiesJson ,List<DeviceProperty> listDeviceProperties ,String token,String hardwareID) throws JSONException {

        devicePropertiesJson.put(KEY_TOKEN, token);
        JSONObject propertyJson = new JSONObject();
        propertyJson.put(KEY_DEVICE_ID, hardwareID);
        devicePropertiesJson.put(KEY_DEVICE, propertyJson);
        JSONArray jsonArray = new JSONArray();
        for (DeviceProperty deviceProperty:listDeviceProperties) {
            JSONObject devicePropertyJson = new JSONObject();
            devicePropertyJson.put(KEY_PROPERTY_ID,Property.getDefaultProperty(deviceProperty.getPropertyId()).getName());
            devicePropertyJson.put(KEY_PROPERTY_METRIC,"Boolean");
            if(deviceProperty.getValue().equals("On")) {
                devicePropertyJson.put(KEY_PROPERTY_VALUE, 1);
            } else {
                devicePropertyJson.put(KEY_PROPERTY_VALUE, 0);
            }
            devicePropertyJson.put(KEY_PROPERTY_CREATE_AT,deviceProperty.getCreatedAt().getTime());
            if(deviceProperty.getDismissedAt() != null)
                devicePropertyJson.put(KEY_PROPERTY_DISMISS,deviceProperty.getDismissedAt().getTime());
            else
                devicePropertyJson.put(KEY_PROPERTY_DISMISS,null);
            devicePropertyJson.put(KEY_PROPERTY_DISMISS_DURATION,deviceProperty.getTotalTimeAlarm());
            jsonArray.put(devicePropertyJson);
        }
        devicePropertiesJson.put(KEY_LIST_PROPERTIES, jsonArray);
        return  devicePropertiesJson;
    }
}
