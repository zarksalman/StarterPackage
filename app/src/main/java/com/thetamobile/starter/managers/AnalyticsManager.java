package com.thetamobile.starter.managers;

import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.thetamobile.starter.Application;
import com.thetamobile.starter.helpers.AppConstants;


public class AnalyticsManager {

    private static AnalyticsManager manager;
    private FirebaseAnalytics firebaseAnalytics;

    private AnalyticsManager() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(Application.getContext());
    }

    public static AnalyticsManager getInstance() {
        if (manager == null) {
            manager = new AnalyticsManager();
        }
        return manager;
    }

    public void sendAnalytics(String actionDetail, String actionName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, (actionDetail));
        bundle.putString(AppConstants.ACTION_TYPE, actionName);
        firebaseAnalytics.logEvent(actionName, bundle);
    }
}
