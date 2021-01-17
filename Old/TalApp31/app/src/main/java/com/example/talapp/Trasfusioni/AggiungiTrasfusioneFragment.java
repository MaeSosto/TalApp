package com.example.talapp.Trasfusioni;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.Notification.Alarm;
import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.settingsRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.ACTION_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTIFICA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;


public class AggiungiTrasfusioneFragment extends Fragment {
    private boolean Notification;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Faccio partire il service che setta gli allarmi e controllo se l'utente vuole essere notificato o no
        getActivity().startService(new Intent( getContext(), Alarm.class));
        settingsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Notification = task.getResult().contains(KEY_TRASFUSIONE_NOTIFICA) && (boolean) task.getResult().get(KEY_TRASFUSIONE_NOTIFICA);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Fermo l'alarm
        getActivity().stopService(new Intent(getContext(), Alarm.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aggiungi_trasfusione, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        //Setto la data
        EditText ETDate = root.findViewById(R.id.editTextDate);
        ETDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDate);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        //Setto l'ora
        EditText ETTime = root.findViewById(R.id.editTextEsito);
        ETTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(ETTime);
                dialogFragment.show(getParentFragmentManager(), "timepicker");
            }
        });

        //Bottone salva trasfusione
        root.findViewById(R.id.buttonSalvaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ETDate.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data valida", Toast.LENGTH_SHORT).show();
                }
                if(ETTime.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un orario valido", Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar date = Calendar.getInstance();
                    try {
                        date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //CREO LA TRASFUSIONE
                    Map<String, Object> trasfusione = new HashMap<>();
                    trasfusione.put(KEY_DATA, date.getTime());
                    Spinner Sunita = root.findViewById(R.id.spinnerPeriodicitaEsame);
                    trasfusione.put(KEY_TRASFUSIONE_UNITA, Sunita.getSelectedItem().toString());
                    EditText ETNote = root.findViewById(R.id.editTextNoteTrasfusione);
                    if(!ETNote.getText().toString().isEmpty()) {
                        trasfusione.put(KEY_NOTE, ETNote.getText().toString());
                    }
                    //Controllo se ho le notifiche attive e mancano più di 24h
                    //Calendar ventiquattroOre = date;
                    //ventiquattroOre.add(Calendar.DATE, -1);
                    //if(Notification && (Calendar.getInstance().compareTo(ventiquattroOre) < 0)){
                    //    int id = (int) (date.getTime().getTime()/1000);
                    //    Alarm.setAlarm(String.valueOf(id), ventiquattroOre, ACTION_TRASFUSIONE);
                    //    trasfusione.put(KEY_TRASFUSIONE_NOTIFICA, id);
                    //}

                    //AGGIUNGO AL DB
                    trasfusioniRef.add(trasfusione);
                    Toast.makeText(getContext(), "Trasfusione aggiunta", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(root).popBackStack();
                }
            }
        });
        return root;
    }
}