package com.maws.loonandroid.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.maws.loonandroid.R;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomProgressSpinner;
import com.maws.loonandroid.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrexxjc on 01/05/2015.
 */
public class UserRequestHandler {

    private static final String TAG = "UserRequestHandler";
    public static final String KEY_ROLE_ID = "caregiver";

    public static void signUp( final Context context, final User user, final StandardRequestListener listener){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.creating_user));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + "user/sign_up";
        Log.d("Sign Up URL: ", url);
        JSONObject userJson = null;
        try {
            userJson = createJsonSingUpObject(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest =  new JsonObjectRequest(Request.Method.POST,url,userJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            spinner.dismiss();
                            JSONObject jsonObject = response;
                            listener.onSuccess(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinner.dismiss();
                        listener.onFailure(VolleySingleton.getResponseData(error.networkResponse));
                        error.printStackTrace();
                    }
                });
        jsObjRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(jsObjRequest);
    }

    private static JSONObject createJsonSingUpObject(User user) throws JSONException {
        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("email", user.getEmail());
        userJsonObject.put("password",user.getPassword());
        userJsonObject.put("confirm_password", user.getPassword());
        userJsonObject.put("role_id", UserRequestHandler.KEY_ROLE_ID);

        return userJsonObject;
    }

    private static  JSONObject createJsonLoginObject(User user,String siteId , String customerId) throws  JSONException{
        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("email",user.getEmail());
        userJsonObject.put("password",user.getPassword());
        userJsonObject.put("confirm_password",user.getPassword());
        userJsonObject.put("role_id",KEY_ROLE_ID);
        userJsonObject.put("site_name",siteId);
        userJsonObject.put("customer_id",customerId);
        return  userJsonObject;
    }

    public static void generateUserId(final Context context, final StandardRequestListener listener){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.generating_user_id));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + "user/generateUserId";
        Log.d("Generate User Id URL: ", url);
        StringRequest generateUserIdRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    spinner.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    listener.onSuccess(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFailure(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.dismiss();
                listener.onFailure( VolleySingleton.getResponseData(error.networkResponse) );
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                long timestamp = (new Date()).getTime();
                String headerToSend = "{\"environment\":\"%1$s\",\"action\":\"generateUserId\",\"timestamp\":\"%2$s\"}";
                headerToSend = String.format(headerToSend, VolleySingleton.ENVIRONMENT, timestamp);
                headerToSend = Util.MD5(headerToSend);

                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("APP_TOKEN",headerToSend);
                params.put("APP_TIMESTAMP",String.valueOf(timestamp));
                return params;
            }
        };
        generateUserIdRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(generateUserIdRequest);
    }

    public static void resetPassword(final Context context, final StandardRequestListener listener, final String email){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.resetting_password));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + "user/resetPassword";
        Log.d("Reset Password URL: ", url);
        StringRequest generateUserIdRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    spinner.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    listener.onSuccess(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFailure(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinner.dismiss();
                listener.onFailure(VolleySingleton.getResponseData(error.networkResponse));
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                Log.d("Reset Password Params:\n", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        generateUserIdRequest.setRetryPolicy(VolleySingleton.getRetryPolicy() );
        queue.add(generateUserIdRequest);
    }

    public interface LoginListener{
        void onSuccess(JSONObject response, User user, String siteId, String customerId,Context context);
        void onFailure(String error);
    }


    public static void login( final Context context,
                              final LoginListener listener,
                                final User user, final String siteId, final String customerId){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.signing_in));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + "users/login";
        JSONObject loginJson=new JSONObject();
        try {
             loginJson= createJsonLoginObject(user,siteId,customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest loginRequest =  new JsonObjectRequest(Request.Method.POST,url,loginJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spinner.dismiss();
                        listener.onSuccess(response, user, siteId, customerId,context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinner.dismiss();
                        try {
                            if (error.networkResponse != null) {
                                listener.onFailure(VolleySingleton.getResponseData(error.networkResponse));
                                Log.d(TAG, new String(error.networkResponse.data));
                            } else {
                                listener.onFailure(error.getCause().getMessage());
                                Log.d(TAG, new String(error.getCause().getMessage()));
                            }
                        }catch (Exception e) {
                            listener.onFailure(null);
                        }
                        error.printStackTrace();
                    }
                });
        queue.add(loginRequest);
    }


}
