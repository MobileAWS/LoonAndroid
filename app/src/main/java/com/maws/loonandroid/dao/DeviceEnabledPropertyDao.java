package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.maws.loonandroid.contentproviders.DeviceEnabledPropertyContentProvider;
import com.maws.loonandroid.models.DeviceEnabledProperty;

/**
 * Created by Andrexxjc on 24/06/2015.
 */
public class DeviceEnabledPropertyDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblDeviceEnabledProperty";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_PROPERTY_ID = "propertyId";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_DELAY = "delay";

    private Context context;

    public DeviceEnabledPropertyDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_ID + " INTEGER," +
                KEY_DEVICE_ID + " INTEGER," +
                KEY_PROPERTY_ID + " INTEGER," +
                KEY_ENABLED + " TINYINT," +
                KEY_DELAY + " INTEGER )";
        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    // Adding new object
    public void create(DeviceEnabledProperty dp) {

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, dp.getUserId());
        values.put(KEY_DEVICE_ID, dp.getDeviceId());
        values.put(KEY_PROPERTY_ID, dp.getPropertyId());
        values.put(KEY_ENABLED, dp.isEnabled()?1:0);
        values.put(KEY_DELAY, dp.getDelay());

        Uri uri = context.getContentResolver().insert(DeviceEnabledPropertyContentProvider.CONTENT_URI, values);
        long deviceId = Long.valueOf(uri.getLastPathSegment());
        dp.setId(deviceId);
    }

    // Updating single object
    public long update(DeviceEnabledProperty dp) {

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, dp.getUserId());
        values.put(KEY_DEVICE_ID, dp.getDeviceId());
        values.put(KEY_PROPERTY_ID, dp.getPropertyId());
        values.put(KEY_ENABLED, dp.isEnabled()?1:0);
        values.put(KEY_DELAY, dp.getDelay());

        context.getContentResolver().update(DeviceEnabledPropertyContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(dp.getId())
                }
        );
        return dp.getId();
    }

    // Getting single object
    public DeviceEnabledProperty findByDevicePropertyUser(long deviceId, long propertyId, long userId) {

        Cursor cursor = context.getContentResolver().query(DeviceEnabledPropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_USER_ID,
                        KEY_DEVICE_ID,
                        KEY_PROPERTY_ID,
                        KEY_ENABLED,
                        KEY_DELAY
                },
                KEY_DEVICE_ID + "=? AND " + KEY_USER_ID + "=? AND "  + KEY_PROPERTY_ID + "=?",
                new String[]{
                        String.valueOf(deviceId),
                        String.valueOf(userId),
                        String.valueOf(propertyId)
                },
                null
        );

        if (cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        DeviceEnabledProperty dp = new DeviceEnabledProperty();
        dp.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        dp.setIsEnabled(cursor.getInt(cursor.getColumnIndex(KEY_ENABLED)) == 1);
        dp.setDelay(cursor.getInt(cursor.getColumnIndex(KEY_DELAY)));
        dp.setDeviceId(cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_ID)));
        dp.setUserId(cursor.getLong(cursor.getColumnIndex(KEY_USER_ID)));
        dp.setPropertyId(cursor.getLong(cursor.getColumnIndex(KEY_PROPERTY_ID)));
        cursor.close();

        // return object
        return dp;
    }





}
