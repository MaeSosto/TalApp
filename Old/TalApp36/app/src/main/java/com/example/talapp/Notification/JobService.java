package com.example.talapp.Notification;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.example.talapp.Utils.Util.ID_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.ID_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAME_STRUMENTALI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;

public class JobService extends JobIntentService {

    static final int JOB_ID = 1000;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, JobService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        setAlarms();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void setAlarms(){
        db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("FOREGROUNDSERVICE", "SETTO GLI ALLARMI");
                if((boolean) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI)){
                    Log.d("FOREGROUNDSERVICE", "AGGIUNGO ALLARME TRASFUSIONE");
                    Alarm.setAlarm(Util.TimestampToDate((Timestamp) task.getResult().get(IMPOSTAZIONI_TRASFUSIONI_ORARIO)), ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
                }

                //Fermo l'alarm
                stopService(new Intent(getApplicationContext(), Alarm.class));
            }
        });
    }

    public static void removeAlarms(){
        Log.d("FOREGROUNDSERVICE", "RIMUOVO GLI ALLARMI");
        //Qua l'alarm è già acceso perché viene chiamato nelle impostazioni
        Alarm.removeAlarm(ID_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI);
        Alarm.removeAlarm(ID_ESAME_STRUMENTALI, IMPOSTAZIONI_ESAME_STRUMENTALI);
    }
}
