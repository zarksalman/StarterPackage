package com.thetamobile.starter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thetamobile.starter.R;
import com.thetamobile.starter.managers.AlarmManager;
import com.thetamobile.starter.managers.SharedPreferencesManager;


public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SharedPreferencesManager.getInstance().getBoolean(context.getString(R.string.alarm))) {
            int hour = Integer.parseInt(SharedPreferencesManager.getInstance().getString(context.getString(R.string.hour)));
            int minute = Integer.parseInt(SharedPreferencesManager.getInstance().getString(context.getString(R.string.minute)));
            AlarmManager.getInstance().setAlarm(context, hour, minute);
        }
    }
}
