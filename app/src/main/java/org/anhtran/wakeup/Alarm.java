package org.anhtran.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

import static org.anhtran.wakeup.AlarmEditorActivity.MESSAGE;

/**
 * Created by anhtran on 2/26/17.
 */

class Alarm {
    private static final String LOG_TAG = Alarm.class.getSimpleName();

    private int hour;
    private int minute;
    private PendingIntent mServicePendingIntent;
    private AlarmManager alarmManager;
    private boolean isEnabled;
    private Intent intent;

    private Calendar calendar = Calendar.getInstance();

    /**
     * This class manages alarm information
     */
    Alarm() {
    }

    void enable() {
        isEnabled = true;
    }

    void disable() {
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Turn on the alarm
     */
    void setAlarmOn(Context context, Uri currentUri) {

        enable();

        intent = new Intent(context, AlarmReceiver.class);
        //Put extra information to intent
        intent.putExtra(MESSAGE, "start");
        intent.setData(currentUri);

        intent.setAction(Long.toString(System.currentTimeMillis()));

        //the pending intent to delay the intent
        mServicePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarm manager
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set exact time for alarm manager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getAlarmTimeInMillis(), mServicePendingIntent);
    }

    /**
     * Turn off the alarm
     */
    void setAlarmOff(Context context) {

        // Alarm is disabled
        disable();

        intent = new Intent(context, AlarmReceiver.class);
        //put intent extra to send stop message to alarm receiver
        intent.putExtra(MESSAGE, "stop");

        //send broadcast
        context.sendBroadcast(intent);

        // Cancel alarm that has been set
        if (alarmManager != null) {
            alarmManager.cancel(mServicePendingIntent);
        }
    }

    /**
     * Get time in millisecond from time picker
     *
     * @return time in millisecond
     */

    long getAlarmTimeInMillis() {

        //Set calendar to current time
        calendar.setTimeInMillis(System.currentTimeMillis());

        //Set calendar hour and minute by time picker hour and minute
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        //if alarm time in millisecond is less than current system time in millisecond,
        // alarm should be set to next day
        long calendarTime = calendar.getTimeInMillis();

        // Compare calendar time in millis with system current time in millis,
        // if system time is greater than calendar time,
        // the alarm time will be added 1 more day
        if (calendarTime < System.currentTimeMillis()) {
            calendarTime = calendarTime + 86400000;
        }

        return calendarTime;
    }

}
