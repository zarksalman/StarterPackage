package com.thetamobile.starter;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.MobileAds;
import com.thetamobile.starter.managers.AdsManager;


public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
/*        TTSManager.getInstance();
        Stetho.initializeWithDefaults(this);
        if (BuildConfig.DEBUG) {
            AdSettings.addTestDevice("4e7eec09-d8cb-49f4-aa6c-b3bcf29076d9");
        }
        MobileAds.initialize(context, context.getString(R.string.app_id));
        AdsManager.getInstance();*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }
}
