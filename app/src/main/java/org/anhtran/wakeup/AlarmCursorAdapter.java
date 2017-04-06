package org.anhtran.wakeup;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.anhtran.wakeup.data.AlarmContract.AlarmEntry;

import java.text.SimpleDateFormat;


/**
 * Created by anhtran on 2/26/17.
 */

public class AlarmCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = AlarmCursorAdapter.class.getSimpleName();

    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        long datetime = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_ALARM_DATETIME));

        TextView alarmTimeView = (TextView) view.findViewById(R.id.alarm_time);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss aaa");
        String time = timeFormatter.format(datetime);
        alarmTimeView.setText(time);

        TextView alarmDateView = (TextView) view.findViewById(R.id.alarm_date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM dd");
        String date = dateFormatter.format(datetime);
        alarmDateView.setText(date);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_ALARM_NAME));
        TextView alarmNameView = (TextView) view.findViewById(R.id.alarm_name);
        if (TextUtils.isEmpty(name)) {
            name = "Name is empty";
        }
        alarmNameView.setText(name);

        TextView alarmStateView = (TextView) view.findViewById(R.id.alarm_state);
        GradientDrawable stateCircle = (GradientDrawable) alarmStateView.getBackground();

        int state = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_ALARM_STATE));
        int stateResourceId;
        switch (state) {
            case AlarmEntry.STATE_OFF:
                alarmStateView.setText(R.string.alarm_list_activity_state_off);
                stateResourceId = R.color.colorStateOff;
                stateCircle.setColor(ContextCompat.getColor(view.getContext(), stateResourceId));
                break;
            case AlarmEntry.STATE_ON:
                alarmStateView.setText(R.string.alarm_list_activity_state_on);
                stateResourceId = R.color.colorStateOn;
                stateCircle.setColor(ContextCompat.getColor(view.getContext(), stateResourceId));
                break;
        }

    }
}
