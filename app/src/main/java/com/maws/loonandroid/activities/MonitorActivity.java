package com.maws.loonandroid.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.SensorServiceListAdapter;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.dao.SensorServiceDao;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorService;

import java.util.List;

/**
 * Created by Andrexxjc on 15/05/2015.
 */
public class MonitorActivity extends ActionBarActivity {

    private static final String TAG = "MONITOR";
    public static final String MONITOR_ID = "mId";

    private long sensorId;
    private TextView nameTV, codeTV;
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
        loadInformation();
    }

    private void loadInformation(){
        LoonMedicalDao lDao = new LoonMedicalDao(this);
        SensorDao sDao = new SensorDao(this);

        Sensor currentSensor = sDao.get(sensorId, lDao.getReadableDatabase());
        if(currentSensor != null) {
            SensorServiceDao ssDao = new SensorServiceDao(this);
            nameTV.setText(currentSensor.getName());
            codeTV.setText(currentSensor.getSerial());

            List<SensorService> services = ssDao.getAllBySensorId(currentSensor.getId(), lDao.getReadableDatabase());
            adapter = new SensorServiceListAdapter(this,services);
            sensorServicesLV.setAdapter(adapter);
        }
    }
}
