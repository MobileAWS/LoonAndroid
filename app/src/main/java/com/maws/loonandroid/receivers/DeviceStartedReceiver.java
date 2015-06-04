package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.maws.loonandroid.services.BLEService;

/**
 * This class is used to start the service that listens to the bluetooth devices when the device is started
 */
public class DeviceStartedReceiver extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Intent intent = new Intent(arg0, BLEService.class);
        arg0.startService(intent);
        Log.i("Autostart", "Started BLE service, i think");
    }
}
