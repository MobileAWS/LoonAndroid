package com.maws.loonandroid.dao;

import java.util.ArrayList;
import java.util.List;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.SensorService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class SensorDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblSensor";

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CODE = "code";
    public static final String KEY_SERIAL = "serial";
    public static final String KEY_VERSION = "version";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MAC_ADDRESS = "mac";
    public static final String KEY_ACTIVE = "active";

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
                KEY_MAC_ADDRESS + " TEXT" + ")";

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
    public void create(Sensor sensor, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_CODE, sensor.getCode());
        values.put(KEY_SERIAL, sensor.getSerial());
        values.put(KEY_VERSION, sensor.getVersion());
        values.put(KEY_DESCRIPTION, sensor.getDescription());
        values.put(KEY_MAC_ADDRESS, sensor.getMacAddress());
        values.put(KEY_ACTIVE, sensor.isActive());

        // Inserting Row
        long sensorId =  db.insert(TABLE_NAME, null, values);

        if(sensorId >= 0) {
            sensor.setId(sensorId);

            //if the sensor has services, we need to create them too
            SensorServiceDao ssDao = new SensorServiceDao(this.context);
            for (SensorService sService : sensor.getSensorServices()) {
                sService.setSensorId(sensorId);
                ssDao.create(sService, db, false);
            }
        }

        db.close(); // Closing database connection

    }

    // Getting single object
    public Sensor get(long id, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_NAME,
                        KEY_CODE,
                        KEY_SERIAL,
                        KEY_VERSION,
                        KEY_DESCRIPTION,
                        KEY_MAC_ADDRESS,
                        KEY_ACTIVE
                },
                KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)
                },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Sensor sensor = new Sensor();
        sensor.setId(cursor.getInt(0));
        sensor.setName(cursor.getString(1));
        sensor.setCode(cursor.getString(2));
        sensor.setSerial(cursor.getString(3));
        sensor.setVersion(cursor.getString(4));
        sensor.setDescription(cursor.getString(5));
        sensor.setMacAddress(cursor.getString(6));
        sensor.setActive(cursor.getInt(7) == 1);
        db.close();
        cursor.close();

        // return object
        return sensor;
    }

    // Getting All objects
    public List<Sensor> getAll(SQLiteDatabase db) {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getInt(0));
                sensor.setName(cursor.getString(1));
                sensor.setCode(cursor.getString(2));
                sensor.setSerial(cursor.getString(3));
                sensor.setVersion(cursor.getString(4));
                sensor.setDescription(cursor.getString(5));
                sensor.setActive(cursor.getInt(6) == 1);
                sensor.setMacAddress(cursor.getString(7));
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Sensor> getAllActive(SQLiteDatabase db) {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+KEY_ACTIVE+" = 1 ORDER BY " + KEY_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getInt(0));
                sensor.setName(cursor.getString(1));
                sensor.setCode(cursor.getString(2));
                sensor.setSerial(cursor.getString(3));
                sensor.setVersion(cursor.getString(4));
                sensor.setDescription(cursor.getString(5));
                sensor.setActive(cursor.getInt(6) == 1);
                sensor.setMacAddress(cursor.getString(7));
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<Sensor> getAllInactive(SQLiteDatabase db) {

        List<Sensor> toReturnList = new ArrayList<Sensor>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+KEY_ACTIVE+" = 0 ORDER BY " + KEY_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Sensor sensor = new Sensor();
                sensor.setId(cursor.getInt(0));
                sensor.setName(cursor.getString(1));
                sensor.setCode(cursor.getString(2));
                sensor.setSerial(cursor.getString(3));
                sensor.setVersion(cursor.getString(4));
                sensor.setDescription(cursor.getString(5));
                sensor.setActive(cursor.getInt(6) == 1);
                sensor.setMacAddress(cursor.getString(7));
                toReturnList.add(sensor);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(Sensor sensor, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, sensor.getName());
        values.put(KEY_CODE, sensor.getCode());
        values.put(KEY_SERIAL, sensor.getSerial());
        values.put(KEY_VERSION, sensor.getVersion());
        values.put(KEY_DESCRIPTION, sensor.getDescription());
        values.put(KEY_MAC_ADDRESS, sensor.getMacAddress());
        values.put(KEY_ACTIVE, sensor.isActive()? 1:0);

        // updating row
        int toReturn =  db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(sensor.getId()) });

        return toReturn;
    }

    // Deleting single object
    public void delete(Sensor sensor, SQLiteDatabase db) {
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(sensor.getId()) });
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
        db.delete(SensorServiceDao.TABLE_NAME, null, null);
        db.delete(AlertDao.TABLE_NAME, null, null);

    }
}

