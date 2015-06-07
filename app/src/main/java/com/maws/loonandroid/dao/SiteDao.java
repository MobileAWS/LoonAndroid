package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.contentproviders.SiteContentProvider;
import com.maws.loonandroid.models.Site;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 04/06/2015.
 */
public class SiteDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblSite";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_CODE = "code";

    private Context context;

    public SiteDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_CODE + " TEXT )";
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
    public void create(Site site) {

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, site.getCode());
        context.getContentResolver().insert(SiteContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public Site get(long id) {

        Cursor cursor = context.getContentResolver().query(
                SiteContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_CODE
                },
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Site site = new Site();
        site.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        site.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        cursor.close();
        return site;
    }

    // Getting single object
    public Site get(String code) {

        Cursor cursor = context.getContentResolver().query(
                SiteContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_CODE
                },
                KEY_CODE + "=?",
                new String[]{
                        code
                },
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Site site = new Site();
        site.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        site.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        cursor.close();
        return site;
    }

    // Getting All objects
    public List<Site> getAll(SQLiteDatabase db) {

        List<Site> toReturnList = new ArrayList<Site>();
        Cursor cursor = context.getContentResolver().query(
                SiteContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_CODE
                },
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Site site = new Site();
                site.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                site.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                toReturnList.add(site);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public long update(Site site) {

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, site.getCode());

        long id = context.getContentResolver().update(
                SiteContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(site.getId())
                });
        return id;
    }

    // Deleting single object
    public void delete(Site site) {
        context.getContentResolver().delete(
                SiteContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[]{
                        String.valueOf(site.getId())
                }
        );
    }
}
