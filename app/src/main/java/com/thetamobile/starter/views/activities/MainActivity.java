package com.thetamobile.starter.views.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.thetamobile.starter.BuildConfig;
import com.thetamobile.starter.R;
import com.thetamobile.starter.database.AppDatabase;
import com.thetamobile.starter.databinding.ActivityMainBinding;
import com.thetamobile.starter.entities.HouseAds;
import com.thetamobile.starter.managers.ActivityManager;
import com.thetamobile.starter.managers.AdsManager;
import com.thetamobile.starter.managers.AnalyticsManager;
import com.thetamobile.starter.managers.SharedPreferencesManager;
import com.thetamobile.starter.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ConsentForm form;
    private AlertDialog exitDialog;
    private List<HouseAds> houseAds;
    private int currentPromotion = 1;
    private ActivityMainBinding binding;
    private CountDownTimer countDownTimer;
    private boolean isAppInBackground = false;
    private ConsentInformation consentInformation;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.layoutNavigationDrawer.setListener(this);

        setSupportActionBar(binding.appBarMain.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        prepareExitDialog();
        //initializeInHousePromotion();
        consentInformation = ConsentInformation.getInstance(this);
        requestGoogleConsentForm(true);
        AnalyticsManager.getInstance().sendAnalytics(TAG, "Activity opened");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setupInHousePromotion();
        isAppInBackground = false;
    }

    @Override
    protected void onPause() {
        isAppInBackground = true;
        cancelCountDownTimer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelCountDownTimer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    private void requestGoogleConsentForm(boolean isAppLaunch) {
        if (BuildConfig.DEBUG) {
            consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
            consentInformation.addTestDevice("8F14986600C19D5CB98F0125581FBBF4");
        }
        consentInformation.requestConsentInfoUpdate(new String[]{getString(R.string.publisher_id)}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, consentStatus.name());
                if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                    // user is located in EEA, consent is required
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        showGoogleConsentForm();
                    }
                } else {
                    if (!isAppLaunch) {
                        showPrivacyPolicy();
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update
                Log.d(TAG, "onFailedToUpdateConsentInfo: " + errorDescription);
            }
        });
    }

    private void showGoogleConsentForm() {
        // consent not provided, collect consent using Google-rendered consent form
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getString(R.string.privacy_policy_url));
        } catch (MalformedURLException e) {
            Log.e(TAG, "onConsentInfoUpdated: " + e.getLocalizedMessage());
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "onConsentFormLoaded");
                        if (!isAppInBackground) {
                            form.show();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        Log.d(TAG, "onConsentFormClosed");
                        if (consentStatus == ConsentStatus.PERSONALIZED) {
                            SharedPreferencesManager.getInstance().setBoolean(getString(R.string.npa), false);
                        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                            SharedPreferencesManager.getInstance().setBoolean(getString(R.string.npa), true);
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        Log.d(TAG, "onConsentFormError: " + errorDescription);
                    }
                })
                .build();
        form.load();
    }

    @SuppressLint("InflateParams")
    private void prepareExitDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null);

        AdView banner = dialogView.findViewById(R.id.adView);
        if (banner != null) {
            AdsManager.getInstance().showBanner(banner);
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        exitDialog = dialogBuilder.create();
        exitDialog.setView(dialogView);
        exitDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.rate_us), (dialog, which) -> showRatingDialog());
        exitDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel), (dialog, which) -> {

        });
        exitDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(android.R.string.yes), (dialog, which) -> {
            isAppInBackground = true;
            AnalyticsManager.getInstance().sendAnalytics(TAG, "App closed");
            MainActivity.this.finish();
        });
    }

    private void initializeInHousePromotion() {
        if (Utils.isNetworkAvailable(this)) {
            new Thread(() -> {
                AppDatabase appDatabase = AppDatabase.getAppDatabase();
                if (appDatabase != null) {
                    int count = appDatabase.houseAdsDao().getCount();
                    if (count == 0) {
                        // open the house ads promotion file stream located at S3
                        try {
                            URL url = new URL(getString(R.string.house_ads));
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                                String inputLine;
                                StringBuilder json = new StringBuilder();
                                while ((inputLine = in.readLine()) != null) {
                                    json.append(inputLine);
                                }
                                in.close();
                                // insert the json in Room DB
                                if (json.length() > 0) {
                                    houseAds = new Gson().fromJson(json.toString(), new TypeToken<List<HouseAds>>() {
                                    }.getType());
                                    if (houseAds != null && houseAds.size() > 0) {
                                        appDatabase.houseAdsDao().insertAll(houseAds);
                                    }
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "initializeInHousePromotion -> IOException:" + e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        } catch (MalformedURLException e) {
                            Log.e(TAG, "initializeInHousePromotion -> MalformedURLException: " + e.getLocalizedMessage());
                        }
                    } else {
                        houseAds = appDatabase.houseAdsDao().getAll();
                    }
                    // setup the in house promotion
                    runOnUiThread(this::setupInHousePromotion);

                }
            }).start();
        }
    }

    private void setupInHousePromotion() {
        if (houseAds != null && houseAds.size() > 0) {
            String url = houseAds.get(currentPromotion - 1).getIcon();
            Picasso.get().load(url).into(binding.layoutNavigationDrawer.houseAd);
            countDownTimer = new CountDownTimer((houseAds.get(currentPromotion - 1).getInterval() * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (houseAds != null && houseAds.size() > 0) {
                        if (currentPromotion < houseAds.size()) {
                            currentPromotion++;
                        } else {
                            currentPromotion = 1;
                        }
                        setupInHousePromotion();
                    }
                }
            }.start();
        }
    }

    private void cancelCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showExitDialog() {
        if (exitDialog != null) {
            exitDialog.show();
            exitDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            exitDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTransformationMethod(null);
            exitDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            exitDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTransformationMethod(null);
            exitDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            exitDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTransformationMethod(null);
        }
    }

    @SuppressLint("InflateParams")
    private void showRatingDialog() {
        AnalyticsManager.getInstance().sendAnalytics(TAG, "Rate us clicked");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null);
        final AppCompatRatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setView(dialogView);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.no_thanks), (dialog, which) -> {

        });
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTransformationMethod(null);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if (ratingBar1.getRating() >= 4 && ratingBar1.getRating() <= 5) {
                rateUs();
            } else {
                AlertDialog feedbackDialog = dialogBuilder.create();
                feedbackDialog.setTitle(getString(R.string.uhoh));
                feedbackDialog.setMessage(getString(R.string.how_can_we_improve));
                feedbackDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.feedback), (dialog, which) ->
                        ActivityManager.getInstance().openNewActivity(MainActivity.this, FeedbackActivity.class, true));
                feedbackDialog.show();
                feedbackDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTransformationMethod(null);
            }
            alertDialog.dismiss();
        });
    }

    private void showPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://s3-us-west-2.amazonaws.com/thetaapps/PrivacyPolicy.htm"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }

    private void rateUs() {
        String url;
        try {
            MainActivity.this.getPackageManager().getPackageInfo("com.android.vending", 0);
            url = "market://details?id=" + getPackageName();
        } catch (final Exception e) {
            url = "https://play.google.com/store/apps/details?id=" + getPackageName();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivity(intent);
    }

    public void onSettingsClicked() {
        onBackPressed();
        new Handler().postDelayed(() -> ActivityManager.getInstance().openNewActivity(MainActivity.this, SettingsActivity.class, true), 500);
    }

    public void onFeedbackClicked() {
        onBackPressed();
        new Handler().postDelayed(() -> ActivityManager.getInstance().openNewActivity(MainActivity.this, FeedbackActivity.class, true), 500);
    }

    public void onMoreAppsClicked() {
        onBackPressed();
        new Handler().postDelayed(() -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=Theta+Mobile")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Theta+Mobile")));
            }
        }, 500);
        AnalyticsManager.getInstance().sendAnalytics("More Apps", "Clicked");
    }

    public void onPrivacyPolicyClicked() {
        onBackPressed();
        new Handler().postDelayed(() -> {
            consentInformation.setConsentStatus(ConsentStatus.UNKNOWN);
            requestGoogleConsentForm(false);
        }, 500);
        AnalyticsManager.getInstance().sendAnalytics("Privacy Policy", "Clicked");
    }
}
