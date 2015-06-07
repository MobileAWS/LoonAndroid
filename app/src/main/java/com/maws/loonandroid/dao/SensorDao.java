package com.maws.loonandroid.dao;

import java.util.ArrayList;
import java.util.List;
import com.maws.loonandroid.contentproviders.SensorContentProvider;
import com.maws.loonandroid.models.Sensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class SensorDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblSensor";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CODE = "code";
    public static final String KEY_SERIAL = "serial";
    public static final String KEY_VERSION = "version";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MAC_ADDRESS = "mac";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_CONNECTED = "connected";

    private Context context;

    public SensorDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_CODE + " TEXT," +
                KEY_SERIAL + " TEXT," +
                KEY_VERSION + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_ACTIVE + " TINYINT," +
                KEY_MAC_ADDRESS + " TEXT," +
                KEY_CONNECTED + " TINYINT" + ")";

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
    public void create(Sensor sensor) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_CODE, sensor.getCode());
        values.put(KEY_SERIAL, sensor.getSerial());
        values.put(KEY_VERSION, sensor.getVersion());
        values.put(KEY_DESCRIPTION, sensor.getDescription());
        values.put(KEY_MAC_ADDRESS, sensor.getMacAddress());
        values.put(KEY_ACTIVE, sensor.isActive()?1:0);
        values.put(KEY_CONNECTED, sensor.isConnected() ? 1 : 0);
        Uri uri = context.getContentResolver().insert(SensorContentProvider.CONTENT_URI, values);
        long sensorId = Long.valueOf(uri.getLastPathSegment());
        sensor.setId(sensorId);
    }

    // Getting single object
    public Sensor get(long id) {

        Cursor cursor = context.getContentResolver().query(SensorContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_SERIAL,
                        KEY_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null
        );

        if (cursor != null  && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Sensor sensor = new Sensor();
        sensor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        sensor.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        sensor.setSerial(cursor.getString(cursor.getColumnIndex(KEY_SERIAL)));
        sensor.setVersion(cursor.getString(cursor.getColumnIndex(KEY_VERSION)));
        sensor.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        sensor.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
        sensor.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
        sensor.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
        cursor.close();

        // return object
        return sensor;
    }

    // Getting single object
    public Sensor findByMacAddress(String macAddress) {

        Cursor cursor = context.getContentResolver().query(SensorContentProvider.CONTENT_URI,
            new String[]{
                KEY_ID,
                KEY_NAME,
                KEY_CODE,
                KEY_SERIAL,
                KEY_VERSION,
                KEY_DESCRIPTION,
                KEY_MAC_ADDRESS,
                KEY_ACTIVE,
                KEY_CONNECTED
            },
            KEY_MAC_ADDRESS + "=?",
                new String[]{
                        macAddress
            },
            null
        );

        if (cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Sensor sensor = new Sensor();
        sensor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        sensor.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        sensor.setSerial(cursor.getString(cursor.getColumnIndex(KEY_SERIAL)));
        sensor.setVersion(cursor.getString(cursor.getColumnIndex(KEY_VERSION)));
        sensor.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        sensor.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
        sensor.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
        sensor.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
        cursor.close();

        // return object
        return sensor;
    }

    // Getting All objects
    public List<Sensor> getAll() {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(SensorContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_SERIAL,
                        KEY_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED
                },
                null,
                null,
                KEY_NAME
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                sensor.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                sensor.setSerial(cursor.getString(cursor.getColumnIndex(KEY_SERIAL)));
                sensor.setVersion(cursor.getString(cursor.getColumnIndex(KEY_VERSION)));
                sensor.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                sensor.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                sensor.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                sensor.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Sensor> getAllActive() {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(SensorContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_SERIAL,
                        KEY_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED
                },
                KEY_ACTIVE + "=?",
                new String[]{
                        "1"
                },
                KEY_NAME
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                sensor.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                sensor.setSerial(cursor.getString(cursor.getColumnIndex(KEY_SERIAL)));
                sensor.setVersion(cursor.getString(cursor.getColumnIndex(KEY_VERSION)));
                sensor.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                sensor.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                sensor.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                sensor.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Sensor> getAllInactive() {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        Cursor cursor = context.getContentResolver().query(SensorContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_SERIAL,
                        KEY_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE,
                        KEY_CONNECTED
                },
                KEY_ACTIVE + "=?",
                new String[]{
                        "0"
                },
                KEY_NAME
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                sensor.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                sensor.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                sensor.setSerial(cursor.getString(cursor.getColumnIndex(KEY_SERIAL)));
                sensor.setVersion(cursor.getString(cursor.getColumnIndex(KEY_VERSION)));
                sensor.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                sensor.setActive(cursor.getInt(cursor.getColumnIndex(KEY_ACTIVE)) == 1);
                sensor.setMacAddress(cursor.getString(cursor.getColumnIndex(KEY_MAC_ADDRESS)));
                sensor.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_CONNECTED)) == 1);
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public long update(Sensor sensor) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_CODE, sensor.getCode());
        values.put(KEY_SERIAL, sensor.getSerial());
        values.put(KEY_VERSION, sensor.getVersion());
        values.put(KEY_DESCRIPTION, sensor.getDescription());
        values.put(KEY_MAC_ADDRESS, sensor.getMacAddress());
        values.put(KEY_ACTIVE, sensor.isActive() ? 1 : 0);
        values.put(KEY_CONNECTED, sensor.isConnected() ? 1 : 0);

        context.getContentResolver().update(SensorContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(sensor.getId())
                }
        );
        return sensor.getId();
    }

    // Updating single object
    public void disconnectAllSensors() {

        ContentValues values = new ContentValues();
        values.put(KEY_CONNECTED, 0);
        context.getContentResolver().update(SensorContentProvider.CONTENT_URI,
                values,
                null,
                null
        );
    }



    // Deleting single object
    public void delete(Sensor sensor) {
        context.getContentResolver().delete(SensorContentProvider.CONTENT_URI,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(sensor.getId())
                }
        );
    }

    public void deleteAll(SQLiteDatabase db){
        context.getContentResolver().delete(SensorContentProvider.CONTENT_URI,
                null,
                null
        );
        db.delete(SensorCharacteristicDao.TABLE_NAME, null, null);
        db.delete(AlertDao.TABLE_NAME, null, null);

    }
}

