package com.example.talapp.Notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.talapp.R;

import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.NOTIFICATION_ID;

public class NotificationManager extends IntentService {
    private static final String CHANNEL_ID = "NotificationChannel";

    public NotificationManager(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (intent.getAction().equals(IMPOSTAZIONI_TRASFUSIONI)) {
            Log.d("NOTIFICATION", "TRASFUSIONE");
            String id = intent.getStringExtra("ID");
            notificationManagerCompat.notify(ID_TRASFUSIONI, getTrasfusioniNotification(Integer.parseInt(id), getApplicationContext()).build());
        }
        stopSelf();
    }


    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getTrasfusioniNotification(int id, Context mContext) {
        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_blood_drop_icon)
                .setContentTitle("Notifica trasfusione")
                .setContentText("Trasfusione numero: "+ id)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return builder;
    }
}
