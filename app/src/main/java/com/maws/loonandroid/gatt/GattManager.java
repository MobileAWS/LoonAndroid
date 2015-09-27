package com.maws.loonandroid.gatt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.AsyncTask;
import android.os.Handler;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.gatt.events.GattEvent;
import com.maws.loonandroid.gatt.operations.GattDescriptorReadOperation;
import com.maws.loonandroid.gatt.operations.GattOperation;
import org.droidparts.bus.EventBus;
import org.droidparts.util.L;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GattManager {

    private ConcurrentLinkedQueue<GattOperation> mQueue;
    private ConcurrentHashMap<String, BluetoothGatt> mGatts;
    private GattOperation mCurrentOperation;
    private HashMap<UUID, ArrayList<CharacteristicChangeListener>> mCharacteristicChangeListeners;
    private AsyncTask<Void, Void, Void> mCurrentOperationTimeout;
    private static GattManager instance;
    private Handler handler = new Handler();

    public static GattManager getInstance(){
        if(instance == null){
            instance = new GattManager();
        }
        return instance;
    }

    public GattManager( ) {
        mQueue = new ConcurrentLinkedQueue<>();
        mGatts = new ConcurrentHashMap<>();
        mCurrentOperation = null;
        mCharacteristicChangeListeners = new HashMap<>();

        //i need a constant task that asks for rssi on active gatts every 7 seconds
        handler.post(sendData);
    }

    private final Runnable sendData = new Runnable(){
        public void run(){
            try {
                if(mGatts.size() > 0) {
                    //read all of the rssi on the gatt queue
                    for (BluetoothGatt mGatt : mGatts.values()) {
                        mGatt.readRemoteRssi();
                    }
                }
                handler.postDelayed(this, 7000); //this will run every 7 seconds
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onDestroy(){
        mQueue.clear();
        cancelCurrentOperationBundle();
        for(BluetoothGatt gatt: mGatts.values()){
            gatt.close();
        }
        handler.removeCallbacks(sendData);
    }

    public synchronized void cancelCurrentOperationBundle() {
        L.v("Cancelling current operation. Queue size before: " + mQueue.size());
        if(mCurrentOperation != null && mCurrentOperation.getBundle() != null) {
            for(GattOperation op : mCurrentOperation.getBundle().getOperations()) {
                mQueue.remove(op);
            }
        }
        L.v("Queue size after: " + mQueue.size());
        mCurrentOperation = null;
        drive();
    }

    public synchronized void queue(GattOperation gattOperation) {
        mQueue.add(gattOperation);
        L.v("Queueing Gatt operation, size will now become: " + mQueue.size());
        drive();
    }

    private synchronized void drive() {
        if(mCurrentOperation != null) {
            L.e("tried to drive, but currentOperation was not null, " + mCurrentOperation);
            return;
        }
        if( mQueue.size() == 0) {
            L.v("Queue empty, drive loop stopped.");
            mCurrentOperation = null;
            return;
        }

        final GattOperation operation = mQueue.poll();
        L.v("Driving Gatt queue, size will now become: " + mQueue.size());
        setCurrentOperation(operation);
        operation.onStart();

        if(mCurrentOperationTimeout != null) {
            mCurrentOperationTimeout.cancel(true);
        }
        mCurrentOperationTimeout = new AsyncTask<Void, Void, Void>() {
            @Override
            protected synchronized Void doInBackground(Void... voids) {
                try {
                    L.v("Starting to do a background timeout");
                    wait(operation.getTimoutInMillis());
                } catch (InterruptedException e) {
                    L.v("was interrupted out of the timeout");
                    operation.onFinish();
                }
                if(isCancelled()) {
                    L.v("The timeout was cancelled, so we do nothing.");
                    operation.onFinish();
                    return null;
                }
                L.v("Timeout ran to completion, time to cancel the entire operation bundle. Abort, abort!");
                cancelCurrentOperationBundle();
                operation.onFinish();
                return null;
            }

            @Override
            protected synchronized void onCancelled() {
                super.onCancelled();
                operation.onFinish();
                notify();
            }
        }.execute();

        final BluetoothDevice device = operation.getDevice();
        if(mGatts.containsKey(device.getAddress())) {
            execute(mGatts.get(device.getAddress()), operation);
        } else {
            device.connectGatt(LoonAndroid.globalApplicationContext, true, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    EventBus.postEvent(GattEvent.GATT_CONECTION_STATE_CHANGED,
                            new GattManagerBundle(
                                    device.getAddress(),
                                    gatt,
                                    newState,
                                    GattEvent.GATT_CONECTION_STATE_CHANGED) );

                    if (status == 133) {
                        L.e("Got the status 133 bug, closing gatt");
                        gatt.close();
                        mGatts.remove(device.getAddress());
                        return;
                    }

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        L.i("Gatt connected to device " + device.getAddress());
                        mGatts.put(device.getAddress(), gatt);
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        L.i("Disconnected from gatt server " + device.getAddress() + ", newState: " + newState);
                        mGatts.remove(device.getAddress());
                        setCurrentOperation(null);
                        gatt.close();
                        drive();
                    }
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                    ((GattDescriptorReadOperation) mCurrentOperation).onRead(descriptor);
                    setCurrentOperation(null);
                    drive();
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                    setCurrentOperation(null);
                    drive();
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);

                    EventBus.postEvent(GattEvent.GATT_CHARACTERISTIC_READ,
                            new GattManagerBundle(
                                    gatt.getDevice().getAddress(),
                                    gatt,
                                    0,
                                    GattEvent.GATT_CHARACTERISTIC_READ,
                                    characteristic));

                    //((GattCharacteristicReadOperation) mCurrentOperation).onRead(gatt.getDevice().getAddress(),characteristic);
                    setCurrentOperation(null);
                    drive();
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    L.d("services discovered, status: " + status);
                    EventBus.postEvent(GattEvent.GATT_SERVICES_DISCOVERED,
                            new GattManagerBundle(
                                    device.getAddress(),
                                    gatt,
                                    status,
                                    GattEvent.GATT_SERVICES_DISCOVERED ));

                    execute(gatt, operation);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    L.d("Characteristic " + characteristic.getUuid() + "written to on device " + device.getAddress());
                    setCurrentOperation(null);
                    drive();
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    L.e("Characteristic " + characteristic.getUuid() + "was changed, device: " + device.getAddress());
                    EventBus.postEvent(GattEvent.GATT_CHARACTERISTIC_CHANGED,
                            new GattManagerBundle(
                                    device.getAddress(),
                                    gatt,
                                    0,
                                    GattEvent.GATT_CHARACTERISTIC_CHANGED,
                                    characteristic));
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    EventBus.postEvent(GattEvent.GATT_RSSI_READ,
                            new GattManagerBundle(
                                    device.getAddress(),
                                    gatt,
                                    status,
                                    GattEvent.GATT_RSSI_READ,
                                    rssi));
                }
            });
        }
    }

    private void execute(BluetoothGatt gatt, GattOperation operation) {
        if(operation != mCurrentOperation) {
            return;
        }
        operation.onStart();
        operation.execute(gatt);
        if(!operation.hasAvailableCompletionCallback()) {
            setCurrentOperation(null);
            drive();
        }
    }

    public synchronized void setCurrentOperation(GattOperation currentOperation) {
        mCurrentOperation = currentOperation;
    }

    public BluetoothGatt getGatt(BluetoothDevice device) {
        return mGatts.get(device);
    }

    public void addCharacteristicChangeListener(UUID characteristicUuid, CharacteristicChangeListener characteristicChangeListener) {
        if(!mCharacteristicChangeListeners.containsKey(characteristicUuid)) {
            mCharacteristicChangeListeners.put(characteristicUuid, new ArrayList<CharacteristicChangeListener>());
        }
        mCharacteristicChangeListeners.get(characteristicUuid).add(characteristicChangeListener);
    }

    public void queue(GattOperationBundle bundle) {
        for(GattOperation operation : bundle.getOperations()) {
            queue(operation);
        }
    }

    public class GattManagerBundle {
        public final String gattEvent;
        public final BluetoothGatt gatt;
        public final int newState;
        public final String address;
        public final BluetoothGattCharacteristic characteristic;
        public final int rssi;

        public GattManagerBundle(String address, BluetoothGatt gatt,int newState, String gattEvent) {
            this.address = address;
            this.gatt = gatt;
            this.newState = newState;
            this.gattEvent = gattEvent;
            this.characteristic = null;
            this.rssi = 0;
        }

        public GattManagerBundle(String address, BluetoothGatt gatt,int newState, String gattEvent, int rssi) {
            this.address = address;
            this.gatt = gatt;
            this.newState = newState;
            this.gattEvent = gattEvent;
            this.characteristic = null;
            this.rssi = rssi;
        }

        public GattManagerBundle(String address, BluetoothGatt gatt,int newState, String gattEvent, BluetoothGattCharacteristic characteristic) {
            this.address = address;
            this.gatt = gatt;
            this.newState = newState;
            this.gattEvent = gattEvent;
            this.characteristic = characteristic;
            this.rssi = 0;
        }
    }

}
