package com.example.talapp.Notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.talapp.HomeActivity;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Notification.JobService.ACTION_SETALARMS;
import static com.example.talapp.Utils.Util.FOREGROUND_SERVICE_CHANNEL;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.NOTIFICATION_CHANNEL;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "RICEVO");
        Intent serviceIntent = new Intent(context, JobService.class);
        serviceIntent.setAction(intent.getAction());
        JobService.enqueueWork(context, serviceIntent);
    }
}