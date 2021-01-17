package com.example.talapp.Notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.talapp.Utils.Util;

import java.util.Calendar;
import java.util.Date;

import static com.example.talapp.Utils.Util.CHANNEL_ID;


public class Alarm extends Service {
    private static AlarmManager alarmManager;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("AlarmManager", "START SERVICE");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = getApplicationContext();

        //Creo il canale delle notifiche
        createNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AlarmManager", "STOP SERVICE");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setAlarm(Date giorno, int id, String action){
        Date date = Calendar.getInstance().getTime();
        giorno.setYear(date.getYear());
        giorno.setMonth(date.set);
        Intent intent = new Intent(context, ForegroundService.NotificationReceiver.class);
        intent.setAction(action);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("AlarmManager", Util.DateToString(date)+ " alle "+ Util.DateToOrario(date));
        //alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), AlarmManager.RTC, pendingIntent);
    }

    public static void removeAlarm(String id, String action){

    }

    //CREO IL CANALE DELLE NOTIFICHE
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notification";
            String description = "Descrizione";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            android.app.NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
