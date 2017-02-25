package org.anhtran.alarmtest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by anhtran on 2/22/17.
 */

public class AlarmService extends Service {
    MediaPlayer ringtone;
    boolean isPlaying;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Get intent extra string
        String intentMsg = intent.getExtras().getString(AlarmSettingActivity.MESSAGE);

        //Start or stop ringtone
        switch (intentMsg) {
            //This case happens when Alarm On button is pressed
            case "start":
                //Check if ringtone is playing and
                if (!isPlaying) {
                    startAlarm();
                }
                break;
            //This case happens when Alarm Off button is pressed
            case "stop":
                if (isPlaying) {
                    stopAlarm();
                }
                break;
        }
        return START_NOT_STICKY;
    }

    //This method will start the alarm and add notification service
    private void startAlarm() {

        Log.e("AlarmService","Starting...");

        //Create media player ringtone
        ringtone = MediaPlayer.create(getApplicationContext(),R.raw.rythmoftherain);

        //Start ringtone
        ringtone.start();

        //The ringtone is playing
        isPlaying =true;
    }

    //This method will stop the alarm and notification service
    private void stopAlarm() {
        Log.e("AlarmService","Stop!");
        //Stop the ringtone
        ringtone.stop();
        ringtone.reset();

        //The ringtone is not playing
        isPlaying = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
