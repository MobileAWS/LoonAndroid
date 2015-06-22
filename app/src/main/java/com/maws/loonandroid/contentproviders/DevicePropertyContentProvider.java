package com.maws.loonandroid.contentproviders;

/**
 * Created by Andrexxjc on 19/05/2015.
 */
import java.util.Arrays;
import java.util.HashSet;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.enums.LoonDataType;

public class DevicePropertyContentProvider extends ContentProvider {

    // database classes
    private LoonMedicalDao loonMedicalDao;

    // used for the UriMacher
    private static final int DEVICE_PROPERTIES = 10;
    private static final int DEVICE_PROPERTY_ID = 20;

    private static final String AUTHORITY = "com.maws.loonandroid.contentproviders.DevicePropertyContentProvider";
    private static final String BASE_PATH = "deviceproperties";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/deviceproperties";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/deviceproperty";

    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, BASE_PATH, DEVICE_PROPERTIES);
        matcher.addURI(AUTHORITY, BASE_PATH + "/#", DEVICE_PROPERTY_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        loonMedicalDao = new LoonMedicalDao(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(DevicePropertyDao.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DEVICE_PROPERTIES:
                break;
            case DEVICE_PROPERTY_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(DevicePropertyDao.KEY_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = loonMedicalDao.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return LoonDataType.ALERT.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case DEVICE_PROPERTIES:
                id = sqlDB.insert(DevicePropertyDao.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case DEVICE_PROPERTIES:
                rowsDeleted = sqlDB.delete(DevicePropertyDao.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case DEVICE_PROPERTY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DevicePropertyDao.TABLE_NAME,
                            DevicePropertyDao.KEY_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DevicePropertyDao.TABLE_NAME,
                            DevicePropertyDao.KEY_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case DEVICE_PROPERTIES:
                rowsUpdated = sqlDB.update(DevicePropertyDao.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case DEVICE_PROPERTY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DevicePropertyDao.TABLE_NAME,
                            values,
                            DevicePropertyDao.KEY_ID+ "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(DevicePropertyDao.TABLE_NAME,
                            values,
                            DevicePropertyDao.KEY_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                DevicePropertyDao.KEY_ID,
                DevicePropertyDao.KEY_DEVICE_ID,
                DevicePropertyDao.KEY_CREATED_AT,
                DevicePropertyDao.KEY_PROPERTY_ID,
                DevicePropertyDao.KEY_VALUE,
                DevicePropertyDao.KEY_DISMISSED_DATE,
                DevicePropertyDao.KEY_TOTAL_TIME_ALARM,
                DevicePropertyDao.KEY_CUSTOMER_ID,
                DevicePropertyDao.KEY_SITE_ID
                };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
