package com.maws.loonandroid.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.maws.loonandroid.R;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomProgressSpinner;
import com.maws.loonandroid.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrexxjc on 01/05/2015.
 */
public class UserRequestHandler {

    private static final String TAG = "UserRequestHandler";

    public static void signUp( final Context context, final StandardRequestListener listener,
           final String email, final String pass, final long customerId, final String site ){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.creating_user));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();
        String url = VolleySingleton.SERVER_URL + "user/sign_up";
        Log.d("Sign Up URL: ", url);
        StringRequest signUpRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",email);
                params.put("password",pass);
                params.put("confirm_password",pass);
                params.put("customer_site_id", site);
                params.put("customer_id", String.valueOf(customerId));
                Log.d(TAG, params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        signUpRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(signUpRequest);
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
                listener.onFailure( VolleySingleton.getResponseData(error.networkResponse) );
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
        generateUserIdRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(generateUserIdRequest);
    }

    public interface LoginListener{
        public void onSuccess(JSONObject response, String email, String password, String userSite);
        public void onFailure(String error);
    }

    public static void login( final Context context, final LoginListener listener,
                               final String email, final String pass, final String site ){

        final CustomProgressSpinner spinner = new CustomProgressSpinner(context, context.getString(R.string.signing_in));
        spinner.show();

        VolleySingleton vs = VolleySingleton.getInstance();
        RequestQueue queue = vs.getRequestQueue();

        StringBuilder url = new StringBuilder( VolleySingleton.SERVER_URL + "users/login" );

        try {
            url.append("?email=");
            url.append(URLEncoder.encode( email, VolleySingleton.DEFAULT_PARAMS_ENCODING ));
            url.append('&');
            url.append("password=");
            url.append(URLEncoder.encode( pass , VolleySingleton.DEFAULT_PARAMS_ENCODING));
            url.append('&');
            url.append("customer_site_id=");
            url.append(URLEncoder.encode( site , VolleySingleton.DEFAULT_PARAMS_ENCODING));

        } catch (Exception e) {
            //ignore it for now. We control the UTF chars on this encoding
        }

        StringRequest signUpRequest = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    spinner.dismiss();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject responseRoot = jsonObject.getJSONObject("response");
                    if(responseRoot.optBoolean("error", false)){
                        listener.onFailure(responseRoot.toString());
                        return;
                    }

                    listener.onSuccess(jsonObject, email, pass, site);
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
                Log.d(TAG, new String(error.networkResponse.data));
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        signUpRequest.setRetryPolicy( VolleySingleton.getRetryPolicy() );
        queue.add(signUpRequest);
    }
}
