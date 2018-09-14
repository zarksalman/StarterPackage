package com.thetamobile.starter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.thetamobile.starter.R;
import com.thetamobile.starter.managers.NotificationManager;
import com.thetamobile.starter.managers.SharedPreferencesManager;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SharedPreferencesManager.getInstance().getBoolean(context.getString(R.string.alarm))) {
            String title = context.getString(R.string.app_name);
            String text = context.getString(R.string.notification_text);
            NotificationManager.getInstance().generateNotification(context, title, text);
        }
    }
}