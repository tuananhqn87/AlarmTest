package org.anhtran.alarmtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anhtran on 2/26/17.
 */

public class AlarmAdapter extends ArrayAdapter<Alarm> {

    public AlarmAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context, 0, alarms);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.alarm_view,parent,false);
        }

        Alarm currentAlarm = (Alarm) getItem(position);

        TextView alarmTime = (TextView) listItemView.findViewById(R.id.alarm_text);
        alarmTime.setText(currentAlarm.toString());

        return listItemView;
    }
}
