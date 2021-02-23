package com.example.talapp.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.talapp.HomeActivity;
import com.example.talapp.R;
import com.example.talapp.Sveglie.ClockAlarm;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.settingsRef;
import static com.example.talapp.Notification.ForegroundService.sveglieRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.ESAMI_CHANNEL;
import static com.example.talapp.Utils.Util.ESAMI_PERIODICI_CHANNEL;
import static com.example.talapp.Utils.Util.ID_ESAME;
import static com.example.talapp.Utils.Util.ID_ESAMI_PERIODICI;
import static com.example.talapp.Utils.Util.ID_SVEGLIE;
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
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_FARMACO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTIFICHE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_PERIODICITA;
import static com.example.talapp.Utils.Util.KEY_RICORDA;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.SVEGLIE_CHANNEL;
import static com.example.talapp.Utils.Util.TRASFUSIONI_CHANNEL;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;

public class JobService extends JobIntentService {

    static final int JOB_ID = 1000;
    public static final String ACTION_SET_NOTIFICATION_ALARMS = "SET NOTIFICATION ALARM";
    public static final String ACTION_SET_CLOCK_ALARMS = "SET CLOCK ALARMS";
    public static final String ACTION_CLOCK = "CLOCK";

    public static void enqueueWork(Context context, Intent work) {
       // Log.d("OnHandleWork", String.valueOf(work.getBundleExtra("BUNDLE")));
        enqueueWork(context, JobService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("ONHANDLEWORK", "ECCOMI");
        switch (intent.getAction()){
            case ACTION_SET_NOTIFICATION_ALARMS:
                setNotificationAlarms();
                break;
            case ACTION_SET_CLOCK_ALARMS:
                setClockAlarms();
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
            case ACTION_CLOCK:
                //Log.d("OnHandleWork", String.valueOf(intent.getBundleExtra("BUNDLE")));
                getFarmaco(intent);
                break;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Faccio partire il service che setta gli allarmi delle notifiche
        startService(new Intent(getApplicationContext(), NotificationAlarm.class));
        //Faccio partire il service che setta gli allarmi delle sveglie
        startService(new Intent(getApplicationContext(), ClockAlarm.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Fermo l'alarm delle notifiche
        stopService(new Intent(getApplicationContext(), NotificationAlarm.class));
        //Fermo l'alarm delle sveglie
        stopService(new Intent(getApplicationContext(), ClockAlarm.class));
        //toast("All work complete");
    }

    @SuppressWarnings("deprecation")
    //final Handler mHandler = new Handler();

    // Helper for showing tests
    //void toast(final CharSequence text) {
    //    mHandler.post(new Runnable() {
    //        @Override public void run() {
    //            Toast.makeText(JobService.this, text, Toast.LENGTH_SHORT).show();
    //        }
    //    });
    //}

    private void getFarmaco(Intent intent) {
        String idSveglia = intent.getStringExtra("SVEGLIA");
        //int ID = intent.getIntExtra("ID", 0);
        Log.d("GETFARMACO", "ha suonato la sveglia con id: "+idSveglia);

        //Prendo le informazioni della sveglia
        sveglieRef.document(idSveglia).get().addOnCompleteListener(task -> {
            Map<String, Object> sveglia = task.getResult().getData();
            Log.d("GETFARMACO", "CONTROLLO NOTIFICHE HA SUONATO LA SVEGLIA "+ idSveglia);
            if(((Boolean)sveglia.get(KEY_NOTIFICHE) && Util.getNumeroGiornoDaSettimana((List<Boolean>) sveglia.get(KEY_SETTIMANA)))){
                Log.d("GETFARMACO", "Ho le notifiche attive e oggi deve suonare");
                terapieRef.document((String) sveglia.get(KEY_FARMACO)).get().addOnCompleteListener(task1 -> {
                    Map<String, Object> terapia = task1.getResult().getData();
                    Date dataFine = Util.TimestampToDate((Timestamp) terapia.get(KEY_DATA_FINE));
                    Log.d("GETFARMACO", "CONTROLLO DATA"+ dataFine+ " compare "+ Util.getPrimoMinuto(Calendar.getInstance()).getTime());
                    if(dataFine.compareTo(Util.getPrimoMinuto(Calendar.getInstance()).getTime()) >= 0){
                        Log.d("GETFARMACO", "NOTIFICA");

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                        notificationManagerCompat.notify(ID_SVEGLIE, getSveglieNotification("Sono le " + Util.TimestampToOrario((Timestamp) sveglia.get(KEY_ORARIO)) + "! Prendi " + terapia.get(KEY_DOSE) + " dosi di " + terapia.get(KEY_NOME), getApplicationContext()).build());
                    }
                });
            }
        });
    }

    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getSveglieNotification(String messaggio, Context mContext) {
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.terapieFragment)
                .createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, SVEGLIE_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentTitle("Sveglia farmaco")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    //Controlla se nella giornata di oggi o di domani ci sono delle trasfusioni in programma, in tal caso crea una notifica e la mostra
    private void getNextTrasfusione(){
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).orderBy(KEY_DATA, Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            StringBuilder messaggio = new StringBuilder();
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i);
                messaggio.append("• Il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                if(i+1 != task.getResult().getDocuments().size()) messaggio.append('\n');
            }
            if(!messaggio.toString().isEmpty()){
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(ID_TRASFUSIONI, getTrasfusioniNotification(messaggio.toString(), getApplicationContext()).build());
            }
        });
    }

    //Crea la notifica delle trasfusioni
    private static NotificationCompat.Builder getTrasfusioniNotification(String messaggio, Context mContext) {
        NavDeepLinkBuilder navDeepLinkBuilder = new NavDeepLinkBuilder(mContext);
        PendingIntent pendingIntent = navDeepLinkBuilder.setComponentName(HomeActivity.class)
                .setGraph(R.navigation.nav_graph_home)
                .setDestination(R.id.trasfusioniFragment)
                .createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, TRASFUSIONI_CHANNEL);
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
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.setDomani().getTime()).orderBy(KEY_DATA, Query.Direction.ASCENDING).get().addOnCompleteListener(task1 -> {
            StringBuilder messaggio = new StringBuilder();

            for (int i = 0; i < task1.getResult().getDocuments().size(); i++) {
                DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(i);
                messaggio.append("• Evento: ").append(documentSnapshot.get(KEY_NOME)).append(" il ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA)))).append(" alle ore ").append(Util.DateToOrario(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                if(i+1 != task1.getResult().getDocuments().size()) messaggio.append("\n");
            }


            settingsRef.get().addOnCompleteListener(task2 -> {

                //Prendo gli esami per la quale devo digiunare
                esamiRef.whereGreaterThan(KEY_DIGIUNO, Util.getPrimoMinuto(Calendar.getInstance()).getTime()).whereLessThanOrEqualTo(KEY_DIGIUNO, Util.setDomani().getTime()).orderBy(KEY_DIGIUNO, Query.Direction.ASCENDING).get().addOnCompleteListener(task3 -> {
                    if((boolean) task2.getResult().get(IMPOSTAZIONI_ESAMI_DIGIUNO)){
                        if(!messaggio.toString().isEmpty()) messaggio.append("\n");
                        for (int i = 0; i < task3.getResult().getDocuments().size(); i++) {
                            DocumentSnapshot documentSnapshot = task3.getResult().getDocuments().get(i);
                            messaggio.append("• Digiuna per ").append(documentSnapshot.get(KEY_NOME));
                            if(i+1 != task3.getResult().getDocuments().size()) messaggio.append("\n");
                        }
                    }

                    //Prendo gli esami dove ho un'attivazione anticipata
                    esamiRef.whereGreaterThan(KEY_ATTIVAZIONE, Util.getPrimoMinuto(Calendar.getInstance()).getTime()).whereLessThanOrEqualTo(KEY_ATTIVAZIONE, Util.setDomani().getTime()).orderBy(KEY_ATTIVAZIONE, Query.Direction.ASCENDING).get().addOnCompleteListener(task4 -> {
                        if((boolean) task2.getResult().get(IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA)){
                            if(!messaggio.toString().isEmpty()) messaggio.append("\n");
                            for (int i = 0; i < task4.getResult().getDocuments().size(); i++) {
                                DocumentSnapshot documentSnapshot = task4.getResult().getDocuments().get(i);
                                messaggio.append("• Hai attivato un preavviso di 24h per l'esame ").append(documentSnapshot.get(KEY_NOME));
                                if(documentSnapshot.contains(KEY_RICORDA)){
                                    messaggio.append(", ricordati di ").append(documentSnapshot.get(KEY_RICORDA));
                                }
                                if(i+1 != task4.getResult().getDocuments().size()) messaggio.append("\n");
                            }
                        }
                        if(!messaggio.toString().isEmpty()) {
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                            notificationManagerCompat.notify(ID_ESAME, getEsamiNotification(messaggio.toString(), getApplicationContext()).build());
                        }
                    });
                });
            });
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, ESAMI_CHANNEL);
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
        esamiRef.whereGreaterThan(KEY_PERIODICITA, Calendar.getInstance().getTime()).whereLessThanOrEqualTo(KEY_PERIODICITA, Util.setDomani().getTime()).orderBy(KEY_PERIODICITA, Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            StringBuilder messaggio = new StringBuilder();
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i);
                messaggio.append("• A breve dovresti rifare l'esame ").append(documentSnapshot.get(KEY_NOME)).append(" fatto in data ").append(Util.DateToString(Util.TimestampToDate((Timestamp) documentSnapshot.get(KEY_DATA))));
                if(i+1 != task.getResult().getDocuments().size()) messaggio.append("\n");
            }
            if(!messaggio.toString().isEmpty()) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                notificationManagerCompat.notify(ID_ESAMI_PERIODICI, getEsamiPeriodiciNotification(messaggio.toString(), getApplicationContext()).build());
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, ESAMI_PERIODICI_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_clinic_foder_icon)
                .setContentTitle("Tempo scaduto per questo esame!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messaggio))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    //Setto le notifiche delle sveglie
    private void setClockAlarms() {
        //Rimuovo tutte le sveglie
        removeClockAlarms();

        sveglieRef.get().addOnCompleteListener(task -> {
            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                if((boolean) task.getResult().getDocuments().get(i).getData().get(KEY_NOTIFICHE)) {
                    ClockAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(KEY_ORARIO)), task.getResult().getDocuments().get(i).getId());
                }
            }
        });
    }

    //Elimina tutte le notifiche delle impostazioni
    public static void removeClockAlarms(){
        sveglieRef.get().addOnCompleteListener(task -> {
            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                ClockAlarm.removeAlarm(task.getResult().getDocuments().get(i).getId());
            }
        });
    }

    //Setto le notifiche delle impostazioni in base alle impostazioni
    private void setNotificationAlarms(){
        //Rimuovo i vecchi allarmi
        removeNotificationAlarms();

        //Se non ho creato le impostazioni allora lo faccio
        createSetttings();
    }

    //Elimina tutte le notifiche delle impostazioni
    public static void removeNotificationAlarms(){
        NotificationAlarm.removeAlarm(ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
        NotificationAlarm.removeAlarm(ID_ESAME, IMPOSTAZIONI_ESAMI);
        NotificationAlarm.removeAlarm(ID_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI);
    }

    //Crea le impostazioni se queste non sono già presenti
    private void createSetttings(){
        settingsRef.get().addOnCompleteListener(task2 -> {
            //Creo le impostazioni
            if(!task2.getResult().contains(IMPOSTAZIONI_TRASFUSIONI)){
                Map<String, Object> data = new HashMap<>();
                data.put(IMPOSTAZIONI_TRASFUSIONI, false);
                data.put(IMPOSTAZIONI_ESAMI, false);
                data.put(IMPOSTAZIONI_ESAMI_PERIODICI, false);
                data.put(IMPOSTAZIONI_ESAMI_DIGIUNO, false);
                data.put(IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA, false);

                settingsRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task1) {
                        settingsRef.get().addOnCompleteListener(task -> {
                            if((boolean) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI)) NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI_ORARIO)), ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
                            if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI)) NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_ORARIO)), ID_ESAME, IMPOSTAZIONI_ESAMI);
                            if((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI)) NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO)), ID_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI);
                        });
                    }
                });
            }
            else {
                settingsRef.get().addOnCompleteListener(task -> {
                    if ((boolean) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI))
                        NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI_ORARIO)), ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
                    if ((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI))
                        NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_ORARIO)), ID_ESAME, IMPOSTAZIONI_ESAMI);
                    if ((boolean) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI))
                        NotificationAlarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO)), ID_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI);
                });
            }
        });
    }
}
