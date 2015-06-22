package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.contentproviders.DevicePropertyContentProvider;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 10/05/2015.
 */
public class DevicePropertyDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblDeviceProperty";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_DEVICE_ID = "deviceID";
    public static final String KEY_CUSTOMER_ID = "customerID";
    public static final String KEY_SITE_ID = "siteID";
    public static final String KEY_PROPERTY_ID = "devicePropertyID";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_VALUE = "value";
    public static final String KEY_DISMISSED_DATE = "dismissedAt";
    public static final String KEY_TOTAL_TIME_ALARM = "totalTimeAlarm";


    private Context context;

    public DevicePropertyDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_DEVICE_ID + " INT," +
                KEY_PROPERTY_ID + " INT," +
                KEY_CREATED_AT + " INT," +
                KEY_VALUE + " TEXT," +
                KEY_DISMISSED_DATE + " INT," +
                KEY_TOTAL_TIME_ALARM + " INT," +
                KEY_CUSTOMER_ID + " INT," +
                KEY_SITE_ID + " INT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

   //TODO borrar cuando se pruebe // Adding new object
    public void create(DeviceProperty deviceProperty) {

        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_ID, deviceProperty.getDeviceId());
        values.put(KEY_PROPERTY_ID, deviceProperty.getPropertyId());
        values.put(KEY_CUSTOMER_ID, deviceProperty.getCostumerId());
        values.put(KEY_SITE_ID, deviceProperty.getSiteId());
        values.put(KEY_CREATED_AT, deviceProperty.getCreatedAt() == null ? null : deviceProperty.getCreatedAt().getTime());
        values.put(KEY_VALUE, deviceProperty.getValue());
        context.getContentResolver().insert(DevicePropertyContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public DeviceProperty get(long id) {

        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                    KEY_ID,
                    KEY_DEVICE_ID,
                    KEY_PROPERTY_ID,
                    KEY_CREATED_AT,
                    KEY_VALUE,
                    KEY_DISMISSED_DATE,
                    KEY_TOTAL_TIME_ALARM,
                    KEY_CUSTOMER_ID,
                    KEY_SITE_ID
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        DeviceProperty deviceProperty = new DeviceProperty();
        deviceProperty.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        deviceProperty.setDeviceId(cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_ID)));
        deviceProperty.setPropertyId(cursor.getLong(cursor.getColumnIndex(KEY_PROPERTY_ID)));
        deviceProperty.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))));
        deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
        deviceProperty.setDismissedAt(cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE)) ? null : new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
        deviceProperty.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
        deviceProperty.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
        deviceProperty.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
        cursor.close();

        // return object
        return deviceProperty;
    }

    public List<DeviceProperty> getAllByDeviceId(long id) {
        List<DeviceProperty> listDeviceProperties = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_PROPERTY_ID,
                        KEY_CREATED_AT,
                        KEY_VALUE,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                KEY_DEVICE_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do  {
                DeviceProperty deviceProperty = new DeviceProperty();
                deviceProperty.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                deviceProperty.setDeviceId(cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_ID)));
                deviceProperty.setPropertyId(cursor.getLong(cursor.getColumnIndex(KEY_PROPERTY_ID)));
                deviceProperty.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))));
                deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
                deviceProperty.setDismissedAt(cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE)) ? null : new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
                deviceProperty.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
                deviceProperty.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                deviceProperty.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
                listDeviceProperties.add(deviceProperty);
            } while(cursor.moveToNext());
        }else{
            return null;
        }
        cursor.close();
        // return list object
        return listDeviceProperties;
    }

    // Getting All objects
    public List<DeviceProperty> getAll() {

        List<DeviceProperty> toReturnList = new ArrayList<DeviceProperty>();
        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_PROPERTY_ID,
                        KEY_CREATED_AT,
                        KEY_VALUE,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                null,
                null,
                null
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                DeviceProperty deviceProperty = new DeviceProperty();
                deviceProperty.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                deviceProperty.setDeviceId(cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_ID)));
                deviceProperty.setPropertyId(cursor.getLong(cursor.getColumnIndex(KEY_PROPERTY_ID)));
                deviceProperty.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))));
                deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
                deviceProperty.setDismissedAt(cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE)) ? null : new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
                deviceProperty.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
                deviceProperty.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
                deviceProperty.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
                toReturnList.add(deviceProperty);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public void dismiss(DeviceProperty deviceProperty) {

        ContentValues values = new ContentValues();
        long dismissedDate = new Date().getTime();
        values.put(KEY_DISMISSED_DATE, dismissedDate);
        long  totalTimeAlarm = Util.subtract2Dates(deviceProperty.getCreatedAt(), new Date(dismissedDate));
        values.put(KEY_TOTAL_TIME_ALARM, totalTimeAlarm);

        context.getContentResolver().update(
                DevicePropertyContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(deviceProperty.getId())}
        );
    }

    // Deleting single object
    public void delete(DeviceProperty deviceProperty) {
        context.getContentResolver().delete(
                DevicePropertyContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[]{String.valueOf(deviceProperty.getId())}
        );
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }

    public DeviceProperty getLastAlertForDevice(long deviceId){

        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_DEVICE_ID,
                        KEY_PROPERTY_ID,
                        KEY_CREATED_AT,
                        KEY_VALUE,
                        KEY_DISMISSED_DATE,
                        KEY_TOTAL_TIME_ALARM,
                        KEY_CUSTOMER_ID,
                        KEY_SITE_ID
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(deviceId)
                },
                "ORDER BY " + KEY_ID + " DESC"
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        DeviceProperty deviceProperty = new DeviceProperty();
        deviceProperty.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        deviceProperty.setDeviceId(cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_ID)));
        deviceProperty.setPropertyId(cursor.getLong(cursor.getColumnIndex(KEY_PROPERTY_ID)));
        deviceProperty.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))));
        deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
        deviceProperty.setDismissedAt(cursor.isNull(cursor.getColumnIndex(KEY_DISMISSED_DATE)) ? null : new Date(cursor.getLong(cursor.getColumnIndex(KEY_DISMISSED_DATE))));
        deviceProperty.setTotalTimeAlarm(cursor.getLong(cursor.getColumnIndex(KEY_TOTAL_TIME_ALARM)));
        deviceProperty.setCostumerId(cursor.getLong(cursor.getColumnIndex(KEY_CUSTOMER_ID)));
        deviceProperty.setSiteId(cursor.getLong(cursor.getColumnIndex(KEY_SITE_ID)));
        cursor.close();

        // return object
        return deviceProperty;
    }

}
