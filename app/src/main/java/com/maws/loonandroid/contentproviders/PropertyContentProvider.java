package com.maws.loonandroid.contentproviders;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.PropertyDao;
import com.maws.loonandroid.enums.LoonDataType;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Andres Prada on 6/18/2015.
 */
public class PropertyContentProvider  extends ContentProvider {

    private LoonMedicalDao loonMedicalDao;


    private static final int PROPERTY = 10;
    private static final int PROPERTY_ID = 20;

    private static final String AUTHORITY = "com.maws.loonandroid.contentproviders.PropertyContentProvider";
    private static final String BASE_PATH = "properties";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);


    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/properties";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/property";

    private static final UriMatcher sURIMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, BASE_PATH, PROPERTY);
        matcher.addURI(AUTHORITY, BASE_PATH + "/#", PROPERTY_ID);
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

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(PropertyDao.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PROPERTY:
                break;
            case PROPERTY_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(PropertyDao.KEY_ID + "="
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
        return LoonDataType.PROPERTY.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case PROPERTY:
                id = sqlDB.insert(PropertyDao.TABLE_NAME, null, values);
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
            case PROPERTY:
                rowsDeleted = sqlDB.delete(PropertyDao.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case PROPERTY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PropertyDao.TABLE_NAME,
                            PropertyDao.KEY_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(PropertyDao.TABLE_NAME,
                            PropertyDao.KEY_ID + "=" + id
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case PROPERTY:
                rowsUpdated = sqlDB.update(PropertyDao.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PROPERTY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PropertyDao.TABLE_NAME,
                            values,
                            PropertyDao.KEY_ID+ "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(PropertyDao.TABLE_NAME,
                            values,
                            PropertyDao.KEY_ID + "=" + id
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
                PropertyDao.KEY_ID,
                PropertyDao.KEY_NAME,
                PropertyDao.KEY_DISPLAY_ID,
                PropertyDao.KEY_METRIC
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
