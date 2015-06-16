package com.maws.loonandroid.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.AlertHistoryListAdapter;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistorySensorActivity extends ActionBarActivity {
    private ListView listView ;
    private long sensorId;
    private AlertHistoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sensor);
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        sensorId = extras.getLong(MonitorActivity.MONITOR_ID, -1);
        listView = (ListView) findViewById(R.id.listAlarmHistory);
        listView.setAdapter(loadInformation(sensorId));

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

    private AlertHistoryListAdapter loadInformation (long sensorId) {
        AlertDao alertDao = new AlertDao(this);
        List<Alert> listAlert = alertDao.getAllForId(sensorId);
        if (listAlert != null && listAlert.size() > 0 ) {
            adapter= new AlertHistoryListAdapter(listAlert,this);
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
