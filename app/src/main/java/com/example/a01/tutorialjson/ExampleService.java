package com.example.a01.tutorialjson;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;


public class ExampleService extends Service {
    private int counter = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "myServiceID";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    public void noticationHelper(Context context,String message){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("My_notification");
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.build();

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(1, mBuilder.build());
        startForeground(1,mBuilder.build());
    }
    private Timer timer;
    private TimerTask timerTask;

    public void startTimer(){
        timer = new Timer();
        initTimerTask();
        timer.schedule(timerTask,2000,2000);
    }
    public void initTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                noticationHelper(getApplicationContext(),"Data " + (counter++));
            }
        };
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
