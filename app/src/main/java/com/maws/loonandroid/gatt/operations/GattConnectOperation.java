package com.maws.loonandroid.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.models.Device;

/**
 * Created by Andrexxjc on 02/06/2015.
 */
public class GattConnectOperation extends GattOperation {

    Context context;

    public GattConnectOperation(BluetoothDevice device, Context context){
        super(device);
        this.context = context;
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt) {
        Log.d("GattConnect", "Connected successfully!");
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return false;
    }

    @Override
    public void onFinish(){
        String macAddress = getDevice().getAddress();
        if(TextUtils.isEmpty(macAddress))return;

        DeviceDao dDao = new DeviceDao(context);
        Device d = dDao.findByMacAddress(macAddress);
        if(d != null){
            d.setConnecting(false);
            dDao.update(d);
        }
    }
}
