package org.anhtran.wakeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


public class AlarmService extends Service {
    private static final String LOG_TAG = AlarmService.class.getSimpleName();

    MediaPlayer ringtone;
    boolean isPlaying;

    Uri mCurrentAlarmUri;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOG_TAG, "onStartCommand() methods called");

        //Get intent extra string
        String intentMsg = intent.getExtras().getString(AlarmEditorActivity.MESSAGE);

        // Get URI of current alarm
        mCurrentAlarmUri = intent.getData();

        // If intentMsg is null, return early
        if (intentMsg == null) {
            return START_NOT_STICKY;
        }

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

        Log.e(LOG_TAG, "startAlarm() methods called");

        //Create media player ringtone
        ringtone = MediaPlayer.create(getApplicationContext(), R.raw.rythmoftherain);

        //Start ringtone
        ringtone.start();

        //The ringtone is playing
        isPlaying = true;

        NotificationManager notifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(getApplicationContext(), NotificationActivity.class);
        if (mCurrentAlarmUri != null) {
            notifyIntent.setData(mCurrentAlarmUri);
        } else {
            Log.e(LOG_TAG, "Uri is: " + mCurrentAlarmUri);
        }
        notifyIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Wake Up Alarm")
                .setContentText("Click to open")
                .setContentIntent(notifyPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_alarm_add)
                .build();

        notifyManager.notify(0, notification);
    }

    //This method will stop the alarm and notification service
    private void stopAlarm() {
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
