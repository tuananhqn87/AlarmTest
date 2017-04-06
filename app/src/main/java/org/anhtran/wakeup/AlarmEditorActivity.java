package org.anhtran.wakeup;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.anhtran.wakeup.data.AlarmContract.AlarmEntry;


public class AlarmEditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    final static String MESSAGE = "msg";
    private static final String LOG_TAG = AlarmEditorActivity.class.getSimpleName();
    private static final int CURRENT_ALARM_LOADER = 0;
    private TimePicker mTimePicker;
    private EditText mNameEditText;
    private TextView mAlarmTextView;
    private Uri mCurrentAlarmUri;
    private Switch mEnableSwitch;
    private Alarm alarm;
    private boolean mHasChanged;
    private Calendar calendar = Calendar.getInstance();
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mHasChanged = true;

            return false;
        }
    };


    /**
     * Method to create activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_editor);

        // Get URI data of intent from alarm list activity
        mCurrentAlarmUri = getIntent().getData();

        if (mCurrentAlarmUri == null) {
            setTitle("Add an Alarm");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Alarm");
            getLoaderManager().initLoader(CURRENT_ALARM_LOADER, null, this);
        }

        // Instantiate a new alarm object
        alarm = new Alarm();

        //Add view control
        addControl();
    }


    /**
     * Method to add view control
     */
    private void addControl() {
        // Add alarm name editor
        mNameEditText = (EditText) findViewById(R.id.editor_name_edit_text);
        mNameEditText.setOnTouchListener(mTouchListener);
        // Add time picker
        mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        mTimePicker.setOnTouchListener(mTouchListener);

        //Add alarm text view
        mAlarmTextView = (TextView) findViewById(R.id.editor_alarm_view);

        // Add on/off switch
        mEnableSwitch = (Switch) findViewById(R.id.editor_enable_switch);
        mEnableSwitch.setOnTouchListener(mTouchListener);
        mEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarm.enable();
                } else {
                    alarm.disable();
                    mAlarmTextView.setText(R.string.text_view_alarm_off);
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.COLUMN_ALARM_NAME,
                AlarmEntry.COLUMN_ALARM_DATETIME,
                AlarmEntry.COLUMN_ALARM_STATE};

        return new CursorLoader(this, mCurrentAlarmUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        final boolean ON = true;
        final boolean OFF = false;

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(AlarmEntry.COLUMN_ALARM_NAME);
            int datetimeColumnIndex = cursor.getColumnIndex(AlarmEntry.COLUMN_ALARM_DATETIME);
            int stateColumnIndex = cursor.getColumnIndex(AlarmEntry.COLUMN_ALARM_STATE);

            String name = cursor.getString(nameColumnIndex);
            Long datetime = cursor.getLong(datetimeColumnIndex);

            mNameEditText.setText(name);
            calendar.setTimeInMillis(datetime);

            Integer state = cursor.getInt(stateColumnIndex);
            if (state == AlarmEntry.STATE_ON) {
                mEnableSwitch.setChecked(ON);
                alarm.enable();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm:ss aaa");
                String text = formatter.format(datetime);
                mAlarmTextView.setText(getString(R.string.text_view_alarm_on) + "\n" + text);
            } else {
                mEnableSwitch.setChecked(OFF);
                mAlarmTextView.setText(R.string.text_view_alarm_off);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        calendar.setTimeInMillis(System.currentTimeMillis());
    }


    // Save alarm to database
    @TargetApi(Build.VERSION_CODES.M)
    private void saveAlarm() {

        // Set time to alarm
        alarm.setTime(mTimePicker.getHour(), mTimePicker.getMinute());

        // Get alarm time in millisecond
        long datetime = alarm.getAlarmTimeInMillis();

        // Assign name from edit text to variable name
        String name = mNameEditText.getText().toString().trim();

        // If alarm is enabled, the state will be STATE_ON, otherwise the state will be STATE_OFF
        int state;
        if (alarm.isEnabled()) {
            state = AlarmEntry.STATE_ON;
        } else {
            state = AlarmEntry.STATE_OFF;
        }

        // Put values to write to database columns
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_ALARM_DATETIME, datetime);
        values.put(AlarmEntry.COLUMN_ALARM_NAME, name);
        values.put(AlarmEntry.COLUMN_ALARM_STATE, state);

        // If the is no current URI then insert new alarm to database
        if (mCurrentAlarmUri == null) {

            mCurrentAlarmUri = getContentResolver().insert(AlarmEntry.CONTENT_URI, values);

            if (mCurrentAlarmUri == null) {
                Toast.makeText(this, "Error with saving alarm", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Alarm saved", Toast.LENGTH_LONG).show();
            }

            if (alarm.isEnabled()) {
                alarm.setAlarmOn(this, mCurrentAlarmUri);
            }
        }
        // If there is a current URI then update database record at this current URI
        else {

            int rowsAffected = getContentResolver().update(mCurrentAlarmUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating alarm",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update successful",
                        Toast.LENGTH_SHORT).show();
            }

            // When updating alarm,
            // if alarm is enabled then set alarm off to cancel current pending intent
            // then set alarm on again to enable alarm with new pending intent
            if (alarm.isEnabled()) {
                alarm.setAlarmOff(this);
                alarm.setAlarmOn(this, mCurrentAlarmUri);
            }
        }
    }

    private void deleteAlarm() {
        if (mCurrentAlarmUri != null) {
            // When deleting the alarm, set alarm off to cancel pending intent
            alarm.setAlarmOff(this);
            mAlarmTextView.setText(R.string.text_view_alarm_off);

            // Use Content Resolver to send delete command to alarm content provider
            int rowsEffected = getContentResolver().delete(mCurrentAlarmUri, null, null);

            // If no row is effected then toast message "Delete alarm failed"
            // otherwise toast message "Delete alarm successful"
            if (rowsEffected == 0) {
                Toast.makeText(this, "Delete alarm failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Delete alarm successful", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // If this activity is opened to add new alarm then hide the delete item from option menu
        if (mCurrentAlarmUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        if (!mHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

        showUnsavedChangeDialog(discardClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                try {
                    saveAlarm();
                    finish();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAlarm();
                                finish();
                            }
                        };
                showDeleteConfirmationDialog(deleteClickListener);
                return true;

            case android.R.id.home:
                if (!mHasChanged) {
                    NavUtils.navigateUpFromSameTask(AlarmEditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(AlarmEditorActivity.this);
                            }
                        };
                showUnsavedChangeDialog(discardClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangeDialog(
            DialogInterface.OnClickListener discardClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_your_changes);
        builder.setPositiveButton(R.string.discard, discardClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(
            DialogInterface.OnClickListener deleteClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_alarm);
        builder.setPositiveButton(R.string.delete, deleteClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
