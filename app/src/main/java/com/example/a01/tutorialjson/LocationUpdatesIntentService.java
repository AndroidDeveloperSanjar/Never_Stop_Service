package com.example.a01.tutorialjson;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationUpdatesIntentService extends IntentService {
    static final String ACTION_PROCESS_UPDATES =
            "Action_Process_Updates";
    public static final String TAG = LocationUpdatesIntentService.class.getSimpleName();
    public LocationUpdatesIntentService(String name) {
        super(TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action  = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    List<Location> locations = result.getLocations();
                    LocationResultHelper locationResultHelper = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        locationResultHelper = new LocationResultHelper(this,locations);
                    }
                    locationResultHelper.saveResults();
                    locationResultHelper.showNotification();
                    Log.i(TAG,LocationResultHelper.getSavedLocationResult(this));
                }
            }
        }
    }
}
