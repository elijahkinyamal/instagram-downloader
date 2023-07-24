package com.namelox.instagram.downloader;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(getResources().getString(R.string.oneSignal_id));
    }

}
