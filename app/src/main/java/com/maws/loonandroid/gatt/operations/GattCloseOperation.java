package com.maws.loonandroid.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

/**
 * Created by Andrexxjc on 24/08/2015.
 */
public class GattCloseOperation  extends GattOperation {

    public GattCloseOperation(BluetoothDevice device) {
        super(device);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        gatt.close();
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }
}
