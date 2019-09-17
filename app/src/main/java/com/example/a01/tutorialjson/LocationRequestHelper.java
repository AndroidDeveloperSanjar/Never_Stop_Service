package com.example.a01.tutorialjson;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;

public class LocationRequestHelper {
    final static String KEY_LOACTION_UPDATES_REQUESTED = "location_updates_requested";

    static void setRequesting(Context context,boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOACTION_UPDATES_REQUESTED,value)
                .apply();
    }

    static boolean getRequesting(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOACTION_UPDATES_REQUESTED,false);
    }
}
