package com.maws.loonandroid.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistorySensorActivity extends ActionBarActivity {
    ListView listView ;
    long sensorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_sensor);
        Intent i = this.getIntent();
        Bundle extras =  i.getExtras();
        sensorId = extras.getLong(MonitorActivity.MONITOR_ID, -1);
        listView = (ListView) findViewById(R.id.listAlarmHistory);
        List<String> values =  loadInformation(sensorId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values.toArray(new String[values.size()]));
        // Assign adapter to ListView
        listView.setAdapter(adapter);


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

    private List<String> loadInformation (long sensorId) {
        List<String> listAlertHistorical = new ArrayList<>();
        AlertDao alertDao = new AlertDao(this);
        List<Alert> listAlert = alertDao.getAll4Id(sensorId);
        if (listAlert != null && listAlert.size() > 0 ) {
            for(Alert alert:listAlert){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                listAlertHistorical.add("Sensor: "+alert.getSensorId() +
                    "\n Alert Date: " + sdf.format(alert.getAlertDate()) +
                    "\n Dismissed Date: " + sdf.format(alert.getDismissedDate())
                    +"\n Time Elapsed: " + Util.totalTimeDismissed(alert.getTotalTimeAlarm()));
            }
        }
        return listAlertHistorical;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id > 0) {
            goBackInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
