package com.maws.loonandroid.contentproviders;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.maws.loonandroid.dao.LogDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.enums.LoonDataType;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Andrexxjc on 14/09/2015.
 */
public class LogEntryContentProvider extends ContentProvider {

// database classes
private LoonMedicalDao loonMedicalDao;

// used for the UriMacher
private static final int LOGS = 80;

private static final String AUTHORITY = "com.maws.loonandroid.contentproviders.LogEntryContentProvider";
private static final String BASE_PATH = "logs";
public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
        + "/logs";
public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
        + "/log";

private static final UriMatcher sURIMatcher = buildUriMatcher();

        private static UriMatcher buildUriMatcher(){
            final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
            matcher.addURI(AUTHORITY, BASE_PATH, LOGS);
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
            queryBuilder.setTables(LogDao.TABLE_NAME);

            int uriType = sURIMatcher.match(uri);
            switch (uriType) {
                case LOGS:
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
            return LoonDataType.LOG.toString();
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            int uriType = sURIMatcher.match(uri);
            SQLiteDatabase sqlDB = loonMedicalDao.getWritableDatabase();
            long id = 0;
            switch (uriType) {
                case LOGS:
                    id = sqlDB.insert(LogDao.TABLE_NAME, null, values);
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
                case LOGS:
                    rowsDeleted = sqlDB.delete(LogDao.TABLE_NAME, selection,
                            selectionArgs);
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
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        private void checkColumns(String[] projection) {
            String[] available = {
                    LogDao.KEY_ID,
                    LogDao.KEY_MESSAGE,
                    LogDao.KEY_CREATED_AT
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