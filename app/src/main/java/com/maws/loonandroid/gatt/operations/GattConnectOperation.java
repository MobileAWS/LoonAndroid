package com.maws.loonandroid.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

/**
 * Created by Andrexxjc on 02/06/2015.
 */
public class GattConnectOperation extends GattOperation {

    public GattConnectOperation(BluetoothDevice device){
        super(device);
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        Log.d("GattConnect", "Connected successfully!");
        String a = "";
        a.toString();
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }
}
