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
import static com.example.talapp.Utils.Util.ID_ESAME;
import static com.example.talapp.Utils.Util.ID_ESAMI_PERIODICI;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_DIGIUNO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
import static com.example.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_PERIODICITA;
import static com.example.talapp.Utils.Util.KEY_RICORDA;
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
        switch (intent.getAction()){
            case ACTION_SETALARMS:
                setAlarms();
                break;
            case IMPOSTAZIONI_TRASFUSIONI:
                getNextTrasfusione();
                break;
            case IMPOSTAZIONI_ESAMI:
                getNextEsame();
                break;
            case IMPOSTAZIONI_ESAMI_PERIODICI:
                getNextEsamePeriodico();
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
                        messaggio.append("• Il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
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

    //Controlla se nella giornata di oggi o di domani ci sono degli esami in programma, in tal caso crea una notifica e la mostra
    //Deve inoltre controllare se devo fare il digiuno o se ho delle cose da fare con 24h di anticipo
    private void getNextEsame(){
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).orderBy(KEY_DATA, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot valore, @Nullable FirebaseFirestoreException error) {
                StringBuilder messaggio = new StringBuilder();
                if(valore != null) {
                    for (int i = 0; i < valore.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = valore.getDocuments().get(i);
                        messaggio.append("• Evento: ").append(documentSnapshot.get(KEY_NOME)).append(" il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                        if(i+1 != valore.getDocuments().size()) messaggio.append("\n");
                    }
                }

                db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        //Prendo gli esami per la quale devo digiunare
                        esamiRef.whereGreaterThan(KEY_DIGIUNO, Util.getPrimoMinuto(Calendar.getInstance()).getTime()).whereLessThanOrEqualTo(KEY_DIGIUNO, Util.setDomani().getTime()).orderBy(KEY_DIGIUNO, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value != null && (boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_DIGIUNO)){
                                    if(!messaggio.toString().isEmpty()) messaggio.append("\n");
                                    for (int i = 0; i < value.getDocuments().size(); i++) {
                                        DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                                        messaggio.append("• Digiuna per ").append(documentSnapshot.get(KEY_NOME));
                                        if(i+1 != value.getDocuments().size()) messaggio.append("\n");
                                    }
                                }

                                //Prendo gli esami dove ho un'attivazione anticipata
                                esamiRef.whereGreaterThan(KEY_ATTIVAZIONE, Util.getPrimoMinuto(Calendar.getInstance()).getTime()).whereLessThanOrEqualTo(KEY_ATTIVAZIONE, Util.setDomani().getTime()).orderBy(KEY_ATTIVAZIONE, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(value != null && (boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA)){
                                            if(!messaggio.toString().isEmpty()) messaggio.append("\n");
                                            for (int i = 0; i < value.getDocuments().size(); i++) {
                                                DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                                                messaggio.append("• Hai attivato un preavviso di 24h per l'esame ").append(documentSnapshot.get(KEY_NOME));
                                                if(documentSnapshot.contains(KEY_RICORDA)){
                                                    messaggio.append(", ricordati di ").append(documentSnapshot.get(KEY_RICORDA));
                                                }
                                                if(i+1 != value.getDocuments().size()) messaggio.append("\n");
                                            }
                                        }
                                        if(!messaggio.toString().isEmpty()) {
                                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                            notificationManagerCompat.notify(ID_ESAME, getEsamiNotification(messaggio.toString(), getApplicationContext()).build());
                                        }
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }

    //Crea la notifica
    private static NotificationCompat.Builder getEsamiNotification(String messaggio, Context mContext) {
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.esamiFragment)
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

    private void getNextEsamePeriodico() {
        esamiRef.whereGreaterThan(KEY_PERIODICITA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_PERIODICITA, Util.setDomani().getTime()).orderBy(KEY_PERIODICITA, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                StringBuilder messaggio = new StringBuilder();
                if(value != null) {
                    for (int i = 0; i < value.getDocuments().size(); i++) {
                        DocumentSnapshot documentSnapshot = value.getDocuments().get(i);
                        messaggio.append("• A breve dovresti rifare l'esame ").append(documentSnapshot.get(KEY_NOME)).append(" fatto in data ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                        if(i+1 != value.getDocuments().size()) messaggio.append("\n");
                    }

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(ID_ESAMI_PERIODICI, getEsamiPeriodiciNotification(messaggio.toString(), getApplicationContext()).build());
                }
            }
        });
    }

    //Crea la notifica
    private static NotificationCompat.Builder getEsamiPeriodiciNotification(String messaggio, Context mContext) {
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.calendarioFragment)
                .createPendingIntent();

        //NOTIFICA GIORNALIERA BUILDER
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_clinic_foder_icon)
                .setContentTitle("Tempo scaduto per questo esame!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    //Setto gli allarmi in base alle impostazioni
    private void setAlarms(){
        removeAlarms();
        db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if((boolean) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI_ORARIO)), ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_ORARIO)), ID_ESAME, IMPOSTAZIONI_ESAMI);
                if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI))Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO)), ID_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI);
            }
        });
    }

    //Elimina tutti gli allarmi
    public static void removeAlarms(){
        Alarm.removeAlarm(ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
        Alarm.removeAlarm(ID_ESAME, IMPOSTAZIONI_ESAMI);
        Alarm.removeAlarm(ID_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI);
    }
}
