package org.anhtran.wakeup.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.anhtran.wakeup.data.AlarmContract.AlarmEntry;

/**
 * Created by anhtran on 3/14/17.
 */

public class AlarmProvider extends ContentProvider {

    public static final String LOG_TAG = AlarmProvider.class.getSimpleName();

    private static final int ALARMS = 100;

    private static final int ALARM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.PATH_ALARMS, ALARMS);

        sUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY, AlarmContract.PATH_ALARMS + "/#", ALARM_ID);
    }

    private AlarmDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new AlarmDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                cursor = database.query(AlarmEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ALARM_ID:
                selection = AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AlarmEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALARMS:
                return insertAlarm(uri, values);
            default:
                throw new IllegalArgumentException("Insert error with the URI " + uri);
        }
    }

    private Uri insertAlarm(Uri uri, ContentValues values) {

        Long datetime = values.getAsLong(AlarmEntry.COLUMN_ALARM_DATETIME);
        if (datetime == null || datetime < 0) {
            throw new IllegalArgumentException("Date/Time is invalid");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(AlarmEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rows;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALARMS:
                rows = database.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                if (rows != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rows;
            case ALARM_ID:
                selection = AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = database.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                if (rows != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rows;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return updateAlarm(uri, values, selection, selectionArgs);
            case ALARM_ID:
                selection = AlarmEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAlarm(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not support for " + uri);
        }
    }

    private int updateAlarm(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(AlarmEntry.COLUMN_ALARM_DATETIME)) {
            Long datetime = values.getAsLong(AlarmEntry.COLUMN_ALARM_DATETIME);
            if (datetime == null || datetime < 0) {
                throw new IllegalArgumentException("Alarm date/time is invalid");
            }
        }

        if (values.containsKey(AlarmEntry.COLUMN_ALARM_STATE)) {
            Integer state = values.getAsInteger(AlarmEntry.COLUMN_ALARM_STATE);
            if (state == null || !AlarmEntry.isStateValid(state)) {
                throw new IllegalArgumentException("Alarm State is invalid");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rows = database.update(AlarmEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return AlarmEntry.CONTENT_LIST_TYPE;
            case ALARM_ID:
                return AlarmEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
