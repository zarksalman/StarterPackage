package com.thetamobile.starter.helpers;

import android.widget.Toast;
import com.thetamobile.starter.Application;
import com.thetamobile.starter.BuildConfig;


public class ToastHelper {

    public static void makeToast(String text) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(Application.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
