package com.maws.loonandroid.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.dao.DeviceEnabledPropertyDao;
import com.maws.loonandroid.dao.LogDao;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceEnabledProperty;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.models.Site;
import com.maws.loonandroid.models.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 04/05/2015.
 */
public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM, h:mm a");
    public static final SimpleDateFormat longDateFormat = new SimpleDateFormat("EEE dd MMM");
    public static final SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("hh:mm:ss a");
    public static final SimpleDateFormat logDateFormat = new SimpleDateFormat("EEE dd MMM, hh:mm:ss a");
    public static final String EMAIL_PREFERENCE = "email";
    public static final String CUSTOMER_ID_PREFERENCE = "customerId";
    public static final String SITE_ID_PREFERENCE = "siteId";

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

        //before generating an alarm, i need to make sure the property is not disabled

        DeviceEnabledPropertyDao depDao = new DeviceEnabledPropertyDao(context);
        DeviceEnabledProperty deviceEnabledProperty =  depDao.findByDevicePropertyUser(dProperty.getDeviceId());
        boolean isEnabled = deviceEnabledProperty == null?true:deviceEnabledProperty.isEnabled();

        if(isEnabled) {
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
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
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
        //TODO implemetation if is online
        //User user =User.getCurrent(context);
        //if(user != null && user.getToken()!= null && !user.getToken().isEmpty()){
          //  return  true;
        //}
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
    public static long getCustomerId(Context context){
        Customer customer = Customer.getCurrent(context);
        return customer.getId();
    }
    public static long getSiteId(Context context){
        return Site.getCurrent(context).getId();
    }
    public static long getUserId(Context context) { return 0; }//User.getCurrent(context).getId(); }

    public static void setUpSignalView(Context context, ImageView signalIV, Device device){int signal = device.getSignalStrength();
        if(signal <= -90){
            signalIV.setImageResource(R.drawable.wifi1);
            signalIV.setColorFilter( context.getResources().getColor(R.color.dark_orange));
        }else if(signal < -65 ){
            signalIV.setImageResource(R.drawable.wifi2);
            signalIV.setColorFilter(context.getResources().getColor(R.color.toast_warning_border));
        }else if(signal < 0){
            signalIV.setImageResource(R.drawable.wifi3);
            signalIV.setColorFilter(context.getResources().getColor(R.color.green));
        }else{
            signalIV.setImageResource(R.drawable.wifi1);
            signalIV.setColorFilter(context.getResources().getColor(R.color.light_gray));
        }
    }

    public static void setUpBatteryView(Context context, ImageView batteryIV, Device device){
        int battery = device.getBatteryStatus();
        if(battery >= 80){
            batteryIV.setImageResource(R.drawable.battery4);
            batteryIV.setColorFilter(context.getResources().getColor(R.color.green));
        }else if(battery > 50 ){
            batteryIV.setImageResource(R.drawable.battery3);
            batteryIV.setColorFilter(context.getResources().getColor(R.color.green));
        }else if(battery > 25 ){
            batteryIV.setImageResource(R.drawable.battery2);
            batteryIV.setColorFilter(context.getResources().getColor(R.color.toast_warning_border));
        }else{
            batteryIV.setImageResource(R.drawable.battery1);
            batteryIV.setColorFilter(context.getResources().getColor(R.color.dark_orange));
        }
    }

    public static void log(Context context, String message){
        LogDao lDao = new LogDao(context);
        lDao.create(message);
    }

    public static double fahrenheitToCelsius(double value) {
        double toReturn = (value - 32) * 5/9;
        return to2Decimal(toReturn);
    }

    public static double celsiusToFahrenheit(double value){
        double toReturn = (value * 9/5) + 32;
        return to2Decimal(toReturn);
    }

    public static double to2Decimal(double value){
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static List<Property> convertArrayToList (Property[] arrayPropertie ){
        List<Property> propertyList = new ArrayList<>();
        for(int i = 0; i< arrayPropertie.length; i++){
            propertyList.add(arrayPropertie[i]);
        }
        return propertyList;
    }

    public static void setLoginInit(String email, String siteId, String customerId,Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Util.EMAIL_PREFERENCE,email);
        editor.putString(Util.CUSTOMER_ID_PREFERENCE,customerId);
        editor.putString(Util.SITE_ID_PREFERENCE,siteId);
        editor.apply();
    }

    public static int dpToPx(Context context, int dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }
}
