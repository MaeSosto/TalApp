package com.MartinaSosto.talapp.Notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;


import com.MartinaSosto.talapp.LauncherActivity;
import com.MartinaSosto.talapp.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.MartinaSosto.talapp.Notification.JobService.ACTION_SET_CLOCK_ALARMS;
import static com.MartinaSosto.talapp.Notification.JobService.ACTION_SET_NOTIFICATION_ALARMS;
import static com.MartinaSosto.talapp.Utils.Util.ESAMI_CHANNEL;
import static com.MartinaSosto.talapp.Utils.Util.ESAMI_PERIODICI_CHANNEL;
import static com.MartinaSosto.talapp.Utils.Util.ID_FOREGROUND;
import static com.MartinaSosto.talapp.Utils.Util.SVEGLIE_CHANNEL;
import static com.MartinaSosto.talapp.Utils.Util.FOREGROUND_SERVICE_CHANNEL;
import static com.MartinaSosto.talapp.Utils.Util.KEY_ESAME;
import static com.MartinaSosto.talapp.Utils.Util.KEY_SVEGLIE;
import static com.MartinaSosto.talapp.Utils.Util.KEY_TERAPIE;
import static com.MartinaSosto.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.MartinaSosto.talapp.Utils.Util.KEY_UTENTI;
import static com.MartinaSosto.talapp.Utils.Util.TRASFUSIONI_CHANNEL;
import static com.MartinaSosto.talapp.Utils.Util.Utente;
import static com.MartinaSosto.talapp.Utils.Util.db;
import static com.MartinaSosto.talapp.Utils.Util.mAuth;
import static com.MartinaSosto.talapp.Utils.Util.mFirebaseUser;
import static com.MartinaSosto.talapp.Utils.Util.mGoogleSignInClient;
import static com.MartinaSosto.talapp.Utils.Util.mGoogleUser;

public class ForegroundService extends Service {
    public static CollectionReference trasfusioniRef = null;
    public static CollectionReference esamiRef = null;
    public static CollectionReference terapieRef = null;
    public static DocumentReference settingsRef = null;
    public static CollectionReference sveglieRef = null;
    //private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static boolean ServiceOn = false;
    public static NotificationReceiver notificationReceiver;

    public static boolean isServiceOn() {
        return ServiceOn;
    }
    public static void setServiceOn(boolean serviceOn) {
        ServiceOn = serviceOn;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FOREGROUND SERVICE", "SONO IN START");
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null || mGoogleUser != null) {
           if(mFirebaseUser != null){
               Log.d("ACCESSO", "Accesso con firebase");
               Utente = mFirebaseUser.getEmail();
           }
           else if(mGoogleUser != null){
               Log.d("ACCESSO", "Accesso con Google");
               Utente = mGoogleUser.getEmail();
           }
            trasfusioniRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_TRASFUSIONE);
            esamiRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_ESAME);
            terapieRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_TERAPIE);
            settingsRef = db.collection(KEY_UTENTI).document(Utente);
            sveglieRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_SVEGLIE);
            setup();
            checkNotificationAlarm();
            checkClockAlarm();

            //Faccio cose

        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationReceiver = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setup(){
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, LauncherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, FOREGROUND_SERVICE_CHANNEL)
                .setContentTitle("Ci stiamo prendendo cura di te")
                .setNotificationSilent()
                .setSmallIcon(R.drawable.ic_cells_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ID_FOREGROUND, notification);
        setServiceOn(true);
    }

    //CREO IL CANALE DELLE NOTIFICHE
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannelForeground = new NotificationChannel(FOREGROUND_SERVICE_CHANNEL, "Foreground",  NotificationManager.IMPORTANCE_HIGH);
            notificationChannelForeground.setDescription("Mostra la notifica del Foreground Service");
            android.app.NotificationManager notificationManagerForeground = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManagerForeground.createNotificationChannel(notificationChannelForeground);

            NotificationChannel trasfusioniChannel = new NotificationChannel(TRASFUSIONI_CHANNEL, "Trasfusioni",  android.app.NotificationManager.IMPORTANCE_HIGH);
            trasfusioniChannel.setDescription("Mostra le notifiche delle trasfusioni");
            android.app.NotificationManager trasfusioniManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            trasfusioniManager.createNotificationChannel(trasfusioniChannel);

            NotificationChannel esamiChannel = new NotificationChannel(ESAMI_CHANNEL, "Esami",  android.app.NotificationManager.IMPORTANCE_HIGH);
            esamiChannel.setDescription("Mostra le notifiche degli esami");
            android.app.NotificationManager esamiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            esamiManager.createNotificationChannel(esamiChannel);

            NotificationChannel esamiPeriodiciChannel = new NotificationChannel(ESAMI_PERIODICI_CHANNEL, "Esami futuri",  android.app.NotificationManager.IMPORTANCE_HIGH);
            esamiPeriodiciChannel.setDescription("Mostra le notifiche degli esami futuri");
            android.app.NotificationManager esamiPeriodiciManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            esamiPeriodiciManager.createNotificationChannel(esamiPeriodiciChannel);

            NotificationChannel sveglieChannel = new NotificationChannel(SVEGLIE_CHANNEL, "Sveglie",  android.app.NotificationManager.IMPORTANCE_HIGH);
            sveglieChannel.setDescription("Mostra le notifiche delle sveglie");
            android.app.NotificationManager sveglieManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            sveglieManager.createNotificationChannel(sveglieChannel);
        }
    }

    private void checkNotificationAlarm(){
        Intent serviceIntent = new Intent(getApplicationContext(), JobService.class);
        serviceIntent.setAction(ACTION_SET_NOTIFICATION_ALARMS);
        JobService.enqueueWork(getApplicationContext(), serviceIntent);
    }

    private void checkClockAlarm(){
        Intent serviceIntent = new Intent(getApplicationContext(), JobService.class);
        serviceIntent.setAction(ACTION_SET_CLOCK_ALARMS);
        JobService.enqueueWork(getApplicationContext(), serviceIntent);
    }
}
