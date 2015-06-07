package com.maws.loonandroid.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoonMedicalDao extends SQLiteOpenHelper {

    public static final String _DATABASE_NAME = "loonmedical";
    public static final int _DATABASE_VERSION = 9;
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

        SensorCharacteristicDao sensorCharacteristicDao = new SensorCharacteristicDao(context);
        sensorCharacteristicDao.onCreate(db);

        AlertDao alertDao = new AlertDao(context);
        alertDao.onCreate(db);

        CustomerDao customerDao = new CustomerDao(context);
        customerDao.onCreate(db);

        SiteDao siteDao = new SiteDao(context);
        siteDao.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        UserDao userDao = new UserDao(context);
        userDao.onUpgrade(db, oldVersion, newVersion);

        SensorDao sensorDao = new SensorDao(context);
        sensorDao.onUpgrade(db, oldVersion, newVersion);

        SensorCharacteristicDao sensorCharacteristicDao = new SensorCharacteristicDao(context);
        sensorCharacteristicDao.onUpgrade(db, oldVersion, newVersion);

        AlertDao alertDao = new AlertDao(context);
        alertDao.onUpgrade(db, oldVersion, newVersion);

        CustomerDao customerDao = new CustomerDao(context);
        customerDao.onUpgrade(db, oldVersion, newVersion);

        SiteDao siteDao = new SiteDao(context);
        siteDao.onUpgrade(db, oldVersion, newVersion);
    }

    public void clearData(){

        SQLiteDatabase db = this.getWritableDatabase();

        UserDao userDao = new UserDao(context);
        userDao.deleteAll(db);

        SensorDao sensorDao = new SensorDao(context);
        sensorDao.deleteAll(db);

        AlertDao alertDao = new AlertDao(context);
        alertDao.deleteAll(db);

        db.close();
    }

}
