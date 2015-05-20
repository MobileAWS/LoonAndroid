package com.maws.loonandroid.dao;

import java.text.SimpleDateFormat;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoonMedicalDao extends SQLiteOpenHelper {

    public static final String _DATABASE_NAME = "loonmedical";
    public static final int _DATABASE_VERSION = 2;
    public static final SimpleDateFormat iso8601Format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private Context context;

    public LoonMedicalDao( Context context ) {
        super(context, LoonMedicalDao._DATABASE_NAME, null, LoonMedicalDao._DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        UserDao userDao = new UserDao(context);
        userDao.onCreate(db);

        SensorDao sensorDao = new SensorDao(context);
        sensorDao.onCreate(db);

        SensorServiceDao sensorServiceDao = new SensorServiceDao(context);
        sensorServiceDao.onCreate(db);

        AlertDao alertDao = new AlertDao(context);
        alertDao.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        UserDao userDao = new UserDao(context);
        userDao.onUpgrade(db, oldVersion, newVersion);

        SensorDao sensorDao = new SensorDao(context);
        sensorDao.onUpgrade(db, oldVersion, newVersion);

        SensorServiceDao sensorServiceDao = new SensorServiceDao(context);
        sensorServiceDao.onUpgrade(db, oldVersion, newVersion);

        AlertDao alertDao = new AlertDao(context);
        alertDao.onUpgrade(db, oldVersion, newVersion);
    }

    public void clearData(){

        SQLiteDatabase db = this.getWritableDatabase();

        UserDao userDao = new UserDao(context);
        userDao.deleteAll(db);

        SensorDao sensorDao = new SensorDao(context);
        sensorDao.deleteAll(db);

        SensorServiceDao sensorServiceDao = new SensorServiceDao(context);
        sensorServiceDao.deleteAll(db);

        AlertDao alertDao = new AlertDao(context);
        alertDao.deleteAll(db);

        db.close();
    }

}
