package com.maws.loonandroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.maws.loonandroid.R;
import com.maws.loonandroid.contentproviders.DevicePropertyContentProvider;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.dao.DeviceDao;
import com.maws.loonandroid.dao.UserDao;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.models.Site;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.util.Util;
import java.util.Date;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
public class DevicePropertyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        long deviceId = bundle.getLong("deviceId");
        long propertyId = bundle.getLong("propertyId");
        String value = bundle.getString("value");
        long alarmDateMillis = bundle.getLong("dateMillis");

        //ignore the alarm if it has no ON or OFF text
        Property thisProp = Property.getDefaultProperty(propertyId);
        String propertyMessage = "";
        if(value.equalsIgnoreCase("on") ){
            propertyMessage = context.getString(thisProp.getOnTextId());
        }else{
            propertyMessage = context.getString(thisProp.getOffTextId());
        }
        if(TextUtils.isEmpty(propertyMessage)){
            context.getContentResolver().notifyChange(DevicePropertyContentProvider.CONTENT_URI, null);
            return;
        }

        DeviceDao sDao = new DeviceDao(context);
        Device device = sDao.get(deviceId);
        if( deviceId > 0 && propertyId > -1 && alarmDateMillis >= 0 && device != null ) {

            //when i receive an alarm, i want to do 2 things: Save it to the db and show a notification

            //let's save it to db
            DeviceProperty dProperty = new DeviceProperty();
            dProperty.setDeviceId(deviceId);
            dProperty.setPropertyId(propertyId);

            dProperty.setValue(value);
            dProperty.setCreatedAt(new Date(alarmDateMillis));

            DevicePropertyDao aDao = new DevicePropertyDao(context);
            aDao.create(dProperty);

            //let's show the notification
            String title = TextUtils.isEmpty(device.getDescription())? device.getName() : device.getDescription();
            Util.generateNotification(context, title, propertyMessage);
        }
    }

}
