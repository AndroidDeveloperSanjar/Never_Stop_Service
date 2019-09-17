package com.example.a01.tutorialjson;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class LocationResultHelper {
    final static String KEY_LOCATION_UPDATES_RESULT = "Location_Updates_Result";
    final private static String PRIMARY_CHANNEL = "default";
    private Context context;
    private List<Location> locations;
    private NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocationResultHelper(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;

        NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,
                context.getString(R.string.default_channel),NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(channel);

    }

    private String getLocationResultTittle(){
        String numlocationsReported = context.getResources().getQuantityString(
                R.plurals.num_locations_reported,locations.size(),locations.size());
        return numlocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }

    private String getLoctionResultText(){
        if (locations.isEmpty()){
            return context.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location mLocation : locations){
            sb.append("(");
            sb.append(mLocation.getLatitude());
            sb.append(", ");
            sb.append(mLocation.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    void saveResults(){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT,getLocationResultTittle() + "\n" +
                        getLoctionResultText())
                .apply();
    }

    static String getSavedLocationResult(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(KEY_LOCATION_UPDATES_RESULT,"");
    }

    private NotificationManager getNotificationManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager)context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void showNotification(){
        Intent notificationIntent = new Intent(context,MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context,
                    PRIMARY_CHANNEL)
                    .setContentTitle(getLocationResultTittle())
                    .setContentText(getLoctionResultText())
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }
        getNotificationManager().notify(0,notificationBuilder.build());
    }
}
