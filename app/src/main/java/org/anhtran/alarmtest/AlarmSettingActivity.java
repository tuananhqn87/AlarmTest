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
    Intent broadcastIntent;
    private PendingIntent pendingIntent;


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


    // Method to add view control
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

    private void setAlarmListener() {
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Turn on alarm
                setAlarmOn();
            }
        });
    }

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

    @TargetApi(Build.VERSION_CODES.M)
    private void setAlarmOn() {
        //Set calendar hour and minute by timepicker hour and minute
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        //Put extra information to intent
        broadcastIntent.putExtra(MESSAGE,"start");

        //the pending intent to delay the intent until time's up
        pendingIntent = PendingIntent.getBroadcast(this,0,broadcastIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarm manager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //if alarm time in millisecond is less than current system time in millisecond,
        // alarm should be set to next day
        long calendarTime = calendar.getTimeInMillis();
        if(calendarTime < System.currentTimeMillis()) {
            calendarTime = calendarTime + (24*60*60*1000);
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendarTime,pendingIntent);

        //Format the date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String text = "Alarm on";
        text = text + "\n" + format.format(calendarTime);

        //Set text view to alarm time
        alarmView.setText(text);

    }

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

}
