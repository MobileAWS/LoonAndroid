package com.maws.loonandroid.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Andrexxjc on 04/06/2015.
 */
public class Customer {

    private long id;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    /*This will save the user in the preferences so we have it available for all the app*/
    private static final String CUSTOMER_PREFS = "Site_prfs";
    private static final String CUSTOMER_PREFS_ID = "Site_i";
    private static final String CUSTOMER_PREFS_CODE = "Site_c";

    public static Customer instance = null;
    public static void setCurrent(Customer customer, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(CUSTOMER_PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(CUSTOMER_PREFS_ID, customer.getId());
        editor.putString(CUSTOMER_PREFS_CODE, customer.getCode());
        editor.apply();
        instance = customer;
    }

    public static Customer getCurrent(Context context){
        if(instance == null){
            SharedPreferences prefs = context.getSharedPreferences(CUSTOMER_PREFS, Context.MODE_PRIVATE);
            instance = new Customer();
            instance.setId(prefs.getLong(CUSTOMER_PREFS_ID, -1));
            instance.setCode(prefs.getString(CUSTOMER_PREFS_CODE, ""));
        }
        return instance;
    }
}
