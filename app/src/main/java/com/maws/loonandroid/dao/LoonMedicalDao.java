package com.maws.loonandroid.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoonMedicalDao extends SQLiteOpenHelper {

    public static final String _DATABASE_NAME = "loonmedical";
    public static final int _DATABASE_VERSION = 16;
    private Context context;

    public LoonMedicalDao( Context context ) {
        super(context, LoonMedicalDao._DATABASE_NAME, null, LoonMedicalDao._DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        UserDao userDao = new UserDao(context);
        userDao.onCreate(db);

        DeviceDao deviceDao = new DeviceDao(context);
        deviceDao.onCreate(db);

        DeviceCharacteristicDao deviceCharacteristicDao = new DeviceCharacteristicDao(context);
        deviceCharacteristicDao.onCreate(db);


        CustomerDao customerDao = new CustomerDao(context);
        customerDao.onCreate(db);

        SiteDao siteDao = new SiteDao(context);
        siteDao.onCreate(db);

        PropertyDao propertyDao = new PropertyDao(context);
        propertyDao.onCreate(db);

        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(context);
        devicePropertyDao.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        UserDao userDao = new UserDao(context);
        userDao.onUpgrade(db, oldVersion, newVersion);

        DeviceDao deviceDao = new DeviceDao(context);
        deviceDao.onUpgrade(db, oldVersion, newVersion);

        DeviceCharacteristicDao deviceCharacteristicDao = new DeviceCharacteristicDao(context);
        deviceCharacteristicDao.onUpgrade(db, oldVersion, newVersion);

        DevicePropertyDao alertDao = new DevicePropertyDao(context);
        alertDao.onUpgrade(db, oldVersion, newVersion);

        CustomerDao customerDao = new CustomerDao(context);
        customerDao.onUpgrade(db, oldVersion, newVersion);

        SiteDao siteDao = new SiteDao(context);
        siteDao.onUpgrade(db, oldVersion, newVersion);

        PropertyDao propertyDao = new PropertyDao(context);
        propertyDao.onUpgrade(db, oldVersion, newVersion);

        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(context);
        devicePropertyDao.onUpgrade(db, oldVersion, newVersion);
    }

    public void clearData(){

        SQLiteDatabase db = this.getWritableDatabase();

        UserDao userDao = new UserDao(context);
        userDao.deleteAll(db);

        DeviceDao deviceDao = new DeviceDao(context);
        deviceDao.deleteAll(db);

        DevicePropertyDao devicePropertyDao = new DevicePropertyDao(context);
        devicePropertyDao.deleteAll(db);

        db.close();
    }

}
