package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.contentproviders.CustomerContentProvider;
import com.maws.loonandroid.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aprada on 12/9/15.
 */
public class ContactDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblContact";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_NUMBER= "number";

    private Context context;

    public ContactDao(Context context) {
        this.context = context;
    }

    // Creating Tables
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT ," +
                KEY_NUMBER +" TEXT )";
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
    public void create(Contact contact) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_NUMBER, contact.getNumber());
        context.getContentResolver().insert(CustomerContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public Contact get(long id) {

        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_NUMBER
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

        Contact contact = new Contact();
        contact.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        contact.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
        cursor.close();
        return contact;
    }

    // Getting single object
    public Contact get(String code) {

        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_NUMBER
                },
                KEY_NAME + "=?",
                new String[]{
                        code
                },
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Contact contact = new Contact();
        contact.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        contact.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NAME)));

        cursor.close();
        return contact;
    }

    // Getting All objects
    public List<Contact> getAll(SQLiteDatabase db) {

        List<Contact> toReturnList = new ArrayList<Contact>();
        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
                new String[]{
                        KEY_ID,
                        KEY_NAME,
                        KEY_NUMBER
                },
                null,
                null,
                null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                toReturnList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public long update(Contact contact) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_NUMBER, contact.getNumber());

        long id = context.getContentResolver().update(
                CustomerContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(contact.getId())
                });
        return id;
    }

    // Deleting single object
    public void delete(Contact contact) {
        context.getContentResolver().delete(
                CustomerContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[] {
                        String.valueOf(contact.getId())
                }
        );
    }
}