package com.maws.loonandroid.requests;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.ContactDao;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;
import com.maws.loonandroid.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aprada on 6/22/2015.
 */
public class UpLoadRequestHandler {

    static final public String URL_SERVICE = "device/addproperties";
    static final public String URL_SMS = "send_sms";
    private static final String TAG = "UploadRequestHandler";

    /* values json Objectt */
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DEVICE ="device";
    private static final String KEY_DEVICE_ID = "hw_id";
    private static final String KEY_DEVICE_NAME = "name";
    private static final String KEY_PROPERTY_ID = "key";
    private static final String KEY_PROPERTY_METRIC = "metric";
    private static final String KEY_PROPERTY_VALUE = "value";
    private static final String KEY_PROPERTY_CREATE_AT = "created_at";
    private static final String KEY_PROPERTY_DISMISS = "dismiss_time";
    private static final String KEY_PROPERTY_DISMISS_DURATION = "dismiss_duration";
    private static final String KEY_LIST_PROPERTIES ="properties";
    /*Fin*/


    public void sendDevicePropertiesToServer(final Context context,
                                             final UploadListener listener,final List<DeviceProperty> listDeviceProperties, final String token,final Device device,final View progressBarView){


        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();

        String url = VolleySingleton.SERVER_URL + URL_SERVICE ;

        JSONObject devicePropertiesJson = new JSONObject();
        try {
            devicePropertiesJson=createJsonObjectToSend(devicePropertiesJson,listDeviceProperties,token,device);
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
                        try {
                            listener.onFailure(VolleySingleton.getResponseData(error.networkResponse), context, progressBarView);
                        } catch (Exception e){
                            CustomToast.showAlert(context,context.getString(R.string.error_upload_message));
                            progressBarView.findViewById(R.id.progressBarUploadIV).setVisibility(View.GONE);
                        }
                        error.printStackTrace();
                    }
                });
        jsObjRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(jsObjRequest);
    }
    public interface UploadListener{
        void onFailure(String error,Context context,View view);
        void onSuccess(JSONObject response, List<DeviceProperty> listDeviceProperties,Context context, View view);
    }


    private JSONObject createJsonObjectToSend (JSONObject devicePropertiesJson ,List<DeviceProperty> listDeviceProperties ,String token,Device device) throws JSONException {

        devicePropertiesJson.put(KEY_TOKEN, token);
        JSONObject propertyJson = new JSONObject();
        propertyJson.put(KEY_DEVICE_ID, device.getHardwareId());
        propertyJson.put(KEY_DEVICE_NAME, TextUtils.isEmpty(device.getDescription())?device.getName():device.getDescription());
        devicePropertiesJson.put(KEY_DEVICE, propertyJson);
        JSONArray jsonArray = new JSONArray();
        for (DeviceProperty deviceProperty:listDeviceProperties) {
            JSONObject devicePropertyJson = new JSONObject();
            devicePropertyJson.put(KEY_PROPERTY_ID, Property.getDefaultProperty(deviceProperty.getPropertyId(), device.getType()).getName());
            devicePropertyJson.put(KEY_PROPERTY_METRIC,"Boolean");
            if(deviceProperty.getValue().equals("On")) {
                devicePropertyJson.put(KEY_PROPERTY_VALUE, "On");
            } else {
                devicePropertyJson.put(KEY_PROPERTY_VALUE, "Off");
            }
            devicePropertyJson.put(KEY_PROPERTY_CREATE_AT, String.valueOf(deviceProperty.getCreatedAt().getTime()/1000L));
            if(deviceProperty.getDismissedAt() != null ) {
                devicePropertyJson.put(KEY_PROPERTY_DISMISS, String.valueOf(deviceProperty.getDismissedAt().getTime()/1000L));
                devicePropertyJson.put(KEY_PROPERTY_DISMISS_DURATION , String.valueOf(deviceProperty.getTotalTimeAlarm()));
            }
            jsonArray.put(devicePropertyJson);
        }
        devicePropertiesJson.put(KEY_LIST_PROPERTIES, jsonArray);
        return  devicePropertiesJson;
    }

    public interface SendSMSListener {
        public void onSuccess(String result);
        public void onFailure(String error);
    }
    public static void sendSMS(final Context context,
                    final String message,
                    final String latitude,
                    final String longitude,
                    final SendSMSListener listener){

        ContactDao cDao = new ContactDao(context);
        final List<Contact> contacts = cDao.getAll();
        if(contacts.size() <= 0 || TextUtils.isEmpty(message)){
            return;
        }
        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + URL_SMS ;
        StringRequest generateUserIdRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailure( VolleySingleton.getResponseData(error.networkResponse) );
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                long timestamp = (new Date()).getTime();
                String headerToSend = "{\"environment\":\"%1$s\",\"action\":\"send_sms\",\"timestamp\":\"%2$s\"}";
                headerToSend = String.format(headerToSend, VolleySingleton.ENVIRONMENT, timestamp);
                headerToSend = Util.MD5(headerToSend);

                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("APP_TOKEN",headerToSend);
                params.put("APP_TIMESTAMP",String.valueOf(timestamp));
                return params;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                for(Contact contact: contacts) {
                    params.put("phone_numbers[]", contact.getNumber());
                }
                params.put("message",message);
                if(!TextUtils.isEmpty(latitude))
                    params.put("latitude",latitude);
                if(!TextUtils.isEmpty(longitude))
                    params.put("longitude",longitude);
                return params;
            }
        };
        generateUserIdRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(generateUserIdRequest);
    }
}
