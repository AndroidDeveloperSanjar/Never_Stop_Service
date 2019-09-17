package com.example.a01.tutorialjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final long UPDATE_INTERVAL = 10 * 1000;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    private TextView show_imei;
    private TextView show_tittle;
    private TextView show_description;
    private TextView show_percentage;
    private TextView location_show;
    private Button get_request_updates;
    private Button get_remove_updates;
    private String get_imei_1;
    private String get_imei_2;
    private String get_longitude;
    private String get_latitude;
    private int progress_status;
    private TelephonyManager telephonyManager;
    private LocationManager locationManager;
    private ProgressBar progress_bar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        show_imei = findViewById(R.id.tv_imei);
        show_tittle = findViewById(R.id.tv_tittle);
        show_description = findViewById(R.id.tv_description);
        progress_bar = findViewById(R.id.progress_bar);
        show_percentage = findViewById(R.id.tv_percentage);
        get_request_updates = findViewById(R.id.request_updates_button);
        get_remove_updates = findViewById(R.id.remove_updates_button);
        location_show = findViewById(R.id.location_updates_result);

        //background service location permissions
        if (!checkPermissions()){
            requestPermissions();
        }
        buildGoogleApiClient();

//        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            onGPS();
//        } else {
//            getLocation();
//        }


        //get imei
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        } else {
            //TODO
        }
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            get_imei_1 = telephonyManager.getDeviceId(1);
            get_imei_2 = telephonyManager.getDeviceId(2);
            show_imei.setText("IMEI 1: " + get_imei_1 + "\n" + "IMEI 2: " + get_imei_2);
        }

        //get time and date
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView show_date_and_time = findViewById(R.id.tv_date_and_time);
                                long date_and_time = System.currentTimeMillis();
                                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyy\nhh:mm:ss ");
                                String get_date_and_time = format.format(date_and_time);
                                show_date_and_time.setText("DATE and TIME" + "\n" + get_date_and_time);
                            }
                        });
                    }

                }catch (InterruptedException e){

                }
            }
        };thread.start();

    //Other methods
        getBattery();
        actionBar();

    }




//    public void getLocation() {
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        } else {
//            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//
//            if (locationGPS != null) {
//                double latitude = locationGPS.getLatitude();
//                double longitude = locationGPS.getLongitude();
//
//                get_latitude = String.valueOf(latitude);
//                get_longitude = String.valueOf(longitude);
//
//                show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
//                        "Longitude: " + get_longitude);
//            } else if (locationNetwork != null) {
//
//                    double latitude =  locationNetwork.getLatitude();
//                    double longitude = locationNetwork.getLongitude();
//
//
//                    get_latitude = String.valueOf(latitude);
//                    get_longitude = String.valueOf(longitude);
//
//                    show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
//                            "Longitude: " + get_longitude);
//
//            } else if (locationPassive != null) {
//                double latitude = locationPassive.getLatitude();
//                double longitude = locationPassive.getLongitude();
//
//
//                get_latitude = String.valueOf(latitude);
//                get_longitude = String.valueOf(longitude);
//
//                show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
//                        "Longitude: " + get_longitude);
//            } else {
//                Toast.makeText(this, "Can't get your location!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    public void onGPS(){
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    public void getBattery(){
        android.content.Context context = getApplicationContext();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBroadcastReceiver,intentFilter);

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String charging_status = "" , power_source = "Unplugged";

            int charge_plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
            if (charge_plug == BatteryManager.BATTERY_PLUGGED_USB){
                power_source = "USB";
            }
            if (charge_plug == BatteryManager.BATTERY_PLUGGED_AC){
                power_source = "AC";
            }
            if (charge_plug == BatteryManager.BATTERY_PLUGGED_WIRELESS){
                power_source = "WIRELESS";
            }

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                charging_status = "CHARGING";
            }
            if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){
                charging_status = "DISCHARGING";
            }
            if (status == BatteryManager.BATTERY_STATUS_FULL){
                charging_status = "BATTERY FULL";
            }
            if (status == BatteryManager.BATTERY_STATUS_UNKNOWN){
                charging_status = "UNKNOWN";
            }
            if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                charging_status = "NOT CHARGING";
            }

            //display the output of battery status
            show_tittle.setText(
                    "Power Source:\n"+
                            "Charging Status: "
            );
            show_description.setText(
                    " " + power_source + "\n" +
                            " " + charging_status);

            int levels = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
            float percentage = levels/(float)scale;
            //update the progress bar to display current battery charched percentage
            progress_status = (int)((percentage)*100);
            show_percentage.setText("" + progress_status +"%");
            //display the battery charged percentage in progress bar
            progress_bar.setProgress(progress_status);
        }
    };
    public void actionBar(){
        // actionbar
        ActionBar actionBar = getSupportActionBar();
        //set tittle
        if (actionBar != null){
            actionBar.setTitle("My_Project");
            //set back button in actionbar
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location_show.setText(LocationResultHelper.getSavedLocationResult(this));
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private void buildGoogleApiClient(){
        if (googleApiClient != null){
            return;
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this,this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"GoogleApiClient connected!");
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(this,LocationUpdatesBroadcastReciever.class);
        intent.setAction(LocationUpdatesBroadcastReciever.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        final String text = "Connection suspended!";
        Log.w(TAG,text + ": Error code: " + i);
        showSnackbar("Connection suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        final String text = "Exception while connecting to Google Play services";
        Log.w(TAG, text + ": " + connectionResult.getErrorMessage());
        showSnackbar(text);
    }

    private void showSnackbar(final String text){
        View container = findViewById(R.id.activity_main);
        if (container != null){
            Snackbar.make(container,text,Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale){
            Log.i(TAG,"Displaying permission ratioanale to provide additional context.");
            Snackbar.make(findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat .requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();
        }else {
            Log.i(TAG,"Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG,"onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            if (grantResults.length <= 0){
                Log.i(TAG,"User interaction was cancelled.");
            }else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
            }else {
                Snackbar.make(findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",BuildConfig.APPLICATION_ID,null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(LocationResultHelper.KEY_LOCATION_UPDATES_RESULT)) {
            location_show.setText(LocationResultHelper.getSavedLocationResult(this));
        }
    }

    public void requestLocationUpdates(View view){
        try{
            Log.i(TAG,"Starting location update");
            LocationRequestHelper.setRequesting(this,true);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,locationRequest,getPendingIntent());
        }catch (SecurityException e){
            LocationRequestHelper.setRequesting(this,false);
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates(View view){
        Log.i(TAG,"Removing location updates");
        LocationRequestHelper.setRequesting(this,false);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                getPendingIntent());
    }
}
