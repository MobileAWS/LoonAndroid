package com.maws.loonandroid.dao;

import java.util.ArrayList;
import java.util.List;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.util.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Created by Andrexxjc on 07/05/2015.
 */
public class UserDao {

    // Contacts table name
    private static final String TABLE_NAME = "tblUser";

    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    private Context context;

    public UserDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_EMAIL + " TEXT," +
                KEY_NAME + " TEXT," +
                KEY_PASSWORD + " TEXT )";

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
    public void create(User user, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, Util.MD5(user.getPassword()));

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection

    }

    // Getting single object
    public User get(String email, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_EMAIL,
                        KEY_NAME,
                        KEY_PASSWORD
                },
                KEY_EMAIL + "=?",
                new String[] {
                        String.valueOf(email)
                },
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
        user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
        db.close();
        cursor.close();

        // return object
        return user;
    }

    // Getting single object
    public User get(Long id, SQLiteDatabase db) {

        Cursor cursor = db.query(TABLE_NAME,
                new String[] {
                        KEY_ID,
                        KEY_EMAIL,
                        KEY_NAME,
                        KEY_PASSWORD
                },
                KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)
                },
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
        user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
        db.close();
        cursor.close();

        // return object
        return user;
    }

    // Getting All objects
    public List<User> getAll(SQLiteDatabase db) {

        List<User> toReturnList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                user.setEmail(cursor.getString( cursor.getColumnIndex(KEY_EMAIL) ));
                user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                toReturnList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public int update(User user, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        if(!TextUtils.isEmpty( user.getPassword() ) ) {
            values.put(KEY_PASSWORD, Util.MD5( user.getPassword()));
        }
        values.put(KEY_EMAIL, user.getEmail());

        // updating row
        int toReturn =  db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });

        return toReturn;
    }

    // Deleting single object
    public void delete(User user, SQLiteDatabase db) {

        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });

    }

    public void deleteAll(SQLiteDatabase db){
        db.delete(TABLE_NAME, null, null);
    }

}
