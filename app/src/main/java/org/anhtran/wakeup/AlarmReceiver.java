package org.anhtran.wakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


/**
 * Created by anhtran on 2/22/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = AlarmReceiver.class.getSimpleName();
    Intent intentService;

    @Override
    public void onReceive(Context context, Intent intent) {
        //This intent to send to AlarmService
        intentService = new Intent(context, AlarmService.class);

        //Intent extra msg
        String intentMsg = intent.getExtras().getString(AlarmEditorActivity.MESSAGE);

        // Intent Uri Data
        Uri intentDataUri = intent.getData();

        //Forward intent extra to service class
        intentService.putExtra(AlarmEditorActivity.MESSAGE, intentMsg);
        intentService.setData(intentDataUri);

        //The intent to send to alarm service class
        context.startService(intentService);

    }
}
