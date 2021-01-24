package com.example.bootreceivertest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class ForegroundService extends Service {

    public static final String FOREGROUND_SERVICE_CHANNEL = "Foreground Service Channel";
    public static final String NOTIFICATION_CHANNEL = "Notification Channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setup();
        //Faccio cose


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setup(){
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, FOREGROUND_SERVICE_CHANNEL)
                .setContentTitle("Ci stiamo prendendo cura di te")
                // .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        int NOTIFICATION_ID_SERVICE = 1;
        startForeground(NOTIFICATION_ID_SERVICE, notification);
    }

    //CREO IL CANALE DELLE NOTIFICHE
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannelForeground = new NotificationChannel(FOREGROUND_SERVICE_CHANNEL, "Foreground",  android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannelForeground.setDescription("Mostra la notifica del Foreground Service");
            android.app.NotificationManager notificationManagerForeground = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManagerForeground.createNotificationChannel(notificationChannelForeground);

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, "Notifiche",  android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Mostra le notifiche degli eventi");
            android.app.NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
