package com.maws.loonandroid.dao;

/**
 * Created by Andrexxjc on 10/05/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.models.SensorService;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class SensorServiceDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblSensorService";

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    private static final String KEY_SENSOR_ID = "sensorId";
    private static final String KEY_NAME = "name";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_ON = "active";

    private Context context;

    public SensorServiceDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_SENSOR_ID + " INT REFERENCES "+SensorDao.TABLE_NAME+"("+SensorDao.KEY_ID+") ON DELETE CASCADE," +
                KEY_NAME + " TEXT," +
                KEY_ALARM + " INT," +
                KEY_ON + " TINYINT" + ")";

        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void create(SensorService sensorService, SQLiteDatabase db) {
        create(sensorService, db, true);
    }

    // Adding new object
    public void create(SensorService sensorService, SQLiteDatabase db, boolean closeConnection) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_ID, sensorService.getSensorId());
        values.put(KEY_NAME, sensorService.getName());
        values.put(KEY_ALARM, sensorService.getAlarm());
        values.put(KEY_ON, sensorService.isOn()?1:0);

        // Inserting Row
        long id = db.insert(TABLE_NAME, null, values);
        sensorService.setId(id);
        if(closeConnection) {
            db.close(); // Closing database connection
        }

    }

    // Getting single object
    public SensorService get(int id, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_SENSOR_ID,
                        KEY_NAME,
                        KEY_ALARM,
                        KEY_ON
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

        SensorService sensor = new SensorService();
        sensor.setId(cursor.getLong(0));
        sensor.setSensorId(cursor.getLong(1));
        sensor.setName(cursor.getString(2));
        sensor.setAlarm(cursor.getInt(3));
        sensor.setOn(cursor.getInt(4) == 1);
        db.close();
        cursor.close();

        // return object
        return sensor;
    }

    // Getting All objects
    public List<SensorService> getAll(SQLiteDatabase db) {

        List<SensorService> toReturnList = new ArrayList<SensorService>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                SensorService sensorService = new SensorService();
                sensorService.setId(cursor.getLong(0));
                sensorService.setSensorId(cursor.getLong(1));
                sensorService.setName(cursor.getString(2));
                sensorService.setAlarm(cursor.getInt(3));
                sensorService.setOn(cursor.getInt(4) == 1);
                toReturnList.add(sensorService);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Getting All objects
    public List<SensorService> getAllBySensorId(long sensorId, SQLiteDatabase db) {

        List<SensorService> toReturnList = new ArrayList<SensorService>();
        // Select All Query
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        KEY_ID,
                        KEY_SENSOR_ID,
                        KEY_NAME,
                        KEY_ALARM,
                        KEY_ON
                },
                KEY_SENSOR_ID + "=?",
                new String[]{
                        String.valueOf(sensorId)
                },
                null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                SensorService sensorService = new SensorService();
                sensorService.setId(cursor.getLong(0));
                sensorService.setSensorId(cursor.getLong(1));
                sensorService.setName(cursor.getString(2));
                sensorService.setAlarm(cursor.getInt(3));
                sensorService.setOn(cursor.getInt(4) == 1);
                toReturnList.add(sensorService);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(SensorService sensorService, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_ID, sensorService.getSensorId());
        values.put(KEY_NAME, sensorService.getName());
        values.put(KEY_ALARM, sensorService.getAlarm());
        values.put(KEY_ON, sensorService.isOn()? 1:0);

        // updating row
        int toReturn =  db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(sensorService.getId()) });

        return toReturn;
    }

    // Deleting single object
    public void delete(SensorService sensorService, SQLiteDatabase db) {
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(sensorService.getId()) });
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }
}

