package com.maws.loonandroid.listener;

import org.json.JSONObject;

/**
 * Created by Andrexxjc on 01/05/2015.
 */
public interface StandardRequestListener {

    public void onSuccess(JSONObject jsonObject);
    public void onFailure(String error);

}
