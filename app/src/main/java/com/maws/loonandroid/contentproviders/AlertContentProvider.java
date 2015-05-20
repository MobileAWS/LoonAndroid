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
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.enums.LoonDataType;

public class AlertContentProvider extends ContentProvider {

    // database classes
    private AlertDao alertDao;
    private LoonMedicalDao loonMedicalDao;

    // used for the UriMacher
    private static final int ALERTS = 10;
    private static final int ALERT_ID = 20;

    private static final String AUTHORITY = "com.maws.loonandroid.contentproviders.AlertContentProvider";
    private static final String BASE_PATH = "alerts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/alerts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/alert";

    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, BASE_PATH, ALERTS);
        matcher.addURI(AUTHORITY, BASE_PATH + "/#", ALERT_ID);
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
        queryBuilder.setTables(AlertDao.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ALERTS:
                break;
            case ALERT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(AlertDao.KEY_ID + "="
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
            case ALERTS:
                id = sqlDB.insert(AlertDao.TABLE_NAME, null, values);
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
            case ALERTS:
                rowsDeleted = sqlDB.delete(AlertDao.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ALERT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(AlertDao.TABLE_NAME,
                            AlertDao.KEY_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(AlertDao.TABLE_NAME,
                            AlertDao.KEY_ID + "=" + id
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
            case ALERTS:
                rowsUpdated = sqlDB.update(AlertDao.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ALERT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AlertDao.TABLE_NAME,
                            values,
                            AlertDao.KEY_ID+ "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(AlertDao.TABLE_NAME,
                            values,
                            AlertDao.KEY_ID + "=" + id
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
                AlertDao.KEY_ID,
                AlertDao.KEY_SENSOR_SERVICE_ID,
                AlertDao.KEY_ALERT_DATE,
                AlertDao.KEY_DISMISSED };
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
