package com.maws.loonandroid;

import android.app.Application;
import android.content.Context;

/**
 * Created by Andrexxjc on 01/05/2015.
 */
public class LoonAndroid extends Application {

    public static Context globalApplicationContext;
    public static final boolean demoMode = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        globalApplicationContext = getApplicationContext();
    }

}

