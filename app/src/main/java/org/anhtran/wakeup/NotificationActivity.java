package org.anhtran.wakeup;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.anhtran.wakeup.data.AlarmContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class NotificationActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURRENT_ALARM_LOADER = 0;
    private View mContentView;
    private TextView mAlarmName;
    private TextView mAlarmTime;
    private TextView mDismiss;
    private Calendar calendar = Calendar.getInstance();
    private Uri mCurrentAlarmUri;
    private Alarm mCurrentAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mAlarmName = (TextView) findViewById(R.id.notif_activity_alarm_name);
        mAlarmTime = (TextView) findViewById(R.id.notif_activity_alarm_time);
        mDismiss = (TextView) findViewById(R.id.notif_activity_dismiss);

        // Get URI data of intent from alarm list activity
        mCurrentAlarmUri = getIntent().getData();

        // Init loader
        getLoaderManager().initLoader(CURRENT_ALARM_LOADER, null, this);

        // Instantiate a new alarm object
        mCurrentAlarm = new Alarm();

        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentAlarm.setAlarmOff(NotificationActivity.this);
                ContentValues values = new ContentValues();
                values.put(AlarmContract.AlarmEntry.COLUMN_ALARM_STATE, 0);
                getContentResolver().update(mCurrentAlarmUri, values, null, null);
                finish();
            }
        });

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (mCurrentAlarmUri != null) {
            String[] projection = {
                    AlarmContract.AlarmEntry._ID,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_NAME,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_DATETIME,
                    AlarmContract.AlarmEntry.COLUMN_ALARM_STATE};

            return new CursorLoader(this, mCurrentAlarmUri, projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_ALARM_NAME);
            int datetimeColumnIndex = cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_ALARM_DATETIME);

            String name = cursor.getString(nameColumnIndex);
            Long datetime = cursor.getLong(datetimeColumnIndex);

            if (TextUtils.isEmpty(name)) {
                mAlarmName.setText(R.string.notification_default_name);
            } else {
                mAlarmName.setText(name);
            }
            calendar.setTimeInMillis(datetime);

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss aaa");
            String text = formatter.format(datetime);
            mAlarmTime.setText(text);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
