package org.anhtran.alarmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by anhtran on 2/22/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Intent intentService;

    @Override
    public void onReceive(Context context, Intent intent) {
        //This intent to send to AlarmService
        intentService = new Intent(context,AlarmService.class);

        //Intent extra msg
        String intentMsg = intent.getExtras().getString(AlarmSettingActivity.MESSAGE);

        //Forward intent extra to service class
        intentService.putExtra(AlarmSettingActivity.MESSAGE,intentMsg);

        //The intent to send to alarm service class
        context.startService(intentService);

    }
}
