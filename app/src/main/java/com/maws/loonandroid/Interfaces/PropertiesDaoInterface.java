package com.maws.loonandroid.Interfaces;

import android.content.Entity;
import android.database.sqlite.SQLiteDatabase;

import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Property;

import java.util.List;
import java.util.Objects;

/**
 * Created by Andres on 6/19/2015.
 */
public interface PropertiesDaoInterface {
    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) ;
    void deleteAll(SQLiteDatabase db);
    void addElement(Property property);
    Property getElementForID(String id);
    List<Property> getAllElementForID(String id);

}
