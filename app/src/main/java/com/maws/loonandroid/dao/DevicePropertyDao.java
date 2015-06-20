package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.contentproviders.DevicePropertyContentProvider;
import com.maws.loonandroid.models.DeviceProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andres on 6/19/2015.
 */
public class DevicePropertyDao {

    public static String TABLE_NAME = "tblDeviceProperties";

    public static  String KEY_ID = "_id";
    public static  String KEY_VALUE = "metric";

    public static final int CODE_BED = 3; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_CHAIR = 2; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_TOILET = 0; //these indexes are mapped to the sensor bit on the device characteristic value.
    public static final int CODE_INCONTINENCE = 1;

    public static String DEVICE_BED="Bed";
    public static String DEVICE_CHAIR = "Chair";
    public static String DEVICE_TOILET = "Toilet";
    public static String DEVICE_INCONTINENCE = "Incontinence";

    private Context context;
    public DevicePropertyDao(Context context){
        this.context = context;

    }

    public void onCreate(SQLiteDatabase db) {
        String  CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " TEXT PRIMARY KEY," +
                KEY_VALUE + " TEXT"+ ")";

        db.execSQL(CREATE_TABLE);

    }

    public void onUpgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void delete(DeviceProperty deviceProperty) {
        context.getContentResolver().delete(
                DevicePropertyContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[]{String.valueOf(deviceProperty.getId())}
        );
    }
    public void deleteAll(SQLiteDatabase db) {
        db.delete(TABLE_NAME, null, null);
    }

    public void addElement(DeviceProperty deviceProperty) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, deviceProperty.getId());
        values.put(KEY_VALUE, deviceProperty.getValue());
        context.getContentResolver().insert(DevicePropertyContentProvider.CONTENT_URI, values);
    }

    public DeviceProperty getElementForID(int id) {
        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_VALUE
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
        deviceProperty.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));

        cursor.close();
        // return object
        return deviceProperty;
    }

    public List<DeviceProperty> getAllElementForID(int id) {
        List<DeviceProperty> listProperties = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_VALUE,
                },
                KEY_ID + "=?" ,
                new String[]{
                       String.valueOf(id)
                },
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do  {
                DeviceProperty deviceProperty = new DeviceProperty();
                deviceProperty.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));

                listProperties.add(deviceProperty);
            } while(cursor.moveToNext());
        }else{
            return null;
        }
        cursor.close();
        // return list object
        return listProperties;
    }

    public List<DeviceProperty> getAll() {
    List<DeviceProperty> toReturnList = new ArrayList<>();
    Cursor cursor = context.getContentResolver().query(DevicePropertyContentProvider.CONTENT_URI,
            new String[]{
                    KEY_ID,
                    KEY_VALUE,
            },
            null,
            null,
            null
    );

    // looping through all rows and adding to list
    if (cursor.moveToFirst()) {
        do {

            DeviceProperty deviceProperty = new DeviceProperty();
            deviceProperty.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            deviceProperty.setValue(cursor.getString(cursor.getColumnIndex(KEY_VALUE)));

            toReturnList.add(deviceProperty);

        } while (cursor.moveToNext());
    }
    cursor.close();

    // return contact list
    return toReturnList;

}
    public int update(DeviceProperty deviceProperty) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, deviceProperty.getId());
        values.put(KEY_VALUE, deviceProperty.getValue());

        int toReturn = context.getContentResolver().update(
                DevicePropertyContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[] {String.valueOf(deviceProperty.getId()) }
        );
        return toReturn;
    }
}
