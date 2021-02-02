package com.example.talapp.Impostazioni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talapp.LauncherActivity;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.Notification.JobService;
import com.example.talapp.R;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.setServiceOn;
import static com.example.talapp.Notification.JobService.ACTION_SETALARMS;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_DIGIUNO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mGoogleSignInClient;

public class ImpostazioniActivity extends AppCompatActivity {
    private  Map<String, Object> impostazioni = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        LinearLayout LLSwitchTrasfusione = findViewById(R.id.LLSwitchTrasfusione);
        Switch switchTrasfusione = findViewById(R.id.switchTrasfusione);
        EditText ETTimeTrasfusione = findViewById(R.id.editTextSettingsTrasfusioni);
        LinearLayout LLEsamiStrumentali = findViewById(R.id.LLEsamiStrumentali);
        Switch switchEsameStrumentale = findViewById(R.id.switchEsameStrumentale);
        EditText editTextEsameStrumentale = findViewById(R.id.editTextEsameStrumentale);
        LinearLayout LLEsamiStrumentaliPeriodici = findViewById(R.id.LLEsamiStrumentaliPeriodici);
        Switch switchEsameStrumentalePeriodici = findViewById(R.id.switchEsameStrumentalePeriodici);
        EditText editTextEsameStrumentalEPeriodici = findViewById(R.id.editTextEsameStrumentaliPeriodici);
        Switch switchEsamiDigiuno = findViewById(R.id.switchEsamiDigiuno);
        Switch switchEsami24h = findViewById(R.id.switchEsami24h);

        //Prendo le informazioni dal DB
        db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                impostazioni = task.getResult().getData();
                setData(switchTrasfusione, ETTimeTrasfusione, IMPOSTAZIONI_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI_ORARIO);
                setData(switchEsameStrumentale, editTextEsameStrumentale, IMPOSTAZIONI_ESAMI, IMPOSTAZIONI_ESAMI_ORARIO);
                setData(switchEsameStrumentalePeriodici, editTextEsameStrumentalEPeriodici, IMPOSTAZIONI_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO);
                switchEsamiDigiuno.setChecked((Boolean) impostazioni.get(IMPOSTAZIONI_ESAMI_DIGIUNO));
                switchEsami24h.setChecked((Boolean) impostazioni.get(IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA));
            }
        });

        //Switch notifiche
        setSwitch(LLSwitchTrasfusione, switchTrasfusione, ETTimeTrasfusione);
        setSwitch(LLEsamiStrumentali, switchEsameStrumentale, editTextEsameStrumentale);
        setSwitch(LLEsamiStrumentaliPeriodici, switchEsameStrumentalePeriodici, editTextEsameStrumentalEPeriodici);

        //Time picker trasfusione
        setTimePicker(ETTimeTrasfusione);
        setTimePicker(editTextEsameStrumentale);
        setTimePicker(editTextEsameStrumentalEPeriodici);

        //Bottone salva impostazioni
        findViewById(R.id.buttonSalvaImpostazioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings(switchTrasfusione, ETTimeTrasfusione, IMPOSTAZIONI_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI_ORARIO);
                updateSettings(switchEsameStrumentale, editTextEsameStrumentale, IMPOSTAZIONI_ESAMI, IMPOSTAZIONI_ESAMI_ORARIO);
                updateSettings(switchEsameStrumentalePeriodici, editTextEsameStrumentalEPeriodici, IMPOSTAZIONI_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO);
                updateSettings(switchEsamiDigiuno, null , IMPOSTAZIONI_ESAMI_DIGIUNO, null);
                updateSettings(switchEsami24h, null , IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA, null);

                db.collection(KEY_UTENTI).document(Utente).set(impostazioni);
                //Aggiorno le sveglie
                checkAlarm();
                Toast.makeText(getApplicationContext(), "Impostazioni aggiornate", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //Textview account
        TextView User = findViewById(R.id.TXVUser);
        User.setText(getString(R.string.impostazioni_label2) + Utente);

        //Bottone disconnessione
        findViewById(R.id.buttonDisconnetti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Utente = null;
               mGoogleSignInClient.signOut();
               mAuth.signOut();

               //Fermo il foreground service
               stopService();

                Intent nextScreen = new Intent(getApplicationContext(), LauncherActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
            }
        });
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        setServiceOn(false);
    }

    private void setData(Switch s, EditText et, String bool, String ora){
        s.setChecked((Boolean) impostazioni.get(bool));
        if(s.isChecked()) et.setText(Util.TimestampToOrario((Timestamp) impostazioni.get(ora)));
    }

    private void setSwitch(LinearLayout l, Switch s, EditText et){
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    l.setVisibility(View.VISIBLE);
                    et.setText(Util.DateToOrario(Calendar.getInstance().getTime()));
                }
                else {
                    l.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setTimePicker(EditText et){
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(et);
                dialogFragment.show(getSupportFragmentManager(), "timepicker");
            }
        });
    }

    private void updateSettings(Switch s, EditText ETOrario, String CampoBooleano, String CampoOrario){
        impostazioni.put(CampoBooleano, s.isChecked());
        if(s.isChecked()) {
            if (CampoOrario != null) {
                try {
                    impostazioni.put(CampoOrario, Util.StringToDateOrario(ETOrario.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            impostazioni.remove(CampoOrario);
        }
    }

    //Chiede al JobIntentService di aggiornare gli allarmi delle notifiche in base alle nuove impostazioni
    private void checkAlarm(){
        Intent serviceIntent = new Intent(getApplicationContext(), JobService.class);
        serviceIntent.setAction(ACTION_SETALARMS);
        JobService.enqueueWork(getApplicationContext(), serviceIntent);
    }
}