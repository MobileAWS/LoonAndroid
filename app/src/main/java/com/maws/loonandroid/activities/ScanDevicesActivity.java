package com.maws.loonandroid.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.BluetoothDeviceAdapter;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.fragments.AddSensorDialogFragment;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.views.CustomToast;

/**
 * Created by Andrexxjc on 27/05/2015.
 */
public class ScanDevicesActivity extends ActionBarActivity {

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = LoonAndroid.demoMode? 4000:10000;
    public static final int REQUEST_ENABLE_BT = 1001;

    private Handler mHandler;
    private static final String TAG = "SCAN";
    private ListView sensorsLV;
    private ProgressBar scanPB;
    private TextView scanTV;
    private boolean discovering = false; //this is used only for demo mode

    private BluetoothDeviceAdapter scanAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!LoonAndroid.demoMode && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            CustomToast.showAlert(this, getString(R.string.ble_not_supported), CustomToast._TYPE_ERROR);
            finish();
        }

        mHandler = new Handler();
        setContentView(R.layout.activity_scan_devices);

        scanPB = (ProgressBar)findViewById(R.id.scanPB);
        scanTV = (TextView) findViewById(R.id.scanTV);

        sensorsLV = (ListView) findViewById(R.id.sensorsLV);
        scanAdapter = new BluetoothDeviceAdapter(this, new BluetoothDeviceAdapter.BluetoothDeviceOptionListener() {
            @Override
            public void onDeviceAdded(Sensor sensor) {
                showSensorDialog(sensor);
            }

            @Override
            public void onDeviceIgnored(Sensor sensor) {
                ignoreSensor(sensor);
            }
        });
        sensorsLV.setAdapter(scanAdapter);
        startScanning();
    }

    public void startScanning(){
        scanAdapter.clear();

        if(!LoonAndroid.demoMode && mBluetoothAdapter == null) {
            // Initializes Bluetooth adapter.
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // Checks if Bluetooth is supported on the device.
        if (!LoonAndroid.demoMode && mBluetoothAdapter == null) {
            CustomToast.showAlert(this, getString(R.string.ble_not_supported), CustomToast._TYPE_ERROR);
            finish();
            return;
        }
        // Checks if Bluetooth is enabled, or asks to make it enabled.
        if (!LoonAndroid.demoMode && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        if( LoonAndroid.demoMode ||!mBluetoothAdapter.isDiscovering()) {
            if(!LoonAndroid.demoMode) {
                mBluetoothAdapter.startDiscovery();
            }
            discovering = true;
            invalidateOptionsMenu();
            scanTV.setText(getString(R.string.scanning));
            scanPB.setVisibility(View.VISIBLE);

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScanning();
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
        }

    }

    private void stopScanning(){
        if(LoonAndroid.demoMode && !isFinishing()){
            discovering = false;
            scanAdapter.add(Sensor.createFakeSensor());
            scanPB.setVisibility(View.GONE);
            try{
                if(scanAdapter.getCount() == 0){
                    scanTV.setText( getString(R.string.scan_zero_found) );
                }else if(scanAdapter.getCount() == 1){
                    scanTV.setText( getString(R.string.scan_one_found) );
                }else if(scanAdapter.getCount() > 1){
                    scanTV.setText( String.format( getString(R.string.scan_many_found), scanAdapter.getCount() ));
                }
            }catch(Exception ex){
                //probably this activity already finished
            }
        }else if (!isFinishing() && mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            scanPB.setVisibility(View.GONE);

            try{
                if(scanAdapter.getCount() == 0){
                    scanTV.setText( getString(R.string.scan_zero_found) );
                }else if(scanAdapter.getCount() == 1){
                    scanTV.setText( getString(R.string.scan_one_found) );
                }else if(scanAdapter.getCount() > 1){
                    scanTV.setText( String.format( getString(R.string.scan_many_found), scanAdapter.getCount() ));
                }
            }catch(Exception ex){
                //probably this activity already finished
            }
        }
    }

    //let's use this broadcast receiver to listen to monitor discoveries
    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short defaultShort = 0;
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, defaultShort);

                //i need to know if this device is already on our database
                SensorDao sDao = new SensorDao(ScanDevicesActivity.this);
                Sensor mSensor = sDao.findByMacAddress(device.getAddress());
                if(mSensor == null){
                    mSensor = new Sensor(device);
                }
                scanAdapter.add(mSensor);
                scanAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onPause();

        //register receiver of devices scanned
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanning();
        unregisterReceiver(ActionFoundReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScanning();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan, menu);
        if(LoonAndroid.demoMode){
            if(!discovering){
                menu.findItem(R.id.action_scan).setVisible(true);
            }else{
                menu.findItem(R.id.action_scan).setVisible(false);
            }
        }else {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isDiscovering()) {
                menu.findItem(R.id.action_scan).setVisible(true);
            } else {
                menu.findItem(R.id.action_scan).setVisible(false);
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                startScanning();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // Start the scan again.
                    startScanning();
                }
                break;
        }
    }

    public void showSensorDialog(Sensor sensor){
        //for now, let's create a random sensor and add it to the dialog
        AddSensorDialogFragment newFragment = AddSensorDialogFragment.newInstance(sensor, new AddSensorDialogFragment.AddSensorDialogListener() {
            @Override
            public void onSensorAdded(Sensor sensor) {
                addSensor(sensor);
            }
        });
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void addSensor(Sensor sensor){
        sensor.setActive(true);
        saveSensor(sensor);
    }

    private void ignoreSensor(Sensor sensor){
        sensor.setActive(false);
        saveSensor(sensor);
    }

    private void saveSensor(Sensor sensor){
        SensorDao sDao = new SensorDao(this);
        sDao.create(sensor);
        scanAdapter.notifyDataSetChanged();
    }


}