package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.maws.loonandroid.contentproviders.AlertContentProvider;
import com.maws.loonandroid.contentproviders.SensorContentProvider;
import com.maws.loonandroid.models.Alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 10/05/2015.
 */
public class AlertDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblAlert";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_SENSOR_ID = "sensorId";
    public static final String KEY_SENSOR_SERVICE_ID = "sensorServiceId";
    public static final String KEY_ALERT_DATE = "dateInMillis";
    public static final String KEY_IS_ON = "isOn";
    public static final String KEY_DISMISSED = "dismissed";

    private Context context;

    public AlertDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_SENSOR_ID + " INT," +
                KEY_SENSOR_SERVICE_ID + " INT," +
                KEY_ALERT_DATE + " INT," +
                KEY_IS_ON + " TINYINT," +
                KEY_DISMISSED + " TINYINT" + ")";

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
    public void create(Alert alert) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_ID, alert.getSensorId());
        values.put(KEY_SENSOR_SERVICE_ID, alert.getSensorServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate() == null ? null : alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed() ? 1 : 0);
        values.put(KEY_IS_ON, alert.isOn() ? 1 : 0);
        context.getContentResolver().insert(AlertContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public Alert get(int id) {

        Cursor cursor = context.getContentResolver().query(AlertContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_SENSOR_ID,
                        KEY_SENSOR_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_IS_ON,
                        KEY_DISMISSED
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

        Alert alert = new Alert();
        alert.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        alert.setSensorId(cursor.getInt(cursor.getColumnIndex(KEY_SENSOR_ID)));
        alert.setSensorServiceId(cursor.getInt(cursor.getColumnIndex(KEY_SENSOR_SERVICE_ID)));
        alert.setAlertDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ALERT_DATE))));
        alert.setDismissed(cursor.getInt(cursor.getColumnIndex(KEY_DISMISSED)) == 1);
        alert.setIsOn( cursor.getInt(cursor.getColumnIndex(KEY_IS_ON)) == 1);
        cursor.close();

        // return object
        return alert;
    }

    // Getting All objects
    public List<Alert> getAll() {

        List<Alert> toReturnList = new ArrayList<Alert>();
        Cursor cursor = context.getContentResolver().query(AlertContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_SENSOR_ID,
                        KEY_SENSOR_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_IS_ON,
                        KEY_DISMISSED
                },
                null,
                null,
                null
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Alert alert = new Alert();
                alert.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                alert.setSensorId(cursor.getInt(cursor.getColumnIndex(KEY_SENSOR_ID)));
                alert.setSensorServiceId(cursor.getInt(cursor.getColumnIndex(KEY_SENSOR_SERVICE_ID)));
                alert.setAlertDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_ALERT_DATE))));
                alert.setDismissed(cursor.getInt(cursor.getColumnIndex(KEY_DISMISSED)) == 1);
                alert.setIsOn(cursor.getInt(cursor.getColumnIndex(KEY_IS_ON)) == 1);
                toReturnList.add(alert);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(Alert alert) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_ID, alert.getSensorId());
        values.put(KEY_SENSOR_SERVICE_ID, alert.getSensorServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed() ? 1 : 0);
        values.put(KEY_IS_ON, alert.isOn() ? 1 : 0);

        int toReturn = context.getContentResolver().update(
                AlertContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) }
        );
        return toReturn;
    }

    // Updating single object
    public void dismiss(Alert alert) {

        ContentValues values = new ContentValues();
        values.put(KEY_DISMISSED, 1);

        context.getContentResolver().update(
                AlertContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(alert.getId())}
        );
    }

    // Deleting single object
    public void delete(Alert alert) {
        context.getContentResolver().delete(
                AlertContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) }
        );
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }

    public Cursor getUndismissedAlertInfo(SQLiteDatabase db, long sensorId){

        final SQLiteQueryBuilder todayShiftQueryBuilder = new SQLiteQueryBuilder();
        todayShiftQueryBuilder.setTables(AlertDao.TABLE_NAME
                        + " JOIN " + SensorDao.TABLE_NAME+ " ON(" + AlertDao.TABLE_NAME + "." + AlertDao.KEY_SENSOR_ID
                        + "=" + SensorDao.TABLE_NAME + "." + SensorDao.KEY_ID + ")"
        );
        String[] projection = {
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_ALERT_DATE,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_SENSOR_SERVICE_ID,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_DISMISSED,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_IS_ON,
                AlertDao.TABLE_NAME + "." + AlertDao.KEY_SENSOR_ID,
                SensorDao.TABLE_NAME + "." + SensorDao.KEY_NAME +  " AS Sensor",
                SensorDao.TABLE_NAME + "." + SensorDao.KEY_CODE +  " AS SensorCode",
                SensorDao.TABLE_NAME + "." + SensorDao.KEY_DESCRIPTION + " AS SensorDescription"
        };
        String selection = AlertDao.TABLE_NAME + "." + AlertDao.KEY_DISMISSED + "=? AND " +
                SensorDao.TABLE_NAME + "." + SensorDao.KEY_ID + "=?";
        String[] selectArgs = {"0", String.valueOf(sensorId)};

        Cursor cursor = todayShiftQueryBuilder.query( db, projection, selection, selectArgs, null, null, null );
        return cursor;
    }

}
