package com.example.a01.tutorialjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;



import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private TextView show_imei;
    private TextView show_tittle;
    private TextView show_description;
    private TextView show_percentage;
    private TextView show_get_location;
    private Button start_service;
    private Button stop_service;
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
        show_get_location = findViewById(R.id.tv_get_location);
        start_service = findViewById(R.id.btn_start_service);
        stop_service = findViewById(R.id.btn_stop_service);


        start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(v);
            }
        });
        stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(v);
            }
        });




        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        } else {
            getLocation();
        }


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

        getBattery();
        actionBar();

    }


    public void startService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationGPS != null) {
                double latitude = locationGPS.getLatitude();
                double longitude = locationGPS.getLongitude();

                get_latitude = String.valueOf(latitude);
                get_longitude = String.valueOf(longitude);

                show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
                        "Longitude: " + get_longitude);
            } else if (locationNetwork != null) {

                    double latitude =  locationNetwork.getLatitude();
                    double longitude = locationNetwork.getLongitude();


                    get_latitude = String.valueOf(latitude);
                    get_longitude = String.valueOf(longitude);

                    show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
                            "Longitude: " + get_longitude);

            } else if (locationPassive != null) {
                double latitude = locationPassive.getLatitude();
                double longitude = locationPassive.getLongitude();


                get_latitude = String.valueOf(latitude);
                get_longitude = String.valueOf(longitude);

                show_get_location.setText("MY LOCATION!" + "\n" + "Latitude: " + get_latitude + "\n" +
                        "Longitude: " + get_longitude);
            } else {
                Toast.makeText(this, "Can't get your location!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onGPS(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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
}
