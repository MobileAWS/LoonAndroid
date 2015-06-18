package com.maws.loonandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.SensorServiceListAdapter;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DeviceCharacteristicDao;
import com.maws.loonandroid.models.Device;

/**
 * Created by Andrexxjc on 15/05/2015.
 */
public class MonitorActivity extends ActionBarActivity implements  View.OnClickListener{

    private static final String TAG = "MONITOR";
    public static final String MONITOR_ID = "mId";
    public static final int CODE_RESULT = 10;

    private long sensorId;
    private TextView nameTV, codeTV;
    private Button viewHistory;
    private ImageView signalIV, batteryIV, checkIV;
    private ListView sensorServicesLV;
    private SensorServiceListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        sensorId = getIntent().getLongExtra(MONITOR_ID, -1);

        nameTV = (TextView) findViewById(R.id.nameTV);
        codeTV = (TextView) findViewById(R.id.codeTV);
        signalIV = (ImageView) findViewById(R.id.signalIV);
        batteryIV = (ImageView) findViewById(R.id.batteryIV);
        checkIV = (ImageView) findViewById(R.id.checkIV);
        sensorServicesLV = (ListView) findViewById(R.id.sensorServicesLV);
        viewHistory = (Button) findViewById(R.id.historyBtn);
        viewHistory.setOnClickListener(this);
        loadInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            sensorId = data.getExtras().getLong(MONITOR_ID);
        }
    }


    private void loadInformation(){
        DeviceDao sDao = new DeviceDao(this);
        Device currentDevice = sDao.get(sensorId);

        if(currentDevice != null) {
            DeviceCharacteristicDao ssDao = new DeviceCharacteristicDao(this);
            nameTV.setText(TextUtils.isEmpty(currentDevice.getDescription())? currentDevice.getName(): currentDevice.getDescription());
            codeTV.setText(currentDevice.getName());

            /*List<DeviceCharacteristic> services = ssDao.getAllBySensorId(currentDevice.getId(), lDao.getReadableDatabase());
            adapter = new SensorServiceListAdapter(this,services);
            sensorServicesLV.setAdapter(adapter);*/
        }
    }

    @Override
    public void onClick(View v) {
        if(v == viewHistory){
            Bundle bundle = new Bundle();
            Intent intent = new Intent(MonitorActivity.this, HistorySensorActivity.class);

            sensorId = getIntent().getLongExtra(MONITOR_ID, -1);
            bundle.putLong(MonitorActivity.MONITOR_ID, sensorId);

            intent.putExtras(bundle);
            startActivityForResult(intent, CODE_RESULT);

        }
    }
}
