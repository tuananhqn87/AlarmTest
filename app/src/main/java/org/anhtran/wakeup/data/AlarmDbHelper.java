package org.anhtran.wakeup.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.anhtran.wakeup.data.AlarmContract.AlarmEntry;

/**
 * Created by anhtran on 3/14/17.
 */

public class AlarmDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = AlarmDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "alarms.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ALARMS_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " ("
                + AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AlarmEntry.COLUMN_ALARM_NAME + " TEXT, "
                + AlarmEntry.COLUMN_ALARM_DATETIME + " INTEGER NOT NULL, "
                + AlarmEntry.COLUMN_ALARM_STATE + " INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
