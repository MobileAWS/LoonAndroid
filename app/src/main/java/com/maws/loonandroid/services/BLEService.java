package com.maws.loonandroid.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.gatt.GattManager;
import com.maws.loonandroid.gatt.events.GattEvent;
import com.maws.loonandroid.gatt.operations.GattCharacteristicReadOperation;
import com.maws.loonandroid.gatt.operations.GattConnectOperation;
import com.maws.loonandroid.gatt.operations.GattSetNotificationOperation;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceCharacteristic;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.DeviceService;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;

import org.droidparts.bus.EventBus;
import org.droidparts.bus.EventReceiver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrexxjc on 30/05/2015.
 */
public class BLEService extends Service
        implements EventReceiver<GattManager.GattManagerBundle>{

    private static final String TAG = "BLEService";
    private static BLEService instance;
    public static final HashMap<String, String> switchValues = new HashMap<String, String>();

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
        DeviceDao deviceDao = new DeviceDao(this);
        deviceDao.disconnectAllDevices();
        manager = GattManager.getInstance();
        instance = this;
        EventBus.registerReceiver( this, GattEvent.GATT_CONECTION_STATE_CHANGED, GattEvent.GATT_SERVICES_DISCOVERED, GattEvent.GATT_CHARACTERISTIC_CHANGED, GattEvent.GATT_CHARACTERISTIC_READ, GattEvent.GATT_RSSI_READ);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.i(TAG, "Started the BLE service");
        DeviceDao deviceDao = new DeviceDao(this);
        deviceDao.disconnectAllDevices();
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
        DeviceDao sDao = new DeviceDao(this);
        List<Device> devices = sDao.getAllActive();

        for(Device device : devices){
            //i need to get the mac address and try to connect to it
            connect(device.getMacAddress());
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
            DeviceDao deviceDao = new DeviceDao(this);
            Device currentDevice = deviceDao.findByMacAddress(address);
            currentDevice.setConnected(false);
            deviceDao.update(currentDevice);
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
        DeviceDao deviceDao = new DeviceDao(this);
        deviceDao.disconnectAllDevices();
    }

    public void restart(){
        manager.onDestroy();
        initializeMonitors();
    }

    @Override
    public void onEvent(String name, GattManager.GattManagerBundle data) {

        try{

            Log.d("BLEService", "I have connected to the following address " + data.address + " " + data.newState);
            DeviceDao sDao = new DeviceDao(this);
            Device device = sDao.findByMacAddress(data.address);

            switch (data.gattEvent){
                case GattEvent.GATT_CONECTION_STATE_CHANGED:
                    if(device != null && data.newState == BluetoothProfile.STATE_CONNECTED){
                        device.setConnected(true);
                    }else{
                        device.setConnected(false);
                    }
                    sDao.update(device);
                    break;
                case GattEvent.GATT_SERVICES_DISCOVERED:

                    //I need the device to read all characteristics
                    BluetoothDevice bluetoothDevice = null;
                    try {
                        bluetoothDevice = mBluetoothAdapter.getRemoteDevice(data.address);
                    } catch (Exception ex) {
                        //just don't connect to the device if the mac address is weird
                    }

                    if (bluetoothDevice == null) {
                        Log.w(TAG, "Device not found.  Unable to connect.");
                        return;
                    }

                    //once i have discovered the services i need to read a couple of them for information on the device

                    //here, i read the initial status of the switches
                    GattCharacteristicReadOperation readInitialCare = new GattCharacteristicReadOperation(
                            bluetoothDevice,
                            DeviceService.UUID_CARE_SENTINEL_SERVICE,
                            DeviceCharacteristic._CHAR_CARE_SENTINEL,
                            null
                    );
                    manager.queue(readInitialCare);

                    //here, i read the device information if i hadn't read it before
                    //read the hardware id
                    if( TextUtils.isEmpty(device.getHardwareId())){
                        GattCharacteristicReadOperation readSerial = new GattCharacteristicReadOperation(
                                bluetoothDevice,
                                DeviceService.UUID_DEVICE_INFORMATION,
                                DeviceCharacteristic._CHAR_SERIAL_NUMBER,
                                null
                        );
                        manager.queue(readSerial);
                    }

                    //read the firmware version
                    if( TextUtils.isEmpty(device.getFirmwareVersion())){
                        GattCharacteristicReadOperation readSerial = new GattCharacteristicReadOperation(
                                bluetoothDevice,
                                DeviceService.UUID_DEVICE_INFORMATION,
                                DeviceCharacteristic._CHAR_FIRMWARE_REVISION,
                                null
                        );
                        manager.queue(readSerial);
                    }

                    //read the hardware version
                    if( TextUtils.isEmpty(device.getFirmwareVersion())){
                        GattCharacteristicReadOperation readSerial = new GattCharacteristicReadOperation(
                                bluetoothDevice,
                                DeviceService.UUID_DEVICE_INFORMATION,
                                DeviceCharacteristic._CHAR_HARDWARE_REVISION,
                                null
                        );
                        manager.queue(readSerial);
                    }

                    //let's read the current battery status
                    GattCharacteristicReadOperation readBattery = new GattCharacteristicReadOperation(
                            bluetoothDevice,
                            DeviceService.UUID_BATTERY_SERVICE,
                            DeviceCharacteristic._CHAR_BATTERY_DATA,
                            null
                    );
                    manager.queue(readBattery);

                    //let's also read the current temperature
                    GattCharacteristicReadOperation readTemperature = new GattCharacteristicReadOperation(
                            bluetoothDevice,
                            DeviceService.UUID_THERMOMETER_SERVICE,
                            DeviceCharacteristic._CHAR_THERMOMETER,
                            null
                    );
                    manager.queue(readTemperature);

                    //lastly, i subscribe to the notification service
                    GattSetNotificationOperation operation = new GattSetNotificationOperation(
                            bluetoothDevice,
                            DeviceService.UUID_CARE_SENTINEL_SERVICE,
                            DeviceCharacteristic._CHAR_CARE_SENTINEL,
                            DeviceCharacteristic._DESCRIPTOR_CARE_SENTINEL_NOTIFICATIONS
                    );
                    manager.queue(operation);
                    break;

                case GattEvent.GATT_CHARACTERISTIC_CHANGED:
                    //the care sentinel characteristic has changed
                    if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_CARE_SENTINEL) ){
                        updateDeviceState(data.address,
                                Integer.toBinaryString(data.characteristic.getValue()[0]),
                                Integer.toBinaryString(data.characteristic.getValue()[1]),
                                true);
                        //Log.d(TAG, "The new value of this chara is " + result);
                    }
                    break;

                case GattEvent.GATT_CHARACTERISTIC_READ:
                    if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_CARE_SENTINEL) ){
                        String result = Integer.toBinaryString( data.characteristic.getValue()[0] );
                        updateDeviceState(data.address,
                                Integer.toBinaryString( data.characteristic.getValue()[0] ),
                                Integer.toBinaryString( data.characteristic.getValue()[1] ),
                                false);
                        Log.d(TAG, "The new value of this chara is " + result);
                    }else if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_SERIAL_NUMBER)){
                        //i need to get the serial number here
                        String result = new String(data.characteristic.getValue());
                        device.setHardwareId(result);
                        sDao.updateHardwareId(device);
                    }else if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_FIRMWARE_REVISION)){
                        //i need to get the serial number here
                        String result = new String(data.characteristic.getValue());
                        device.setFirmwareVersion(result);
                        sDao.updateFirmwareVersion(device);
                    }else if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_HARDWARE_REVISION)){
                        //i need to get the serial number here
                        String result = new String(data.characteristic.getValue());
                        device.setHardwareVersion(result);
                        sDao.updateHardwareVersion(device);
                    }else if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_BATTERY_DATA)){
                        //let's read battery data here
                        int asInt = (data.characteristic.getValue()[0] & 0xFF);
                        device.setBatteryStatus(asInt);
                        sDao.updateBatteryStatus(device);
                    }else if(data.characteristic.getUuid().equals(DeviceCharacteristic._CHAR_THERMOMETER)){
                        //let's read thermometer data here
                        boolean isCelsius = true;
                        if(data.characteristic.getValue()[0] == 1){
                            isCelsius = false;
                        }

                        int mantissa = (
                                data.characteristic.getValue()[1] |
                                ((data.characteristic.getValue()[2] << 8) |
                                (data.characteristic.getValue()[3] << 16)) );

                        int exponent = data.characteristic.getValue()[4];
                        double value = mantissa * Math.pow(10, exponent);
                        value = Util.to2Decimal(value);
                        if(!isCelsius){
                            value = Util.fahrenheitToCelsius(value);
                        }
                        device.setTemperature(value);
                        sDao.updateTemperature(device);
                    }
                    break;
                case GattEvent.GATT_RSSI_READ:
                    device.setSignalStrength(data.rssi);
                    sDao.updateSignal(device);
                    break;
            }
        }
        catch(Exception ex){
            //ignore this, we just need the service never to crash
        }
    }


    private void updateDeviceState(String address, String newState, String newState2, boolean fireNotification){

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

        builder = new StringBuilder(newState2);
        if(builder.length() > 8){
            builder.delete(0, builder.length() - 8);
        }
        while(builder.length() < 8){
            builder.insert(0, 0);
        }
        newState2 = builder.toString();

        String newTotalState = newState + newState2;
        if(fireNotification && switchValues.containsKey(address)){

            DeviceDao sDao = new DeviceDao(this);
            Device device = sDao.findByMacAddress(address);
            if(device != null) {

                String previousState = switchValues.get(address);

                //i need to know which indexes changed
                int indexChanged = -1;
                for (int i = 0; i < previousState.length(); i++) {
                    if ( previousState.charAt(i) != newTotalState.charAt(i)) {

                        Property thisProp = Property.getDefaultProperty(i);
                        if(thisProp == null){
                            continue;
                        }

                        //send an alarm to the broadcast receiver
                        DeviceProperty realAlert = new DeviceProperty();
                        realAlert.setDeviceId(device.getId());
                        realAlert.setValue( newTotalState.charAt(i) == '1' ? "On": "Off" );
                        realAlert.setPropertyId(i);
                        Util.generateAlarm(this, realAlert);
                        break;
                    }
                }
            }
        }
        switchValues.put(address, newTotalState);
    }

}
