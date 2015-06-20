package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.Interfaces.PropertiesDaoInterface;
import com.maws.loonandroid.contentproviders.PropertyContentProvider;
import com.maws.loonandroid.models.Property;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Andres on 6/18/2015.
 */
public class PropertyDao implements PropertiesDaoInterface {

    public static String TABLE_NAME = "tblProperties";

    public static  String KEY_ID = "_id";
    public static  String KEY_METRIC = "metric";


    private Context context;

    public PropertyDao(Context context) {
        this.context = context;
    }


    public void onCreate(SQLiteDatabase db){
        String  CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " TEXT PRIMARY KEY," +
                KEY_METRIC + " TEXT"+ ")";

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void delete(Property property) {
        context.getContentResolver().delete(
                PropertyContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[]{property.getId()}
        );
    }

    @Override
    public void deleteAll(SQLiteDatabase db) {
        db.delete(TABLE_NAME, null, null);
    }

    public void addElement( Property property) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, property.getId());
        values.put(KEY_METRIC, property.getValue());

        context.getContentResolver().insert(PropertyContentProvider.CONTENT_URI, values);
    }

    @Override
    public Property getElementForID(String id) {
        Cursor cursor = context.getContentResolver().query(PropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_METRIC
                },
                KEY_ID + "=?",
                new String[]{
                        id
                },
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }
        Property property = new Property();
        property.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
        property.setValue(cursor.getString(cursor.getColumnIndex(KEY_METRIC)));


        cursor.close();
        // return object
        return property;
    }

    @Override
    public List<Property> getAllElementForID(String id) {
        List<Property> listProperties = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(PropertyContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_METRIC,
                },
                KEY_ID + "=?" ,
                new String[]{
                        id
                },
                null
        );
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do  {
                Property property = new Property();
                property.setId(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                property.setValue(cursor.getString(cursor.getColumnIndex(KEY_METRIC)));

                listProperties.add(property);
            } while(cursor.moveToNext());
        }else{
            return null;
        }
        cursor.close();
        // return list object
        return listProperties;
    }


    public int update(Property property) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, property.getId());
        values.put(KEY_METRIC, property.getValue());

        int toReturn = context.getContentResolver().update(
                PropertyContentProvider.CONTENT_URI,
                values,
                KEY_ID + " = ?",
                new String[] {property.getId() }
        );
        return toReturn;
    }
    public void onUpgrade (SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }



}
