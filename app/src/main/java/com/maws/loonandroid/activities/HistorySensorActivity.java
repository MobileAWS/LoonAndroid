package com.maws.loonandroid.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.DevicePropertyHistoryListAdapter;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.DeviceProperty;

import java.util.List;

public class HistorySensorActivity extends ActionBarActivity {
    private ListView listView ;
    private long deviceId;
    private DevicePropertyHistoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sensor);
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        deviceId = extras.getLong(MonitorActivity.MONITOR_ID, -1);
        listView = (ListView) findViewById(R.id.listAlarmHistory);
        listView.setAdapter(loadInformation(deviceId));

    }

    @Override
    public void onBackPressed() {
        goBackInfo();
        return;
    }

    private void  goBackInfo(){
        Intent returnIntent = new Intent();
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        returnIntent.putExtras(extras);
        this.setResult(RESULT_OK, returnIntent);
        finish();
    }

    private DevicePropertyHistoryListAdapter loadInformation (long deviceId) {
        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(this);
        List<DeviceProperty> listDeviceProperty = devicePropertyDao.getAllByDeviceId(deviceId);
        if (listDeviceProperty.size() > 0 ) {
            adapter= new DevicePropertyHistoryListAdapter(listDeviceProperty,this);
        }
        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            goBackInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
