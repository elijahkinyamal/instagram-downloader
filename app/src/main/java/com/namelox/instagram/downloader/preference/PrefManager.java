package com.namelox.instagram.downloader.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private static final String VIDEODOWNLOADER = "VIDEODOWNLOADER";
    private static final String PREF_NAME = "AwesomeWallpapers";
    private static final String TAG = "PrefManager";
    private static String fileName = "fileName";
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    public PrefManager(Context context) {
        this._context = context;
        this.pref = context.getSharedPreferences(PREF_NAME, 0);
    }

    public static void setRated(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(VIDEODOWNLOADER, 0).edit();
        edit.putBoolean("is_rated_2", z);
        edit.apply();
    }

}