package com.example.talapp.Notification;

import android.app.AlarmManager;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;


import com.example.talapp.LauncherActivity;
import com.example.talapp.R;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.NOTIFICATION_ID;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mFirebaseUser;
import static com.example.talapp.Utils.Util.mGoogleSignInClient;
import static com.example.talapp.Utils.Util.mGoogleUser;

public class ForegroundService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
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

        //Setto il NotificationReceiver
        IntentFilter filter = new IntentFilter(IMPOSTAZIONI_TRASFUSIONI);
        registerReceiver(notificationReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build();
       mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
       mAuth = FirebaseAuth.getInstance();
       mFirebaseUser = mAuth.getCurrentUser();
       if(mFirebaseUser != null || mGoogleUser != null) {
            setup();

            //Faccio cose

        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Elimino il NotificationReceiver
//        unregisterReceiver(notificationReceiver);
        notificationReceiver = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NOTIFICATION", "RICEVO");
            Intent intent1 = new Intent(getApplicationContext(), com.example.talapp.Notification.NotificationManager.class);
            intent1.setAction(intent.getAction());
            intent1.putExtra("ID", intent.getStringExtra("ID"));
            context.startService(intent1);
        }
    }

    private void setup(){
        //String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, LauncherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ci stiamo prendendo cura di te")
               // .setContentText(input)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        int NOTIFICATION_ID_SERVICE = 1;
        startForeground(NOTIFICATION_ID_SERVICE, notification);
        setServiceOn(true);
    }

    public static void startService(Context context) {
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
