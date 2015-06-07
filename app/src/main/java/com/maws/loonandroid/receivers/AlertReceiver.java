package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorService;
import com.maws.loonandroid.util.Util;

import java.util.Date;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long sensorId = intent.getLongExtra("sensorId", 0);
        int serviceId = intent.getIntExtra("serviceId", -1);
        boolean isOn = intent.getBooleanExtra("isOn", true);
        long alarmDateMillis = intent.getLongExtra("dateMillis", 0);

        SensorDao sDao = new SensorDao(context);
        Sensor sensor = sDao.get(sensorId);

        if( sensorId > 0 && serviceId > -1 && alarmDateMillis >= 0 && sensor != null ) {

            //when i receive an alarm, i want to do 2 things: Save it to the db and show a notification

            //let's save it to db
            Alert alert = new Alert();
            alert.setSensorId(sensorId);
            alert.setSensorServiceId(serviceId);
            alert.setIsOn(isOn);
            alert.setAlertDate(new Date(alarmDateMillis));

            AlertDao aDao = new AlertDao(context);
            aDao.create(alert);

            //let's show the notification
            String title = TextUtils.isEmpty(sensor.getDescription())? sensor.getName() : sensor.getDescription();
            String message = isOn? String.format(context.getString(R.string.push_notification_message_alert_on), context.getString(SensorService.serviceNames.get(serviceId))) : String.format(context.getString(R.string.push_notification_message_alert_off), context.getString(SensorService.serviceNames.get(serviceId)));
            Util.generateNotification(context, title, message);
        }
    }

}
