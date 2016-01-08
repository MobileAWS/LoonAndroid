package com.maws.loonandroid.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.BluetoothDeviceAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.fragments.AddSensorDialogFragment;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 27/05/2015.
 */

public class ScanDevicesActivity extends AppCompatActivity {

        // Stops scanning after 10 seconds.
        private static final long SCAN_PERIOD = LoonAndroid.demoMode? 4000:10000;
        public static final int REQUEST_ENABLE_BT = 27;
        public static final int REQUEST_ACCESS_LOCATION = 28;

        private Handler mHandler;
        private static final String TAG = "SCAN";
        private ListView sensorsLV;
        private ProgressBar scanPB;
        private TextView scanTV;
        private boolean discovering = false; //this is used only for demo mode
        private BluetoothLeScanner mLEScanner;
        private ScanSettings settings;
        private List<ScanFilter> filters;
        private FloatingActionButton fab;
        private BluetoothDeviceAdapter scanAdapter;
        private BluetoothAdapter mBluetoothAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Context context = this;
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
                public void onDeviceAdded(Device device) {
                    showSensorDialog(device);
                }

                @Override
                public void onDeviceIgnored(Device device) {
                    ignoreSensor(device);
                }
            });
            sensorsLV.setAdapter(scanAdapter);

            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setLogo(R.mipmap.ic_launcher);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_back_arrow);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            fab = (FloatingActionButton) this.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startScanning();
                }
            });

        }

    private Object getScanCallback(){
        if(Build.VERSION.SDK_INT >= 21) {
            return new ScanCallback() {
                @Override
                @TargetApi(21)
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice btDevice = result.getDevice();
                    processDevice(btDevice, result.getRssi());
                }

                @Override
                @TargetApi(21)
                public void onBatchScanResults(List<ScanResult> results) {
                    for (ScanResult sr : results) {
                        processDevice(sr.getDevice(), sr.getRssi());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    stopScanning();
                }
            };
        }else{
            return null;
        }
    }

    private Object mScanCallback = getScanCallback();

    private void processDevice(BluetoothDevice device, int rssi){

        String name = device.getName();
        if(TextUtils.isEmpty(name) || !name.equalsIgnoreCase("Sensor CS01")){
            return;
        }
        //i need to know if this device is already on our database
        DeviceDao sDao = new DeviceDao(ScanDevicesActivity.this);
        Device mDevice = sDao.findByMacAddress(device.getAddress());
        if(mDevice == null){
            mDevice = new Device(device);
            mDevice.setSignalStrength(rssi);
        }
        scanAdapter.add(mDevice);
        scanAdapter.notifyDataSetChanged();
    }

    @TargetApi(21)
    private void startScan( ){
        mLEScanner.startScan(filters, settings, (ScanCallback) mScanCallback );
    }

    public void startScanning(){
        scanAdapter.clear();

        //i need the location permissions enabled
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_LOCATION);
            stopScanning();
            return;
        }

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


        if(!LoonAndroid.demoMode) {
            if (Build.VERSION.SDK_INT < 21 && !mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
            } else {
                startScan();
            }
            Util.log(this, "Started Scan");
        }

        discovering = true;
        invalidateOptionsMenu();
        scanTV.setText(getString(R.string.scanning));
        scanPB.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
                invalidateOptionsMenu();
            }
        }, SCAN_PERIOD);
    }

    private void stopScanning(){
        fab.setVisibility(View.VISIBLE);
        Util.log(this, "Finished Scan");
        if(LoonAndroid.demoMode && !isFinishing() && discovering){
            discovering = false;
            scanAdapter.add(com.maws.loonandroid.models.Device.createFakeDevice());
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
        }else if (!isFinishing() && mBluetoothAdapter != null) {

            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.cancelDiscovery();
            } else {
                mLEScanner.stopScan((ScanCallback)mScanCallback);
            }
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
            StringBuilder devicesDisc = new StringBuilder("");
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short defaultShort = 0;
                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, defaultShort);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                devicesDisc.append(name + ", ");
                if(TextUtils.isEmpty(name) || !name.equalsIgnoreCase("Sensor CS01")){
                    return;
                }

                //i need to know if this device is already on our database
                DeviceDao sDao = new DeviceDao(ScanDevicesActivity.this);
                Device mDevice = sDao.findByMacAddress(device.getAddress());
                if(mDevice == null){
                    mDevice = new Device(device);
                    mDevice.setSignalStrength(rssi);
                }
                scanAdapter.add(mDevice);
                scanAdapter.notifyDataSetChanged();
            }
            Util.log(context, "Discovered device: " + devicesDisc.toString());
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if ((mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) && !LoonAndroid.demoMode) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
            startScanning();
        }

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
            case REQUEST_ACCESS_LOCATION:
                if (resultCode == RESULT_OK) {
                    // Start the scan again.
                    startScanning();
                }
                break;
        }
    }

    public void showSensorDialog(Device device){
        //for now, let's create a random device and add it to the dialog
        Context context = this;
        AddSensorDialogFragment newFragment = AddSensorDialogFragment.newInstance(device,context, new AddSensorDialogFragment.AddSensorDialogListener() {
            @Override
            public void onSensorAdded(Device sensor) {
                addSensor(sensor);
            }
        });
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void addSensor(Device device){
        device.setActive(true);
        saveSensor(device);
    }

    private void ignoreSensor(Device device){
        device.setActive(false);
        saveSensor(device);
    }

    private void saveSensor(Device device){
        DeviceDao sDao = new DeviceDao(this);
        sDao.create(device);
        scanAdapter.notifyDataSetChanged();
    }

}