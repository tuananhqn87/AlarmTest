package org.anhtran.alarmtest;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlarmSettingActivity extends AppCompatActivity {
    final static String MESSAGE = "msg";

    private TimePicker timePicker;
    private Button alarmOn;
    private Button alarmOff;
    private TextView alarmView;
    private AlarmManager alarmManager;
    private Calendar calendar = Calendar.getInstance();
    private Intent broadcastIntent;
    private PendingIntent pendingIntent;


    /**
     * Method to create activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The intent to send to alarm receiver
        broadcastIntent = new Intent(this, AlarmReceiver.class);

        //Add view control
        addControl();

        //Set calendar to current time
        calendar.setTimeInMillis(System.currentTimeMillis());

        //Listen to set alarm action
        setAlarmListener();

        //Listen to cancel alarm action
        cancelAlarmListener();
    }


    /**
     * Method to add view control
     */
    private void addControl() {
        // Add timepicker
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        //Add alarm on button
        alarmOn = (Button) findViewById(R.id.btn_on);

        //Add alarm off button
        alarmOff = (Button) findViewById(R.id.btn_off);

        //Add alarm text view
        alarmView = (TextView) findViewById(R.id.alarm_view);
    }

    /**
     * Set on click listener to turn on the alarm
     */
    private void setAlarmListener() {
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Turn on alarm
                setAlarmOn();
            }
        });
    }

    /**
     * Set on click listener to turn off the alarm
     */
    private void cancelAlarmListener() {
        alarmOff.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //Turn off alarm
                setAlarmOff();
            }
        });
    }

    /**
     * Turn on the alarm
     */
    private void setAlarmOn() {

        //Put extra information to intent
        broadcastIntent.putExtra(MESSAGE,"start");

        //the pending intent to delay the intent until time's up
        pendingIntent = PendingIntent.getBroadcast(this,0,broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarm manager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,getAlarmTimeInMillis(),pendingIntent);

        //Format the date
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");

        //Text to show the next alarm time
        String text = "Alarm on";
        text = text + "\n" + format.format(getAlarmTimeInMillis());

        //Set text view to alarm time
        alarmView.setText(text);

    }

    /**
     * Turn off the alarm
     */
    private void setAlarmOff() {
        //put intent extra to send stop message to alarm receiver
        broadcastIntent.putExtra(MESSAGE,"stop");

        //send broadcast
        sendBroadcast(broadcastIntent);

        //Cancel alarm has been set
        if(alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        //Set text view to "Alarm Off"
        alarmView.setText(R.string.alarm_off);
    }

    /**
     * Get time in millisecond
     * @return time in millisecond
     */
    public long getAlarmTimeInMillis() {

        //if alarm time in millisecond is less than current system time in millisecond,
        // alarm should be set to next day
        long calendarTime = getAlarmCalendar().getTimeInMillis();

        // Compare calendar time in millis with system current time in millis,
        // if system time is greater than calendar time,
        // the alarm time will be added 1 more day
        if(calendarTime < System.currentTimeMillis()) {
            calendarTime = calendarTime + (24*60*60*1000);
        }

        return calendarTime;
    }

    /**
     * Get alarm calendar that has been set by time picker
     * @return calendar of alarm
     */
    @TargetApi(Build.VERSION_CODES.M)
    public Calendar getAlarmCalendar() {
        //Set calendar hour and minute by timepicker hour and minute
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        return calendar;
    }

}
