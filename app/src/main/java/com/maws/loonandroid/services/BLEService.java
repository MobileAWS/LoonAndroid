package com.maws.loonandroid.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.gatt.GattCharacteristicReadCallback;
import com.maws.loonandroid.gatt.GattManager;
import com.maws.loonandroid.gatt.events.GattEvent;
import com.maws.loonandroid.gatt.operations.GattCharacteristicReadOperation;
import com.maws.loonandroid.gatt.operations.GattConnectOperation;
import com.maws.loonandroid.gatt.operations.GattDescriptorWriteOperation;
import com.maws.loonandroid.gatt.operations.GattSetNotificationOperation;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorCharacteristic;
import com.maws.loonandroid.models.SensorService;
import com.maws.loonandroid.util.Util;

import org.droidparts.bus.EventBus;
import org.droidparts.bus.EventReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andrexxjc on 30/05/2015.
 */
public class BLEService extends Service
        implements EventReceiver<GattManager.GattManagerBundle>{

    private static final String TAG = "BLEService";
    private static BLEService instance;
    private static final HashMap<String, String> switchValues = new HashMap<String, String>();

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
        EventBus.registerReceiver( this, GattEvent.GATT_CONECTION_STATE_CHANGED, GattEvent.GATT_SERVICES_DISCOVERED, GattEvent.GATT_CHARACTERISTIC_CHANGED, GattEvent.GATT_CHARACTERISTIC_READ);
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
        //LoonMedicalDao lDao = new LoonMedicalDao(this);
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

                //I need the device to read all characteristics
                BluetoothDevice device = null;
                try {
                    device = mBluetoothAdapter.getRemoteDevice(data.address);
                } catch (Exception ex) {
                    //just don't connect to the device if the mac address is weird
                }

                if (device == null) {
                    Log.w(TAG, "Device not found.  Unable to connect.");
                    return;
                }

                //once i have discovered the services i need to read a couple of them for information on the device
                GattCharacteristicReadOperation readInitialCare = new GattCharacteristicReadOperation(
                        device,
                        SensorService.UUID_CARE_SENTINEL_SERVICE,
                        SensorCharacteristic._CHAR_CARE_SENTINEL,
                        null
                );
                manager.queue(readInitialCare);

                //lastly, i subscribe to the notification service
                GattSetNotificationOperation operation = new GattSetNotificationOperation(
                        device,
                        SensorService.UUID_CARE_SENTINEL_SERVICE,
                        SensorCharacteristic._CHAR_CARE_SENTINEL,
                        SensorCharacteristic._DESCRIPTOR_CARE_SENTINEL_NOTIFICATIONS
                );
                manager.queue(operation);
                break;

            case GattEvent.GATT_CHARACTERISTIC_CHANGED:
                //the care sentinel characteristic has changed
                if(data.characteristic.getUuid().equals(SensorCharacteristic._CHAR_CARE_SENTINEL) ){
                    String result = Integer.toBinaryString( data.characteristic.getValue()[0] );
                    updateDeviceState(data.address, result, true);
                    Log.d(TAG, "The new value of this chara is " + result);
                }
                break;

            case GattEvent.GATT_CHARACTERISTIC_READ:
                if(data.characteristic.getUuid().equals(SensorCharacteristic._CHAR_CARE_SENTINEL) ){
                    String result = Integer.toBinaryString( data.characteristic.getValue()[0] );
                    updateDeviceState(data.address, result, false);
                    Log.d(TAG, "The new value of this chara is " + result);
                }
                break;
        }
    }


    private void updateDeviceState(String address, String newState, boolean fireNotification){

        if(TextUtils.isEmpty(address) || TextUtils.isEmpty(newState)) {
            return;
        }

        StringBuilder builder = new StringBuilder(newState);
        if(builder.length() > 8){
            builder.delete(0, builder.length() - 8);
        }
        while(builder.length() < 8){
            builder.insert(0, 0);
        }
        newState = builder.toString();
        if(fireNotification && switchValues.containsKey(address)){

            SensorDao sDao = new SensorDao(this);
            Sensor sensor = sDao.findByMacAddress(address);
            if(sensor != null) {

                String previousState = switchValues.get(address);

                //i need to know which indexes changed
                int indexChanged = -1;
                for (int i = 0; i < previousState.length(); i++) {
                    if (previousState.charAt(i) != newState.charAt(i)) {
                        //send an alarm to the broadcast receiver
                        Alert realAlert = new Alert();
                        realAlert.setSensorId(sensor.getId());
                        realAlert.setIsOn( newState.charAt(i) == '1' );
                        realAlert.setSensorServiceId(i);
                        Util.generateAlarm(this, realAlert);
                        break;
                    }
                }
            }

        }
        switchValues.put(address, builder.toString());

    }

}
