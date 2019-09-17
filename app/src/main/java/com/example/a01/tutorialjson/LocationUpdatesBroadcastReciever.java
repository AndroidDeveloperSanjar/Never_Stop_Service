package com.example.a01.tutorialjson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationUpdatesBroadcastReciever extends BroadcastReceiver {
    public static final String TAG = "Broad_Cast_Receiver";
    public static final String ACTION_PROCESS_UPDATES = "Action_Process_Updates";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    List<Location> locations = result.getLocations();
                    LocationResultHelper  locationResultHelper = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        locationResultHelper = new LocationResultHelper(
                                    context,locations);
                    }
                    locationResultHelper.saveResults();
                    locationResultHelper.showNotification();
                    Log.i(TAG,LocationResultHelper.getSavedLocationResult(context));
                }
            }
        }
    }
}
