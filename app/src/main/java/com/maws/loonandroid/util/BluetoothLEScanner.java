package com.maws.loonandroid.util;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

/**
 * Created by Andrexxjc on 17/12/2015.
 */
public class BluetoothLEScanner {
    private final Handler mHandler;
    private final BluetoothAdapter.LeScanCallback mLeScanCallback;
    private final BluetoothUtils mBluetoothUtils;
    private boolean mScanning;

    public BluetoothLEScanner(final BluetoothAdapter.LeScanCallback leScanCallback, final BluetoothUtils bluetoothUtils) {
        mHandler = new Handler();
        mLeScanCallback = leScanCallback;
        mBluetoothUtils = bluetoothUtils;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void scanLeDevice(final int duration, final boolean enable) {
        if (enable) {
            if (mScanning) {
                return;
            }
            // Stops scanning after a pre-defined scan period.
            if (duration > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
                    }
                }, duration);
            }
            mScanning = true;
            mBluetoothUtils.getBluetoothAdapter().startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
        }
    }
}
