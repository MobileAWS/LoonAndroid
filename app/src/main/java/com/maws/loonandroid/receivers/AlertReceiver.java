package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.DeviceService;
import com.maws.loonandroid.models.Site;
import com.maws.loonandroid.util.Util;

import java.util.Date;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long deviceId = intent.getLongExtra("deviceId", 0);
        int serviceId = intent.getIntExtra("serviceId", -1);
        boolean isOn = intent.getBooleanExtra("isOn", true);
        long alarmDateMillis = intent.getLongExtra("dateMillis", 0);

        DeviceDao sDao = new DeviceDao(context);
        Device device = sDao.get(deviceId);

        if( deviceId > 0 && serviceId > -1 && alarmDateMillis >= 0 && device != null ) {

            //when i receive an alarm, i want to do 2 things: Save it to the db and show a notification

            //let's save it to db
            Alert alert = new Alert();
            alert.setDeviceId(deviceId);
            alert.setDeviceServiceId(serviceId);
            alert.setIsOn(isOn);
            alert.setAlertDate(new Date(alarmDateMillis));
            alert.setCostumerId(Customer.getCurrent(context).getId());
            alert.setSiteId(Site.getCurrent(context).getId());

            AlertDao aDao = new AlertDao(context);
            aDao.addElement(alert);
            DevicePropertyDao devicePropertyDao = new DevicePropertyDao(context);
            DeviceProperty deviceProperty =devicePropertyDao.getElementForID(serviceId);

            //let's show the notification
            String title = TextUtils.isEmpty(device.getDescription())? device.getName() : device.getDescription();
            String message = isOn? String.format(context.getString(R.string.push_notification_message_alert_on), deviceProperty.getValue()) : String.format(context.getString(R.string.push_notification_message_alert_off), deviceProperty.getValue());
            Util.generateNotification(context, title, message);
        }
    }

}
