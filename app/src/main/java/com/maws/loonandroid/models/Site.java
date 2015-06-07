package com.maws.loonandroid.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Andrexxjc on 04/06/2015.
 */
public class Site {

    private long id;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /*This will save the user in the preferences so we have it available for all the app*/
    private static final String SITE_PREFS = "Site_prfs";
    private static final String SITE_PREFS_ID = "Site_i";
    private static final String SITE_PREFS_CODE = "Site_c";

    public static Site instance = null;
    public static void setCurrent(Site site, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SITE_PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(SITE_PREFS_ID, site.getId());
        editor.putString(SITE_PREFS_CODE, site.getCode());
        editor.apply();
        instance = site;
    }

    public static Site getCurrent(Context context){
        if(instance == null){
            SharedPreferences prefs = context.getSharedPreferences(SITE_PREFS, Context.MODE_PRIVATE);
            instance = new Site();
            instance.setId(prefs.getLong(SITE_PREFS_ID, -1));
            instance.setCode(prefs.getString(SITE_PREFS_CODE, ""));
        }
        return instance;
    }

}
