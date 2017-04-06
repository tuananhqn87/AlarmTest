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

        //Create media player ringtone
        ringtone = MediaPlayer.create(getApplicationContext(), R.raw.nature_alarm);

        //Start ringtone
        ringtone.start();

        //The ringtone is playing
        isPlaying = true;

        // Initialize notification manager by getting system notification service
        NotificationManager notifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Define intent to open notification activity when click on notification
        Intent notifyIntent = new Intent(getApplicationContext(), NotificationActivity.class);

        // Set data to intent to help notification activity retrieve alarm info
        if (mCurrentAlarmUri != null) {
            notifyIntent.setData(mCurrentAlarmUri);
        }
        // Set action by an ID string to help the intent is unique
        notifyIntent.setAction(Long.toString(System.currentTimeMillis()));

        // Declare pending intent that is set to notification content intent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);

        // Build the notification
        Notification notification = new NotificationCompat.Builder(this)
                // Set the title of notification
                .setContentTitle("Wake Up Alarm")
                // Set the content of notification
                .setContentText("Click to open")
                // Set the intent of notification
                .setContentIntent(notifyPendingIntent)
                // Set default notification behavior
                .setDefaults(Notification.DEFAULT_ALL)
                // Set priority to high to make notification show on the head of screen
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Auto disappear when notification is clicked on
                .setAutoCancel(true)
                // Set the icon for notification
                .setSmallIcon(R.drawable.ic_alarm_add)
                // Build notification
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
