package org.anhtran.alarmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by anhtran on 2/25/17.
 */

public class AlarmMainActivity extends AppCompatActivity {
    private TextView setAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        getView();

    }

    private void getView() {
        setAlarm = (TextView) findViewById(R.id.set_alarm);
        setAlarmListener();
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

}
