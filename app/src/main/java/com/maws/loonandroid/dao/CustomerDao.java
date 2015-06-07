package com.maws.loonandroid.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.maws.loonandroid.contentproviders.CustomerContentProvider;
import com.maws.loonandroid.models.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 04/06/2015.
 */
public class CustomerDao {

    // Contacts table name
    public static final String TABLE_NAME = "tblCustomer";

    // Contacts Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_CODE = "code";

    private Context context;

    public CustomerDao(Context context) {
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
    public void create(Customer customer) {

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, customer.getCode());
        context.getContentResolver().insert(CustomerContentProvider.CONTENT_URI, values);
    }

    // Getting single object
    public Customer get(long id) {

        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
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

        Customer customer = new Customer();
        customer.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        customer.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        cursor.close();
        return customer;
    }

    // Getting single object
    public Customer get(String code) {

        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
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

        Customer customer = new Customer();
        customer.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        customer.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
        cursor.close();
        return customer;
    }

    // Getting All objects
    public List<Customer> getAll(SQLiteDatabase db) {

        List<Customer> toReturnList = new ArrayList<Customer>();
        Cursor cursor = context.getContentResolver().query(
                CustomerContentProvider.CONTENT_URI,
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
                Customer customer = new Customer();
                customer.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                customer.setCode(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                toReturnList.add(customer);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return toReturnList;
    }

    // Updating single object
    public long update(Customer customer) {

        ContentValues values = new ContentValues();
        values.put(KEY_CODE, customer.getCode());

        long id = context.getContentResolver().update(
                CustomerContentProvider.CONTENT_URI,
                values,
                KEY_ID + "=?",
                new String[]{
                        String.valueOf(customer.getId())
                });
        return id;
    }

    // Deleting single object
    public void delete(Customer customer) {
        context.getContentResolver().delete(
                CustomerContentProvider.CONTENT_URI,
                KEY_ID + " = ?",
                new String[] {
                        String.valueOf(customer.getId())
                }
        );
    }
}
