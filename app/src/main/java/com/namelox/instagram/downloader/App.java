package com.namelox.instagram.downloader;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFirebase();
        initializeAdMob();
        initializeOneSignal();
    }

    private void initializeFirebase() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all");
        } catch (Exception e) {
            // Handle Firebase initialization error
        }
    }

    private void initializeAdMob() {
        try {
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        } catch (Exception e) {
            // Handle AdMob initialization error
        }
    }

    private void initializeOneSignal() {
        try {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
            OneSignal.initWithContext(this);
            OneSignal.setAppId(getResources().getString(R.string.oneSignal_id));
        } catch (Exception e) {
            // Handle OneSignal initialization error
        }
    }
}
