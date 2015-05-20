package com.maws.loonandroid.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorService;
import com.maws.loonandroid.models.User;

import java.util.Date;

/**
 * Created by Andrexxjc on 04/05/2015.
 */
public class Util {

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static void generateAlarm(Context context, Sensor sensor, SensorService service){
        String title = sensor.getName().toUpperCase();
        String message = String.format(context.getString(R.string.alarm_notification_text), service.getName());

        Intent intent = new Intent();
        intent.setAction("com.maws.loonandroid.alert");
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("sensor", sensor.getId());
        intent.putExtra("service", service.getId());
        intent.putExtra("dateMillis", new Date().getTime());
        context.sendBroadcast(intent);
    }

    public static void generateNotification(Context context, String title, String message) {

        int id = new Double(Math.random() * 100).intValue();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setStyle(
                        new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        mBuilder.setContentIntent(intent);

        Notification notification = mBuilder.build();
        //notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(id, notification);
    }



}
