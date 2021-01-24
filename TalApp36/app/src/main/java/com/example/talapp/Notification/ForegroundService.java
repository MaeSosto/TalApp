package com.example.talapp.Notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;


import com.example.talapp.LauncherActivity;
import com.example.talapp.R;

import com.example.talapp.Utils.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.talapp.Utils.Util.FOREGROUND_SERVICE_CHANNEL;
import static com.example.talapp.Utils.Util.ID_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
import static com.example.talapp.Utils.Util.KEY_ESAME;
import static com.example.talapp.Utils.Util.KEY_SVEGLIE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.NOTIFICATION_CHANNEL;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mFirebaseUser;
import static com.example.talapp.Utils.Util.mGoogleSignInClient;
import static com.example.talapp.Utils.Util.mGoogleUser;

public class ForegroundService extends Service {
    public static CollectionReference trasfusioniRef = null;
    public static CollectionReference esamiRef = null;
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
    public void onCreate() {
        super.onCreate();
        //Faccio partire il service che setta gli allarmi e controllo se l'utente vuole essere notificato o no
        startService(new Intent(getApplicationContext(), Alarm.class));

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
            settingsRef = db.collection(KEY_UTENTI).document(Utente);
            sveglieRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_SVEGLIE);
            setup();

            Intent serviceIntent = new Intent(getApplicationContext(), JobService.class);
            JobService.enqueueWork(getApplicationContext(), serviceIntent);

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
               // .setContentText(input)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        int NOTIFICATION_ID_SERVICE = 1;
        startForeground(NOTIFICATION_ID_SERVICE, notification);
        setServiceOn(true);
    }

    //CREO IL CANALE DELLE NOTIFICHE
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannelForeground = new NotificationChannel(FOREGROUND_SERVICE_CHANNEL, "Foreground",  android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannelForeground.setDescription("Mostra la notifica del Foreground Service");
            android.app.NotificationManager notificationManagerForeground = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManagerForeground.createNotificationChannel(notificationChannelForeground);

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, "Notifiche",  android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Mostra le notifiche degli eventi");
            android.app.NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }



}
