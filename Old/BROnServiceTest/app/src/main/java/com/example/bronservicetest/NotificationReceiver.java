package com.example.bronservicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.bronservicetest.ForegroundService.ACTION_TRASFUSIONE;
import static com.example.bronservicetest.ForegroundService.CHANNEL_ID;
import static com.example.bronservicetest.ForegroundService.NOTIFICATION_ID;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "RICEVO");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (intent.getAction().equals(ACTION_TRASFUSIONE)) {
            Log.d("NOTIFICATION", "TRASFUSIONE");
            String id = intent.getStringExtra("KEY");
            notificationManagerCompat.notify(NOTIFICATION_ID, sendDailyNotification(id, context).build());
        }
        //VADO ALL'INTENT SERVICE
        //Intent intent = new Intent(context,MessageService.class);
        //String value = "String you want to pass";
        //String name = "data";
        //intent.putExtra(name, value);
        //context.startService(intent);
    }

    //CREA LA NOTIFICA GIORNALIERA O QUELLA CHE DEVE ESSEERE RIMANDATA
    private NotificationCompat.Builder sendDailyNotification(String id, Context mContext) {
        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setContentTitle("Notifica trasfusione")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText("Trasfusione numero: "+ id)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return builder;
    }
}
