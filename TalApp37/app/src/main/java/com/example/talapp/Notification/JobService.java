package com.example.talapp.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.talapp.HomeActivity;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.ESAMI_STRUMENTALI;
import static com.example.talapp.Utils.Util.ID_ESAME_LABORATORIO;
import static com.example.talapp.Utils.Util.ID_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.ID_ESAMI_LABORATORIO_PERIODICI;
import static com.example.talapp.Utils.Util.ID_ESAMI_STRUMENTALI_PERIODICI;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_LABORATORIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_LABORATORIO_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_STRUMENTALI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_TIPO;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.NOTIFICATION_CHANNEL;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;

public class JobService extends JobIntentService {

    static final int JOB_ID = 1000;
    public static final String ACTION_SETALARMS = "SET ALARM";

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, JobService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("ONHANDLEWORK", "HO RICEVUTO DEL LAVORO DA FARE");
        switch (intent.getAction()){
            case ACTION_SETALARMS:
                setAlarms();
                break;
            case IMPOSTAZIONI_TRASFUSIONI:
                getNextTrasfusione();
                break;
            case IMPOSTAZIONI_ESAME_STRUMENTALI:
                getNextEsameStrumentale();
                break;
            case IMPOSTAZIONI_ESAME_LABORATORIO:
                getNextEsameLaboratorio();
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Faccio partire il service che setta gli allarmi
        startService(new Intent(getApplicationContext(), Alarm.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Fermo l'alarm
        stopService(new Intent(getApplicationContext(), Alarm.class));
        toast("All work complete");
    }

    @SuppressWarnings("deprecation")
    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(JobService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Controlla se nella giornata di oggi o di domani ci sono delle trasfusioni in programma, in tal caso crea una notifica e la mostra
    private void getNextTrasfusione(){
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).orderBy(KEY_DATA, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                StringBuilder messaggio = new StringBuilder();
                if(value != null) {
                    for (int i = 0; i < value.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                        messaggio.append("Il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                        if(i+1 != value.getDocuments().size()) messaggio.append('\n');
                    }
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(ID_TRASFUSIONI, getTrasfusioniNotification(value.getDocuments().get(0).getId(), messaggio.toString().toString(), getApplicationContext()).build());
                }
            }
        });
    }

    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getTrasfusioniNotification(String id, String messaggio, Context mContext) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.trasfusioniFragment)
                .setArguments(bundle)
                .createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_blood_drop_icon)
                .setContentTitle("Trasfusioni reminder")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    //Controlla se nella giornata di oggi o di domani ci sono degli esami strumentali in programma, in tal caso crea una notifica e la mostra
    //Deve inoltre controllare se devo fare il digiuno o se ho delle cose da fare con 24h di anticipo
    private void getNextEsameStrumentale(){
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).whereEqualTo(KEY_TIPO, ESAMI_STRUMENTALI).orderBy(KEY_DATA, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                StringBuilder messaggio = new StringBuilder();
                if(value != null) {
                    for (int i = 0; i < value.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                        messaggio.append(documentSnapshot.get(KEY_NOME)).append(": il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                        if(i+1 != value.getDocuments().size()) messaggio.append("\n");
                    }
                }


                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(ID_ESAME_STRUMENTALI, getEsamiStrumentaliNotification(value.getDocuments().get(0).getId(), messaggio.toString(), getApplicationContext()).build());
            }
        });
    }

    //Crea la notifica
    private static NotificationCompat.Builder getEsamiStrumentaliNotification(String id, String messaggio, Context mContext) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.esamiFragment)
                .setArguments(bundle)
                .createPendingIntent();

        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_clinic_foder_icon)
                .setContentTitle("Esami strumentali reminder")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    private void getNextEsameLaboratorio(){
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).whereEqualTo(KEY_TIPO, ESAMI_LABORATORIO).orderBy(KEY_DATA, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                StringBuilder messaggio = new StringBuilder();
                if(value != null) {
                    for (int i = 0; i < value.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                        messaggio.append(documentSnapshot.get(KEY_NOME)).append(": il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                        if(i+1 != value.getDocuments().size()) messaggio.append("\n");
                    }
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(ID_ESAME_LABORATORIO, getEsamiLaboratorioNotification(value.getDocuments().get(0).getId(), messaggio.toString(), getApplicationContext()).build());
                }
            }
        });
    }

    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getEsamiLaboratorioNotification(String id, String messaggio, Context mContext) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.esamiFragment)
                .setArguments(bundle)
                .createPendingIntent();

        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_clinic_foder_icon)
                .setContentTitle("Esami di laboratorio reminder")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    private void setAlarms(){
        //Rimuovo i vecchi allarmi
        removeAlarms();
        db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("FOREGROUNDSERVICE", "SETTO GLI ALLARMI");
                if((boolean) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI_ORARIO)), ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAME_STRUMENTALI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAME_STRUMENTALI_ORARIO)), ID_ESAME_STRUMENTALI, IMPOSTAZIONI_ESAME_STRUMENTALI);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAME_LABORATORIO))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAME_LABORATORIO_ORARIO)), ID_ESAME_LABORATORIO, IMPOSTAZIONI_ESAME_LABORATORIO);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI_ORARIO)), ID_ESAMI_STRUMENTALI_PERIODICI, IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI_ORARIO)), ID_ESAMI_LABORATORIO_PERIODICI, IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI);
            }
        });
    }

    public static void removeAlarms(){
        Alarm.removeAlarm(ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
        Alarm.removeAlarm(ID_ESAME_STRUMENTALI, IMPOSTAZIONI_ESAME_STRUMENTALI);
        Alarm.removeAlarm(ID_ESAME_LABORATORIO, IMPOSTAZIONI_ESAME_LABORATORIO);
        Alarm.removeAlarm(ID_ESAMI_STRUMENTALI_PERIODICI, IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI);
        Alarm.removeAlarm(ID_ESAMI_LABORATORIO_PERIODICI, IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI);
    }
}
