package com.thetamobile.starter.helpers;

import android.util.Log;
import com.thetamobile.starter.BuildConfig;


public class LogHelper {

    public static void logE(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void logD(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void logI(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message);
        }
    }
}
