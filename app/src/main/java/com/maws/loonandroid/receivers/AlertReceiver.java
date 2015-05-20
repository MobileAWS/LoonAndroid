package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.util.Util;

import java.util.Date;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        long sensorServiceId = intent.getLongExtra("service", 0);
        long alarmDateMillis = intent.getLongExtra("dateMillis", 0);

        if(sensorServiceId > 0) {

            //when i receive an alarm, i want to do 2 things: Save it to the db and show a notification

            //let's save it to db
            Alert alert = new Alert();
            alert.setSensorServiceId(sensorServiceId);
            alert.setAlertDate(new Date(alarmDateMillis));

            LoonMedicalDao lDao = new LoonMedicalDao(context);
            AlertDao aDao = new AlertDao(context);
            aDao.create(alert);

            //let's show the notification
            Util.generateNotification(context, title, message);
        }
    }

}
