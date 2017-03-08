package org.anhtran.alarmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anhtran on 2/25/17.
 */

public class AlarmMainActivity extends AppCompatActivity {
    private TextView setAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        addControl();
        setAlarmListener();
        showAlarmList();
    }

    private void addControl() {
        setAlarm = (TextView) findViewById(R.id.set_alarm);
    }

    private void setAlarmListener() {
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmMainActivity.this,AlarmSettingActivity.class);
                startActivity(i);
            }
        });
    }

    private void showAlarmList() {
        ArrayList<Alarm> alarms = new ArrayList<>();
        alarms.add(new Alarm(14,3,20));
        alarms.add(new Alarm(5,15,30));

        AlarmAdapter alarmAdapter = new AlarmAdapter(getApplicationContext(),alarms);
        ListView alarmList = (ListView)findViewById(R.id.list_alarm);

        alarmList.setAdapter(alarmAdapter);
    }


}
