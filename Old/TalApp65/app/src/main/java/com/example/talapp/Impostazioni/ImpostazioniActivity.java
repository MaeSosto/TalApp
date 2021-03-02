package com.example.talapp.Impostazioni;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.talapp.LauncherActivity;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.Notification.JobService;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.setServiceOn;
import static com.example.talapp.Notification.JobService.ACTION_SET_NOTIFICATION_ALARMS;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_DIGIUNO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI;
import static com.example.talapp.Utils.Util.IMPOSTAZIONI_TRASFUSIONI_ORARIO;
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

        //Switch trasfusione
        TextInputLayout LLSwitchTrasfusione = findViewById(R.id.LLSwitchTrasfusione);
        SwitchMaterial switchTrasfusione = findViewById(R.id.switchTrasfusione);
        TextInputEditText ETTimeTrasfusione = findViewById(R.id.editTextSettingsTrasfusioni);
        setSwitch(LLSwitchTrasfusione, switchTrasfusione, ETTimeTrasfusione);

        //Switch esami futuri
        LinearLayout LLEsamiFuturi = findViewById(R.id.LLEsamiFuturi);
        SwitchMaterial switchEsameStrumentale = findViewById(R.id.switchEsameStrumentale);
        TextInputEditText editTextEsameStrumentale = findViewById(R.id.editTextEsameStrumentale);
        setSwitch(LLEsamiFuturi, switchEsameStrumentale, editTextEsameStrumentale);

        //Switch esami non programmati
        TextInputLayout LLEsamiNonProgrammati = findViewById(R.id.LLEsamiNonProgrammati);
        SwitchMaterial switchEsameStrumentalePeriodici = findViewById(R.id.switchEsameStrumentalePeriodici);
        TextInputEditText editTextEsamePeriodici = findViewById(R.id.editTextEsamePeriodici);
        setSwitch(LLEsamiNonProgrammati, switchEsameStrumentalePeriodici, editTextEsamePeriodici);

        //Switch digiuno e esami 24h
        SwitchMaterial switchEsamiDigiuno = findViewById(R.id.switchEsamiDigiuno);
        SwitchMaterial switchEsami24h = findViewById(R.id.switchEsami24h);

        //Prendo le informazioni dal DB
        db.collection(KEY_UTENTI).document(Utente).get().addOnCompleteListener(task -> {
            impostazioni = task.getResult().getData();
            setData(switchTrasfusione, ETTimeTrasfusione, IMPOSTAZIONI_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI_ORARIO);
            setData(switchEsameStrumentale, editTextEsameStrumentale, IMPOSTAZIONI_ESAMI, IMPOSTAZIONI_ESAMI_ORARIO);
            setData(switchEsameStrumentalePeriodici, editTextEsamePeriodici, IMPOSTAZIONI_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO);
            switchEsamiDigiuno.setChecked((Boolean) impostazioni.get(IMPOSTAZIONI_ESAMI_DIGIUNO));
            switchEsami24h.setChecked((Boolean) impostazioni.get(IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA));
        });


        //Time picker trasfusione
        setTimePicker(ETTimeTrasfusione);
        setTimePicker(editTextEsameStrumentale);
        setTimePicker(editTextEsamePeriodici);

        //Bottone salva impostazioni
        findViewById(R.id.buttonSalvaImpostazioni).setOnClickListener(v -> {
            updateSettings(switchTrasfusione, ETTimeTrasfusione, IMPOSTAZIONI_TRASFUSIONI, IMPOSTAZIONI_TRASFUSIONI_ORARIO);
            updateSettings(switchEsameStrumentale, editTextEsameStrumentale, IMPOSTAZIONI_ESAMI, IMPOSTAZIONI_ESAMI_ORARIO);
            updateSettings(switchEsameStrumentalePeriodici, editTextEsamePeriodici, IMPOSTAZIONI_ESAMI_PERIODICI, IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO);
            updateSettings(switchEsamiDigiuno, null , IMPOSTAZIONI_ESAMI_DIGIUNO, null);
            updateSettings(switchEsami24h, null , IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA, null);

            db.collection(KEY_UTENTI).document(Utente).set(impostazioni);
            //Aggiorno le sveglie
            checkAlarm();
            Toast.makeText(getApplicationContext(), "Impostazioni aggiornate", Toast.LENGTH_SHORT).show();
            finish();
        });

        //Textview account
        TextView User = findViewById(R.id.TXVUser);
        User.setText(Utente);

        //Bottone disconnessione
        findViewById(R.id.buttonDisconnetti).setOnClickListener(v -> {
           Utente = null;
           mGoogleSignInClient.signOut();
           mAuth.signOut();

           //Fermo il foreground service
           stopService();

            Intent nextScreen = new Intent(getApplicationContext(), LauncherActivity.class);
            nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(nextScreen);
        });
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        setServiceOn(false);
    }

    private void setData(SwitchMaterial s, TextInputEditText et, String bool, String ora){
        s.setChecked((Boolean) impostazioni.get(bool));
        if(s.isChecked()) et.setText(Util.TimestampToOrario((Timestamp) impostazioni.get(ora)));
    }

    private void setSwitch(TextInputLayout l, SwitchMaterial s, TextInputEditText et){
        s.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                l.setVisibility(View.VISIBLE);
                et.setText(Util.DateToOrario(Calendar.getInstance().getTime()));
            }
            else {
                l.setVisibility(View.GONE);
            }
        });
    }

    private void setSwitch(LinearLayout l, SwitchMaterial s, TextInputEditText et){
        s.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                l.setVisibility(View.VISIBLE);
                et.setText(Util.DateToOrario(Calendar.getInstance().getTime()));
            }
            else {
                l.setVisibility(View.GONE);
            }
        });
    }

    private void setTimePicker(TextInputEditText et){
        et.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(Calendar.getInstance().getTime().getHours()).setMinute(Calendar.getInstance().getTime().getMinutes()).setTitleText("Inserisci un orario").build();
            materialTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
            materialTimePicker.addOnPositiveButtonClickListener(v1 -> et.setText(materialTimePicker.getHour()+":"+materialTimePicker.getMinute()));
        });
    }

    private void updateSettings(SwitchMaterial s, TextInputEditText ETOrario, String CampoBooleano, String CampoOrario){
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
        serviceIntent.setAction(ACTION_SET_NOTIFICATION_ALARMS);
        JobService.enqueueWork(getApplicationContext(), serviceIntent);
    }
}