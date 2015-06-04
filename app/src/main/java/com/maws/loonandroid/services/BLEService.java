package com.maws.loonandroid.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.gatt.GattManager;
import com.maws.loonandroid.gatt.events.GattEvent;
import com.maws.loonandroid.gatt.operations.GattConnectOperation;
import com.maws.loonandroid.models.Sensor;

import org.droidparts.bus.EventBus;
import org.droidparts.bus.EventReceiver;

import java.util.List;
import java.util.UUID;

/**
 * Created by Andrexxjc on 30/05/2015.
 */
public class BLEService extends Service
        implements EventReceiver<GattManager.GattManagerBundle>{

    private static final String TAG = "BLEService";
    private static BLEService instance;

    int mStartMode = START_STICKY; // indicates how to behave if the service is killed
    boolean mAllowRebind; // indicates whether onRebind should be used
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private GattManager manager;

    public static BLEService getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        manager = new GattManager();
        instance = this;
        EventBus.registerReceiver( this, GattEvent.GATT_CONECTION_STATE_CHANGED, GattEvent.GATT_SERVICES_DISCOVERED );
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.i(TAG, "Started the BLE service");
        initializeMonitors();
        return mStartMode;
    }

    public void initializeMonitors(){

        //let's first try to initialize the adapter
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return;
        }

        //i need to read the database for devices
        LoonMedicalDao lDao = new LoonMedicalDao(this);
        SensorDao sDao = new SensorDao(this);
        List<Sensor> sensors = sDao.getAllActive();

        for(Sensor sensor: sensors){
            //i need to get the mac address and try to connect to it
            connect(sensor.getMacAddress());
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public void connect(final String address) {

        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return;
        }

        BluetoothDevice device = null;
        try{
            device = mBluetoothAdapter.getRemoteDevice(address);
        }catch(Exception ex){
            //just don't connect to the device if the mac address is weird
        }

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return;
        }

        GattConnectOperation operation = new GattConnectOperation(device);
        manager.queue(operation);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        instance = null;
        manager.onDestroy();
    }

    public void restart(){
        manager.onDestroy();
        //initializeMonitors();
    }

    @Override
    public void onEvent(String name, GattManager.GattManagerBundle data) {
        Log.d("BLEService", "I have connected to the following address " + data.address + " " + data.newState);

        switch (data.gattEvent){
            case GattEvent.GATT_CONECTION_STATE_CHANGED:

                //here, i need to get the sensor from the database and update its conected status
                SensorDao sDao = new SensorDao(this);
                Sensor sensor = sDao.findByMacAddress(data.address);
                if(sensor != null && data.newState == BluetoothProfile.STATE_CONNECTED){
                    sensor.setConnected(true);
                }else{
                    sensor.setConnected(false);
                }
                sDao.update(sensor);
                break;
            case GattEvent.GATT_SERVICES_DISCOVERED:

                //I have found the services. Let's try
                for(BluetoothGattService bService: data.gatt.getServices()){
                    UUID serviceUUID = bService.getUuid();
                    serviceUUID.toString();
                }
                break;
        }
    }
}
