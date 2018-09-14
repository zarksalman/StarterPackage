package com.thetamobile.starter.views.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.thetamobile.starter.R;
import com.thetamobile.starter.managers.ActivityManager;
import com.thetamobile.starter.managers.AlarmManager;
import com.thetamobile.starter.managers.SharedPreferencesManager;


public class SplashActivity extends AppCompatActivity {

    private boolean isDestroyed = false;
    private final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (!isDestroyed) {
                if (!SharedPreferencesManager.getInstance().getBoolean(getString(R.string.is_first_run))) {
                    setDefaultPreferences();
                }
                ActivityManager.getInstance().openNewActivity(SplashActivity.this, MainActivity.class, false);
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    private void setDefaultPreferences() {
        SharedPreferencesManager.getInstance().setInt(getString(R.string.hour), 8);
        SharedPreferencesManager.getInstance().setInt(getString(R.string.minute), 0);
        SharedPreferencesManager.getInstance().setInt(getString(R.string.language), 0);
        SharedPreferencesManager.getInstance().setBoolean(getString(R.string.alarm), true);
        SharedPreferencesManager.getInstance().setBoolean(getString(R.string.is_first_run), true);
        // set the alarm at 8:00 AM
        AlarmManager.getInstance().setAlarm(this, 8, 0);
    }
}
