package com.maws.loonandroid.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andrexxjc on 04/05/2015.
 */
public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM, h:mm a");
    public static final SimpleDateFormat longDateFormat = new SimpleDateFormat("EEE dd MMM");
    public static final SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("hh:mm:ss a");

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

    public static void generateAlarm(Context context, DeviceProperty dProperty){

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        intent.setAction("com.maws.loonandroid.deviceproperty");
        bundle.putLong("deviceId", dProperty.getDeviceId());
        bundle.putLong("propertyId", dProperty.getPropertyId());
        bundle.putString("value", dProperty.getValue());
        bundle.putLong("dateMillis", new Date().getTime());
        intent.putExtras(bundle);
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

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static long  subtract2Dates(Date date1, Date date2) {

        long difference = (date2.getTime() - date1.getTime());
        return difference;
    }

    public static  String totalTimeDismissed(long totalMilSecs){
        long totalSecs = totalMilSecs / 1000;
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }
    public static  boolean isLoginOnline(Context context){
        User user =User.getCurrent(context);
        if(user != null && user.getToken()!= null && !user.getToken().isEmpty()){
            return  true;
        }
        return false;
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
