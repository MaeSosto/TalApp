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

import static com.example.talapp.Utils.Util.FOREGROUND_SERVICE_CHANNEL;
import static com.example.talapp.Utils.Util.NOTIFICATION_CHANNEL;


public class NotificationAlarm extends Service {
    private static AlarmManager alarmManager;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NotificationAlarm", "START SERVICE");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NotificationAlarm", "STOP SERVICE");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //In: Orario, ID numerico allarme e Stringa di Action
    public static void setAlarm(Date Orario, int IDAllarme, String CampoBooleano){
        Date date = Calendar.getInstance().getTime();
        Orario.setYear(date.getYear());
        Orario.setMonth(date.getMonth());
        Orario.setDate(date.getDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(Orario);
        if(Orario.compareTo(date) <= 0 ) cal.add(Calendar.DATE, 1);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(CampoBooleano);
        //intent.putExtra("ID", String.valueOf(IDAllarme));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, IDAllarme, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("AlarmManager", Util.DateToString(cal.getTime())+ " alle "+ Util.DateToOrario(cal.getTime()));
        //alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void removeAlarm(int id, String action){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(action);
        //intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

}
