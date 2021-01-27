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
import static com.example.talapp.Utils.Util.FOREGROUND_SERVICE_CHANNEL;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.NOTIFICATION_CHANNEL;

public class NotificationReceiver extends BroadcastReceiver {

    private Calendar FineGiorno;
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "RICEVO");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        FineGiorno = setDomani();

        if (intent.getAction().equals(IMPOSTAZIONI_TRASFUSIONI)) {
            Log.d("NOTIFICATION", "TRASFUSIONE");
            String id = intent.getStringExtra("ID");
            getNextTrasfusione(notificationManagerCompat, id, context);
        }
    }

    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getTrasfusioniNotification(DocumentSnapshot documentSnapshot, Context mContext) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", documentSnapshot.getId());
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                     .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.modificaTrasfusioneFragment)
                .setArguments(bundle)
                .createPendingIntent();

        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_blood_drop_icon)
                .setContentTitle("Hai una trasfusione in programma")
                .setContentText("Il giorno: "+ Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))) + "\n alle ore: "+ Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return builder;
    }

    public void getNextTrasfusione(NotificationManagerCompat notificationManagerCompat, String id, Context context){
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime())
                .orderBy(KEY_DATA, Query.Direction.ASCENDING)
                .limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getDocuments().size() > 0){
                    Date dataNext = Util.TimestampToDate((Timestamp) value.getDocuments().get(0).getData().get(KEY_DATA));

                    //Log.d("DATE", FineGiorno.getTime() +" > "+ dataNext);
                    if(FineGiorno.getTime().compareTo(dataNext) > 0) {
                        notificationManagerCompat.notify(ID_TRASFUSIONI, getTrasfusioniNotification(value.getDocuments().get(0), context).build());
                    }
                }
            }
        });
    }

    public Calendar setDomani(){
        //Calcolo a che ora finisce domani
        Calendar FineGiorno = Calendar.getInstance();
        FineGiorno.add(Calendar.DATE, 1);
        FineGiorno.set(Calendar.HOUR_OF_DAY, 24);
        FineGiorno.set(Calendar.MINUTE, 59);
        FineGiorno.set(Calendar.SECOND, 59);
        FineGiorno.set(Calendar.MILLISECOND, 59);
    ;    return FineGiorno;
    }
}