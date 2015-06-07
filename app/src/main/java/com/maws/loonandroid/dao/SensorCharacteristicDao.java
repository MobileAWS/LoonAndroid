package com.maws.loonandroid.dao;

/**
 * Created by Andrexxjc on 10/05/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.models.SensorCharacteristic;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class SensorCharacteristicDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblSensorCharacteristic";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";

    private Context context;

    public SensorCharacteristicDao(Context context) {
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
    public SensorCharacteristic get(int id, SQLiteDatabase db) {

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

        SensorCharacteristic sensor = new SensorCharacteristic();
        sensor.setId(cursor.getLong(0));
        sensor.setName(cursor.getString(1));
        db.close();
        cursor.close();

        // return object
        return sensor;
    }

    // Getting All objects
    public List<SensorCharacteristic> getAll(SQLiteDatabase db) {

        List<SensorCharacteristic> toReturnList = new ArrayList<SensorCharacteristic>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                SensorCharacteristic sensorCharacteristic = new SensorCharacteristic();
                sensorCharacteristic.setId(cursor.getLong(0));
                sensorCharacteristic.setName(cursor.getString(1));
                toReturnList.add(sensorCharacteristic);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

}

