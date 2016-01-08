package com.maws.loonandroid;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Andrexxjc on 01/05/2015.
 */
public class LoonAndroid extends Application {

    public static Context globalApplicationContext;
    public static final boolean demoMode = BuildConfig.demo;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        globalApplicationContext = getApplicationContext();
    }

}

