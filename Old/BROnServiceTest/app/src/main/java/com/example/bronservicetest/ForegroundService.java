package com.example.bronservicetest;

import android.app.AlarmManager;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;


public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_TRASFUSIONE = "TRASFUSIONE";
    public static final int NOTIFICATION_ID = 2;
    private static boolean ServiceOn = false;
    public static BroadcastReceiver NotificationReceiver;
    public static AlarmManager alarmManager;
    String id = "1";

    public static boolean isServiceOn() {
        return ServiceOn;
    }
    public static void setServiceOn(boolean serviceOn) {
        ServiceOn = serviceOn;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerNotificationReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            setup(intent);


            //Faccio cose


        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(NotificationReceiver);
        NotificationReceiver = null;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void registerNotificationReceiver(){
        NotificationReceiver = new NotificationReceiver();

        IntentFilter filter = new IntentFilter(ACTION_TRASFUSIONE);
        registerReceiver(NotificationReceiver, filter);

        Log.d("NOTIFICATION", "SETTO");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentTest = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentTest.setAction(ACTION_TRASFUSIONE);
        intentTest.putExtra("KEY", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(id), intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 5000, pendingIntent);
    }

    private void setup(Intent intent){
        //String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ci stiamo prendendo cura di te")
                // .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        setServiceOn(true);
    }

    public static void startService(Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(context, serviceIntent);
    }


}
