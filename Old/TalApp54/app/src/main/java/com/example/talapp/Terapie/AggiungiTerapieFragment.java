package com.example.talapp.Terapie;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Sveglie.ClockAlarm;
import com.example.talapp.Sveglie.SveglieListAdapter;
import com.example.talapp.Sveglie.SvegliaDialog;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.sveglieRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_FARMACO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_NOTIFICHE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;
import static com.example.talapp.Utils.Util.checkError;
import static com.example.talapp.Utils.Util.setListeners;

public class AggiungiTerapieFragment extends Fragment {

    private static MutableLiveData<List<Map<String, Object>>> sveglie;
    private static Date inizioTerapia, fineTerapia;
    private View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Faccio partire il service che setta gli allarmi
        getActivity().startService(new Intent(getActivity(), ClockAlarm.class));

        sveglie = new MutableLiveData<List<Map<String, Object>>>();
        sveglie.setValue(new ArrayList<>());
        inizioTerapia = null;
        fineTerapia = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Fermo l'alarm
        getActivity().stopService(new Intent(getActivity(), ClockAlarm.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aggiungi_terapie, container, false);
        this.root = root;
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));


        //Nome
        TextInputEditText ETNome = root.findViewById(R.id.editTextNomeFarmaco);
        TextInputLayout TILNomeFarmaco = root.findViewById(R.id.TILNomeFarmaco);
        setListeners(ETNome, TILNomeFarmaco, "Inserisci un nome valido");

        //Costruisco il Material date picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Seleziona il periodo della terapia");
        MaterialDatePicker materialDatePicker = builder.build();

        //Setto il periodo
        TextInputEditText ETDateInizio = root.findViewById(R.id.editTextDataRangeTerapia);
        TextInputLayout TILDataTerapia = root.findViewById(R.id.TILDataTerapia);
        setDatePicker(materialDatePicker, ETDateInizio);
        setListeners(ETDateInizio, TILDataTerapia, "Inserisci un periodo valido");
        ETDateInizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
            }
        });


        //Dose
        TextInputEditText ETDose = root.findViewById(R.id.editTextDoseTerapia);
        TextInputLayout TILDoseFarmaco = root.findViewById(R.id.TILDoseTerapia);
        setListeners(ETDose, TILDoseFarmaco, "Inserisci una dose valida");

        //RecyclerView
        RecyclerView RecyclerViewSveglie = root.findViewById(R.id.RecyclerViewSveglie);
        SveglieListAdapter sveglieListAdapter = new SveglieListAdapter(getContext());
        sveglie.observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> maps) {
                RecyclerViewSveglie.setVisibility(View.VISIBLE);
                RecyclerViewSveglie.setAdapter(sveglieListAdapter);
                RecyclerViewSveglie.setLayoutManager(new LinearLayoutManager(getContext()));
                sveglieListAdapter.setTerapie(maps, sveglie);
            }
        });

        //FAB Aggiungi sveglia
        Button buttonAggiungiSveglia = root.findViewById(R.id.buttonAggiungiSveglia);
        buttonAggiungiSveglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiSveglia();
            }
        });

        //Bottone salva
        Button buttonSalvaTerapia = root.findViewById(R.id.buttonSalvaTerapia);
        buttonSalvaTerapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkError(ETNome, TILNomeFarmaco, "Inserisci un nome valido") && checkError(ETDateInizio, TILDataTerapia, "Inserisci un periodo valido") && checkError(ETDose, TILDoseFarmaco, "Inserisci una dose valida")){

                    Map<String, Object> terapia = new HashMap<>();
                    terapia.put(KEY_NOME, ETNome.getText().toString());
                    terapia.put(KEY_DATA, inizioTerapia);
                    terapia.put(KEY_DATA_FINE, fineTerapia);
                    terapia.put(KEY_DOSE, Double.parseDouble(ETDose.getText().toString()));

                    EditText ETNote = root.findViewById(R.id.ETNoteTerapia);
                    if(!ETNote.getText().toString().isEmpty()) {
                        terapia.put(KEY_NOTE, ETNote.getText().toString());
                    }

                    //Aggiungo al DB le terapie e le sveglie
                    terapieRef.add(terapia).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            String idFarmaco = task.getResult().getId();

                            eliminoVecchieSveglie(idFarmaco);
                        }
                    });
                }
            }
        });

        return root;
    }

    //Elimino le vecchie sveglie
    private void eliminoVecchieSveglie(String idFarmaco) {
        sveglieRef.whereEqualTo(KEY_FARMACO, idFarmaco).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                    String id = task.getResult().getDocuments().get(i).getId();
                    sveglieRef.document(id).delete();
                    ClockAlarm.removeAlarm(id);
                }
//
                settaNuoveSveglie(idFarmaco);
            }
        });
    }

    //Setto le nuove sveglie
    private void settaNuoveSveglie(String idFarmaco) {
        for(int i = 0; i< sveglie.getValue().size(); i++){
            sveglie.getValue().get(i).put(KEY_FARMACO, idFarmaco);
            Date orario = null;
            try {
                orario = Util.TimestampToDate((Timestamp) sveglie.getValue().get(i).get(KEY_ORARIO));
            } catch (Exception e) {
                orario = (Date) sveglie.getValue().get(i).get(KEY_ORARIO);
            }
            Date finalOrario = orario;
            sveglieRef.add(sveglie.getValue().get(i)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    String idSveglia = task.getResult().getId();
                    ClockAlarm.removeAlarm(idSveglia);
                    if(fineTerapia.compareTo(Calendar.getInstance().getTime()) >= 0){
                        Log.d("SALVA TERAPIA", "HO SALVATO L'ALARM");
                        ClockAlarm.setAlarm(finalOrario, idSveglia);
                    }
                }
            });
        }
        Toast.makeText(getContext(), "Terapia aggiunta", Toast.LENGTH_LONG).show();
        Navigation.findNavController(root).popBackStack();
    }

    private void aggiungiSveglia() {
        MutableLiveData<String> orario = new MutableLiveData<>();
        DialogFragment dialogFragment = new SvegliaDialog(orario);
        dialogFragment.show(getParentFragmentManager(), "timepicker");

        orario.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Map<String, Object> sveglia = new HashMap<>();
                try {
                    sveglia.put(KEY_ORARIO, Util.StringToDateOrario(s));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                List<Boolean> settimana = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
                Collections.fill(settimana, Boolean.FALSE);

                sveglia.put(KEY_NOTIFICHE, false);
                sveglia.put(KEY_SETTIMANA, settimana);

                List<Map<String, Object>> tmp = sveglie.getValue();
                tmp.add(sveglia);
                sveglie.setValue(tmp);

                Toast.makeText(getContext(), "Sveglia aggiunta", Toast.LENGTH_SHORT).show();
                //Log.d("SVEGLIE", "SVEGLIA AGGIUNTA");
                //sveglieListAdapter.setTerapie(sveglie);
            }
        });
    }

    private void setDatePicker(MaterialDatePicker materialDatePicker, TextInputEditText textInputEditText) {
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Pair<Long, Long> sel = (Pair<Long, Long>) selection;
                //Object dateSelector = materialDatePicker.getSelection();
                //Collection<Pair<Long, Long>> collection = dateSelector.
                Calendar inizio = Calendar.getInstance();
                inizio.setTimeInMillis(sel.first);
                Calendar fine = Calendar.getInstance();
                fine.setTimeInMillis(sel.second);
                inizioTerapia = inizio.getTime();
                fineTerapia = fine.getTime();
                textInputEditText.setText(Util.DateToString(inizioTerapia)+" - "+ Util.DateToString(fineTerapia));
                //Log.d("MATERIAL DATE PICKER", inizio.getTime()+"/"+ fine.getTime());
            }
        });
    }
}