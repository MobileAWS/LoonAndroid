package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.contentproviders.LogEntryContentProvider;
import com.maws.loonandroid.models.LogEntry;
import com.maws.loonandroid.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 14/09/2015.
 */
public class LogDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblLogs";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CREATED_AT = "createdAt";


    private Context context;

    public LogDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_MESSAGE + " STRING," +
                KEY_CREATED_AT + " INT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    //TODO borrar cuando se pruebe // Adding new object
    public void create(String message) {

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message);
        values.put(KEY_CREATED_AT, new Date().getTime());
        context.getContentResolver().insert(LogEntryContentProvider.CONTENT_URI, values);
    }

    // Getting All objects
    public List<String> getAll() {

        List<String> toReturnList = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(LogEntryContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_MESSAGE,
                        KEY_CREATED_AT
                },
                null,
                null,
                KEY_CREATED_AT + " DESC"
        );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LogEntry entry = getObjectFromCursor(cursor);
                toReturnList.add(entry.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    public void deleteAll(){
        context.getContentResolver().delete(LogEntryContentProvider.CONTENT_URI,null,null);
    }

    /*return  object from cursor db */
    private LogEntry getObjectFromCursor(Cursor cursor){
        LogEntry entry =new LogEntry();
        entry.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        entry.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
        entry.setDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED_AT))));
        return entry;
    }
}
