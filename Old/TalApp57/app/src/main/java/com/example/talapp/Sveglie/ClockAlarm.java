package com.example.talapp.Sveglie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.talapp.Notification.NotificationReceiver;
import com.example.talapp.Utils.Util;

import java.util.Calendar;
import java.util.Date;

import static com.example.talapp.Notification.JobService.ACTION_CLOCK;

public class ClockAlarm extends Service {

    private static AlarmManager alarmManager;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ClockAlarm", "START SERVICE");
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        context = getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClockAlarm", "STOP SERVICE");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Setta l'alarm della sveglia
    public static void setAlarm(Date Orario, String idSveglia){
        Date date = Calendar.getInstance().getTime();
        Orario.setYear(date.getYear());
        Orario.setMonth(date.getMonth());
        Orario.setDate(date.getDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(Orario);
        //Se l'orario oggi è già passato allora viene impostata a domani
        if(Orario.compareTo(date) <= 0 ) cal.add(Calendar.DATE, 1);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_CLOCK);
        //Log.d("AlarmClock", "Calcolo ID: "+ idSveglia.hashCode());
        int ID = Math.abs(idSveglia.hashCode());
        intent.putExtra("SVEGLIA", idSveglia);
        //intent.putExtra("ID", ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("AlarmClock", Util.DateToString(cal.getTime())+ " alle "+ Util.DateToOrario(cal.getTime())+ " id: "+ ID + "("+ idSveglia+")");
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    //Rimuove la sveglia con l'id input
    public static void removeAlarm(String idSveglia){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_CLOCK);
        int ID = Math.abs(idSveglia.hashCode());
        intent.putExtra("SVEGLIA", idSveglia);
        //intent.putExtra("ID", ID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("AlarmClock", "Rimuovo sveglia con id: "+ ID + "("+ idSveglia+")");
        alarmManager.cancel(pendingIntent);
    }
}
