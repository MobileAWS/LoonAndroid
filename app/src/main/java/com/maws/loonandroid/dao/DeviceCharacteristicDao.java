package com.maws.loonandroid.dao;

/**
 * Created by Andrexxjc on 10/05/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.models.DeviceCharacteristic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class DeviceCharacteristicDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblDeviceCharacteristic";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    private Context context;

    public DeviceCharacteristicDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT " + ")";

        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Getting single object
    public DeviceCharacteristic get(int id, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_NAME
                },
                KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)
                },
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        DeviceCharacteristic device = new DeviceCharacteristic();
        device.setId(cursor.getLong(0));
        device.setName(cursor.getString(1));
        db.close();
        cursor.close();

        // return object
        return device;
    }

    // Getting All objects
    public List<DeviceCharacteristic> getAll(SQLiteDatabase db) {

        List<DeviceCharacteristic> toReturnList = new ArrayList<DeviceCharacteristic>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                DeviceCharacteristic deviceCharacteristic = new DeviceCharacteristic();
                deviceCharacteristic.setId(cursor.getLong(0));
                deviceCharacteristic.setName(cursor.getString(1));
                toReturnList.add(deviceCharacteristic);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

}

