package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.models.Alert;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 10/05/2015.
 */
public class AlertDao {

    // Contacts table name
    private static final String TABLE_NAME = "tblAlert";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SENSOR_SERVICE_ID = "sensorServiceId";
    private static final String KEY_ALERT_DATE = "dateInMillis";
    private static final String KEY_DISMISSED = "dismissed";

    private Context context;

    public AlertDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_SENSOR_SERVICE_ID + " INT REFERENCES "+SensorServiceDao.TABLE_NAME+"("+SensorServiceDao.KEY_ID+") ON DELETE CASCADE," +
                KEY_ALERT_DATE + " INT," +
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
    public void create(Alert alert, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_SERVICE_ID, alert.getSensorServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate() == null ? null : alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed()?1:0);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

    }

    // Getting single object
    public Alert get(int id, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_SENSOR_SERVICE_ID,
                        KEY_ALERT_DATE,
                        KEY_DISMISSED
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

        Alert alert = new Alert();
        alert.setId(cursor.getInt(0));
        alert.setSensorServiceId(cursor.getInt(1));
        alert.setAlertDate(cursor.isNull(2) ? null : new Date(cursor.getLong(2)));
        alert.setDismissed(cursor.getInt(3) == 1);
        db.close();
        cursor.close();

        // return object
        return alert;
    }

    // Getting All objects
    public List<Alert> getAll(SQLiteDatabase db) {

        List<Alert> toReturnList = new ArrayList<Alert>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Alert alert = new Alert();
                alert.setId(cursor.getInt(0));
                alert.setSensorServiceId(cursor.getInt(1));
                alert.setAlertDate(cursor.isNull(2) ? null : new Date(cursor.getLong(2)));
                alert.setDismissed(cursor.getInt(3) == 1);
                toReturnList.add(alert);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(Alert alert, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_SERVICE_ID, alert.getSensorServiceId());
        values.put(KEY_ALERT_DATE, alert.getAlertDate() == null ? null : alert.getAlertDate().getTime());
        values.put(KEY_DISMISSED, alert.isDismissed()? 1:0 );

        // updating row
        int toReturn =  db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) });

        return toReturn;
    }

    // Deleting single object
    public void delete(Alert alert, SQLiteDatabase db) {
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(alert.getId()) });
    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }

}
