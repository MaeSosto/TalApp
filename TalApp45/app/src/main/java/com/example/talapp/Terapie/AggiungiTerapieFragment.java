package com.example.talapp.Terapie;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.Notification.NotificationAlarm;
import com.example.talapp.R;
import com.example.talapp.Sveglie.ClockAlarm;
import com.example.talapp.Sveglie.SveglieListAdapter;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Sveglie.SvegliaDialog;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import static com.example.talapp.Utils.Util.KEY_SVEGLIE;

public class AggiungiTerapieFragment extends Fragment {

    private static MutableLiveData<List<Map<String, Object>>> sveglie;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Faccio partire il service che setta gli allarmi
        getActivity().startService(new Intent(getActivity(), ClockAlarm.class));

        sveglie = new MutableLiveData<List<Map<String, Object>>>();
        sveglie.setValue(new ArrayList<>());
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

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));

        //Setto la data di inizio
        EditText ETDateInizio = root.findViewById(R.id.editTextDataInizioTerapia);
        ETDateInizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDateInizio);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        //Setto la data di fine
        EditText ETDateFine = root.findViewById(R.id.editTextDataFineTerapia);
        ETDateFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDateFine);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });
        EditText editTextNomeFarmaco = root.findViewById(R.id.editTextNomeFarmaco);
        EditText editTextDoseFarmaco = root.findViewById(R.id.editTextDoseFarmaco);

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
        FloatingActionButton FABAggiungiSveglia = root.findViewById(R.id.floatingActionButtonAggiungiSveglia);
        FABAggiungiSveglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        Log.d("SVEGLIE", "SVEGLIA AGGIUNTA");
                        //sveglieListAdapter.setTerapie(sveglie);
                    }
                });

            }
        });

        //Bottone che salva la terapia e le sveglie
        Button buttonSalvaTerapia = root.findViewById(R.id.buttonSalvaTerapia);
        buttonSalvaTerapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNomeFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT).show();
                }
                else if(ETDateInizio.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data di inizio terapia valida", Toast.LENGTH_SHORT).show();
                }
                else if(ETDateFine.getText().toString().isEmpty() || (Util.StringToDate(ETDateFine.getText().toString()).compareTo(Util.StringToDate(ETDateInizio.getText().toString())) < 0)){
                    Toast.makeText(getContext(), "Inserisci una data di fine terapia valida", Toast.LENGTH_SHORT).show();
                }
                else if(editTextDoseFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una dose valida", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Creo la terapia
                    Map<String, Object> terapia = new HashMap<>();
                    terapia.put(KEY_NOME, editTextNomeFarmaco.getText().toString());
                    terapia.put(KEY_DATA, Util.StringToDate(ETDateInizio.getText().toString()));
                    terapia.put(KEY_DATA_FINE, Util.StringToDate(ETDateFine.getText().toString()));
                    terapia.put(KEY_DOSE, Double.parseDouble(editTextDoseFarmaco.getText().toString()));

                    EditText ETNote = root.findViewById(R.id.editTextNoteTerapia);
                    if(!ETNote.getText().toString().isEmpty()) {
                        terapia.put(KEY_NOTE, ETNote.getText().toString());
                    }


                    //Aggiungo al DB le terapie e le sveglie
                    terapieRef.add(terapia).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            String idFarmaco = task.getResult().getId();

                            //Elimino le vecchie sveglie
                            sveglieRef.whereEqualTo(KEY_FARMACO, idFarmaco).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                                        String id = task.getResult().getDocuments().get(i).getId();
                                        sveglieRef.document(id).delete();
                                        ClockAlarm.removeAlarm(id);
                                    }
//
                                    //Setto le nuove sveglie
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
                                                ClockAlarm.setAlarm(finalOrario, idSveglia);
                                            }
                                        });
                                    }
//
                                    Toast.makeText(getContext(), "Terapia aggiunta", Toast.LENGTH_LONG).show();
                                    Navigation.findNavController(root).popBackStack();
                                }
                            });
                        }
                    });
                }
            }
        });

        return root;
    }
}