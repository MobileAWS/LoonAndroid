package com.maws.loonandroid.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.PropertyAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DeviceCharacteristicDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;

/**
 * Created by Andrexxjc on 15/05/2015.
 */
public class MonitorActivity extends ActionBarActivity implements  View.OnClickListener{

    private static final String TAG = "MONITOR";
    public static final String MONITOR_ID = "mId";
    public static final int CODE_RESULT = 10;

    private long deviceId;
    private TextView nameTV, serialTV, versionTV, temperatureTV;
    private Button viewHistory,disconnectBtn,renameDeviceBtn;
    private ImageView signalIV, batteryIV;
    private ListView propertiesLV;
    private PropertyAdapter adapter;
    private Device currentDevice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        deviceId = getIntent().getLongExtra(MONITOR_ID, -1);
        nameTV = (TextView) findViewById(R.id.nameTV);
        serialTV = (TextView) findViewById(R.id.serialTV);
        versionTV = (TextView) findViewById(R.id.versionTV);
        temperatureTV = (TextView) findViewById(R.id.temperatureTV);
        signalIV = (ImageView) findViewById(R.id.signalIV);
        batteryIV = (ImageView) findViewById(R.id.batteryIV);
        propertiesLV = (ListView) findViewById(R.id.propertiesLV);
        viewHistory = (Button) findViewById(R.id.historyBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        renameDeviceBtn = (Button) findViewById(R.id.renameDeviceBtn);
        viewHistory.setOnClickListener(this);
        disconnectBtn.setOnClickListener(this);
        renameDeviceBtn.setOnClickListener(this);
        loadInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            deviceId = data.getExtras().getLong(MONITOR_ID);
        }
    }

    private void loadInformation(){
        DeviceDao sDao = new DeviceDao(this);
        currentDevice = sDao.get(deviceId);

        if(currentDevice != null) {
            this.setTitle(TextUtils.isEmpty(currentDevice.getDescription()) ? currentDevice.getName() : currentDevice.getDescription());
            DeviceCharacteristicDao ssDao = new DeviceCharacteristicDao(this);
            nameTV.setText( currentDevice.getName() );
            serialTV.setText(String.format(getString(R.string.device_serial), currentDevice.getHardwareId()));
            versionTV.setText( String.format(getString(R.string.device_version), currentDevice.getFirmwareVersion(), currentDevice.getHardwareVersion()) );
            temperatureTV.setText( String.format(getString(R.string.device_temperature), Util.celsiusToFahrenheit( currentDevice.getTemperature() ), currentDevice.getTemperature()) );
            Util.setUpBatteryView(this, batteryIV, currentDevice );
            Util.setUpSignalView(this, signalIV, currentDevice);
            //i need to create a device enabled and delay controls for each property
            adapter = new PropertyAdapter(this, Property.defaultProperties, currentDevice);
            propertiesLV.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == viewHistory){
            Bundle bundle = new Bundle();
            Intent intent = new Intent(MonitorActivity.this, HistorySensorActivity.class);

            deviceId = getIntent().getLongExtra(MONITOR_ID, -1);
            bundle.putLong(MonitorActivity.MONITOR_ID, deviceId);

            intent.putExtras(bundle);
            startActivityForResult(intent, CODE_RESULT);

        }
        if(v == disconnectBtn && currentDevice != null){
            BLEService.getInstance().disconnect(currentDevice.getMacAddress());
        }
        if(v == renameDeviceBtn) {
            DeviceDao deviceDao = new DeviceDao(this);
            Device device = deviceDao.get(deviceId);
            changeNameDevice(this, device, deviceDao);

        }
    }

    private boolean deleteAllAboutDevice(final long deviceId){
        final boolean result = false;
        final DeviceDao dDao = new DeviceDao(this);
        final DevicePropertyDao dpDao = new DevicePropertyDao(this);
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Device")
                    .setMessage("Do you really want to delete this device?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Device currentDevice = dDao.get(deviceId);
                            dpDao.deleteForDeviceId(currentDevice.getId());
                            dDao.delete(currentDevice);
                            Intent intent = new Intent(MonitorActivity.this ,MainActivity.class );
                            startActivityForResult(intent, CODE_RESULT);

                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }catch (Exception e) {

        }
        return result;
    }
    private void changeNameDevice(final Context context,final  Device device, final DeviceDao deviceDao){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        String oldNameDevice = "";
        if(device.getDescription() != null & !device.getDescription().isEmpty()){
            oldNameDevice = device.getDescription();
        }else {
            oldNameDevice = device.getName();
        }

        alertDialog.setTitle(getString(R.string.tittle_dialogbox_rename)+" '"+ oldNameDevice+"'");
        alertDialog.setMessage(getString(R.string.message_dialogbox_rename));

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(100, 1, 1, 1);
        input.setPadding(30, 10, 10, 20);
        //input.setLayoutParams(lp);
        alertDialog.setView(input);
       //

        alertDialog.setPositiveButton("ACCEPT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(device.getDescription() != null & !device.getDescription().isEmpty()){
                            device.setDescription(input.getText().toString());
                        }else {
                            device.setName(input.getText().toString());
                        }
                        deviceDao.update(device);
                        loadInformation();
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
