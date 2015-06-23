package com.maws.loonandroid.requests;

import com.maws.loonandroid.models.Property;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by SANTIAGO on 6/22/2015.
 */
public class UpLoadRequestHandler {

    public interface UploadListener{
        void onSuccess(JSONObject response, List<Property> listProperties, String siteId, String customerId);
        void onFailure(String error);
    }
}
