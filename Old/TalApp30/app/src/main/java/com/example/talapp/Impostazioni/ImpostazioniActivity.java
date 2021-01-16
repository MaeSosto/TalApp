package com.example.talapp.Impostazioni;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talapp.LauncherActivity;
import com.example.talapp.Notification.Alarm;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.R;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.settingsRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Notification.ForegroundService.setServiceOn;
import static com.example.talapp.Utils.Util.ACTION_TRASFUSIONE;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.ESAMI_STRUMENTALI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOTIFICA;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOTIFICA_NONPROGRAMMATI;
import static com.example.talapp.Utils.Util.KEY_ESAME_TIPO;
import static com.example.talapp.Utils.Util.KEY_NOTIFICA;
import static com.example.talapp.Utils.Util.KEY_NOTIFICA_FUTURA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTIFICA;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mGoogleSignInClient;

public class ImpostazioniActivity extends AppCompatActivity {

    private Switch switchTrasfusione;
    private Map<String, Object> nextTrasfusione = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        //Recupero le inmpostazioni dal DB
        getImpostazioni();

        LinearLayout LLSwitchTrasfusione = findViewById(R.id.LLSwitchTrasfusione);
        switchTrasfusione = findViewById(R.id.switchTrasfusione);
        EditText ETTimeTrasfusione = findViewById(R.id.editTextSettingsTrasfusioni);

        //Switch notifiche trasfusione
        switchTrasfusione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LLSwitchTrasfusione.setVisibility(View.VISIBLE);
                    ETTimeTrasfusione.setText(Util.DateToOrario(Calendar.getInstance().getTime()));
                }
                else {
                    LLSwitchTrasfusione.setVisibility(View.GONE);
                }
            }
        });

        //Time picker trasfusione
        ETTimeTrasfusione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(ETTimeTrasfusione);
                dialogFragment.show(getSupportFragmentManager(), "timepicker");
            }
        });

        //Bottone salva impostazioni
        findViewById(R.id.buttonSalvaImpostazioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "SALVA SETTINGS");
                //Accendo l'Alarm per poter aggiornare i valori
                startService(new Intent(getApplicationContext(), Alarm.class));

                updateTrasfusioni(switchTrasfusione.isChecked());
                Map<String, Object> data = new HashMap<>();
                data.put(KEY_TRASFUSIONE_NOTIFICA, switchTrasfusione.isChecked());

                db.collection(KEY_UTENTI).document(Utente)
                        .set(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("SETTINGS", "AGGGIORNATE");
                                Toast.makeText(getApplicationContext(), "Impostazioni aggiornate", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("SETTINGS", "FALLITO");
                                Toast.makeText(getApplicationContext(), "Impossibile aggiornare le impostazioni", Toast.LENGTH_SHORT).show();
                            }
                        });

                //Fermo l'alarm
                stopService(new Intent(getApplicationContext(), Alarm.class));
            }
        });

        //Textview account
        TextView User = findViewById(R.id.TXVUser);
        User.setText(getString(R.string.impostazioni_label2) + Utente);

        //Bottone disconnessione
        findViewById(R.id.buttonDisconnetti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //currentUser = null;
               mGoogleSignInClient.signOut();
               mAuth.signOut();
               stopService();

                Intent nextScreen = new Intent(getApplicationContext(), LauncherActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                //ActivityCompat.finishAffinity(this);
            }
        });

    }

    private void updateTrasfusioni(boolean checked) {
        Log.d("SETTINGS", "UPDATE TRASFUSIONE");
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                        for(int i= 0; i<mTrasfusioni.size(); i++){
                            String id = mTrasfusioni.get(i).getId();
                            Map<String, Object> trasfusione_old = mTrasfusioni.get(i).getData();
                            Timestamp tmp = (Timestamp) trasfusione_old.get(KEY_DATA);
                            Date date = tmp.toDate();
                            int IDnotifica = (int) (date.getTime()/1000);
                            if(checked) {
                                Log.d("SETTINGS", "UPDATE TRASFUSIONE: TRUE");
                                Calendar ventiquattroOre = Calendar.getInstance();
                                ventiquattroOre.setTime(date);
                                ventiquattroOre.add(Calendar.DATE, -1);
                                Alarm.setAlarm(String.valueOf(IDnotifica), ventiquattroOre, ACTION_TRASFUSIONE);
                                trasfusione_old.put(KEY_TRASFUSIONE_NOTIFICA, id);
                            }
                            else{
                                Log.d("SETTINGS", "UPDATE TRASFUSIONE: FALSE");
                                Alarm.removeAlarm(String.valueOf(IDnotifica), ACTION_TRASFUSIONE);
                                trasfusione_old.remove(KEY_TRASFUSIONE_NOTIFICA);
                            }
                            trasfusioniRef.document(id).update(trasfusione_old)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("SETTINGS", "UPDATE TRASFUSIONE: SUCCESSO");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("SETTINGS", "UPDATE TRASFUSIONE: FALLITO");
                                        }
                                    });
                        }
                    }
                });

    }


    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        setServiceOn(false);
    }

    private void getImpostazioni(){
        settingsRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.contains(KEY_TRASFUSIONE_NOTIFICA)){
                            switchTrasfusione.setChecked((Boolean) documentSnapshot.get(KEY_TRASFUSIONE_NOTIFICA));
                        }
                        if(documentSnapshot.contains(KEY_ESAME_NOTIFICA)){

                        }
                        if(documentSnapshot.contains(KEY_ESAME_NOTIFICA_NONPROGRAMMATI)){

                        }

                    }
                });
    }
}