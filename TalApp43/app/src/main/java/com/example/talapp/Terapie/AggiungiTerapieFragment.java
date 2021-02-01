package com.example.talapp.Terapie;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Sveglie.SveglieListAdapter;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Sveglie.SvegliaDialog;
import com.example.talapp.Utils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_NOTIFICHE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;

public class AggiungiTerapieFragment extends Fragment {

    private List<Map<String, Object>> sveglie;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sveglie = new ArrayList<>();
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
        RecyclerViewSveglie.setVisibility(View.VISIBLE);
        RecyclerViewSveglie.setAdapter(sveglieListAdapter);
        RecyclerViewSveglie.setLayoutManager(new LinearLayoutManager(getContext()));
        sveglieListAdapter.setTerapie(sveglie);

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

                        Boolean[] settimana = new Boolean[7];
                        for(int i = 0; i< settimana.length; i++){ settimana[i] = false; }

                        sveglia.put(KEY_NOTIFICHE, false);
                        sveglia.put(KEY_SETTIMANA, settimana);

                        sveglie.add(sveglia);
                        Log.d("SVEGLIE", "SVEGLIA AGGIUNTA");
                        sveglieListAdapter.setTerapie(sveglie);
                    }
                });

            }
        });

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

                    //AGGIUNGO AL DB
                    terapieRef.add(terapia);
                    Toast.makeText(getContext(), "Terapia aggiunta", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(root).popBackStack();
                }
            }
        });

        return root;
    }
}