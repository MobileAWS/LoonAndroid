package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.contentproviders.PropertyContentProvider;
import com.maws.loonandroid.models.Property;

/**
 * Created by Andres on 6/18/2015.
 */
public class PropertyDao {

    public static String TABLE_NAME = "tblProperties";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DISPLAY_ID = "displayId";
    public static final String KEY_METRIC = "metric";

    private Context context;

    public PropertyDao(Context context) {
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db){
        String  CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_DISPLAY_ID + " INTEGER," +
                KEY_METRIC + " TEXT"+ ")";

        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void create(Property property) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, property.getName());
        values.put(KEY_DISPLAY_ID, property.getDisplayId());
        values.put(KEY_METRIC, property.getMetric());
        context.getContentResolver().insert(PropertyContentProvider.CONTENT_URI, values);
    }

    public void delete(Property property) {
        context.getContentResolver().delete(
                PropertyContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[]{String.valueOf(property.getId())}
        );
    }

    public Property get(long id) {
        Cursor cursor = context.getContentResolver().query(PropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_DISPLAY_ID,
                        KEY_METRIC
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
        Property property = new Property();
        property.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        property.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        property.setDisplayId(cursor.getInt(cursor.getColumnIndex(KEY_DISPLAY_ID)));
        property.setMetric(cursor.getString(cursor.getColumnIndex(KEY_METRIC)));
        cursor.close();

        return property;
    }

    public void deleteAll(SQLiteDatabase db) {
        db.delete(TABLE_NAME, null, null);
    }

}
