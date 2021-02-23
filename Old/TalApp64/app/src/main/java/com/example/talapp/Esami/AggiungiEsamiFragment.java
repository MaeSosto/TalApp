package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.talapp.Esami.AnalisiListAdapter.KEY_MOSTRA;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.ConverterStringToCalendar;
import static com.example.talapp.Utils.Util.KEY_ANALISI;
import static com.example.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_PERIODICITA;
import static com.example.talapp.Utils.Util.KEY_RICORDA;
import static com.example.talapp.Utils.Util.KEY_TIPO;
import static com.example.talapp.Utils.Util.checkError;
import static com.example.talapp.Utils.Util.setListeners;
import static com.example.talapp.Utils.Util.setSpinner;


public class AggiungiEsamiFragment extends Fragment {

    private Date data;
    private Date ora;
    private static MutableLiveData<List<Map<String, Object>>> mAnalisi;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnalisi = new MutableLiveData<>();
        mAnalisi.setValue(new ArrayList<>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aggiungi_esami, container, false);
        this.root = root;
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        //Nome
        TextInputEditText ETNome = root.findViewById(R.id.editTextNomeEsame);
        TextInputLayout TILNomeEsame = root.findViewById(R.id.TILNomeEsame);
        setListeners(ETNome, TILNomeEsame, "Inserisci un nome valido");

        //Data
        TextInputEditText ETDate = root.findViewById(R.id.editTextDateEsame);
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker().setTitleText("Seleziona una data");
        MaterialDatePicker materialDatePicker = builder.build();
        setDatePicker(materialDatePicker, ETDate);
        TextInputLayout TILDataEsame = root.findViewById(R.id.TILDataEsame);
        setListeners(ETDate, TILDataEsame, "Inserisci una data valida");
        ETDate.setOnClickListener(v -> materialDatePicker.show(getFragmentManager(), "DATE_PICKER"));

        //Orario
        TextInputEditText ETTime = root.findViewById(R.id.editTextTimeEsame);
        TextInputLayout TILOraEsame = root.findViewById(R.id.TILOraEsame);
        setListeners(ETTime, TILOraEsame, "Inserisci un orario valido");
        ETTime.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(Calendar.getInstance().getTime().getHours()).setMinute(Calendar.getInstance().getTime().getMinutes()).setTitleText("Inserisci un orario").build();
            setTimePicker(materialTimePicker, ETTime);
            materialTimePicker.show(requireFragmentManager(), "TIME_PICKER");
        });

        //Bottone aggiungi analisi
        Button buttonAggiungiAnalisi = root.findViewById(R.id.buttonAggiungiAnalisi);

        //Analisi
        AutoCompleteTextView ETAnalisi = root.findViewById(R.id.editTextNomeAnalisi);
        TextInputLayout TILNomeAnalisi = root.findViewById(R.id.TILNomeAnalisi);
        setListeners(ETAnalisi, TILNomeAnalisi, "Aggiungi almeno un'analisi");
        setSpinner(getResources().getStringArray(R.array.NomiAnalisi), ETAnalisi, getActivity());
        ETAnalisi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 0){
                    buttonAggiungiAnalisi.setVisibility(View.GONE);
                }
                else buttonAggiungiAnalisi.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("NOME ANALISI", s.toString());
                TILNomeAnalisi.setErrorEnabled(false);
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(KEY_NOME, s.toString());
                tmp.put(KEY_ESITO, null);
                if((!s.toString().isEmpty() || s.toString().compareTo("Nome analisi") != 0) || contiene(mAnalisi.getValue(), tmp)) buttonAggiungiAnalisi.setVisibility(View.VISIBLE);
                else buttonAggiungiAnalisi.setVisibility(View.GONE);
            }
        });

        //Bottone aggiungi analisi
        buttonAggiungiAnalisi.setOnClickListener(v -> {
            if(!ETAnalisi.getText().toString().isEmpty()) {
                Log.d("AGGIUNGI ANALISI", "");
                Map<String, Object> analisi = new HashMap<>();
                analisi.put(KEY_NOME, ETAnalisi.getText().toString());
                analisi.put(KEY_ESITO, null);
                List<Map<String, Object>> tmp = mAnalisi.getValue();
                if (contiene(tmp, analisi)) {
                    Log.d("AGGIUNGI ANALISI", "AGGIUNGO");
                    tmp.add(analisi);
                    mAnalisi.setValue(tmp);
                    ETAnalisi.setText("");
                } else Log.d("AGGIUNGI ANALISI", "NON AGGIUNGO");
            }
        });

        //RecyclerView
        AnalisiListAdapter analisiListAdapter = new AnalisiListAdapter(getContext(), KEY_MOSTRA);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_analisi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(analisiListAdapter);
        TextView TXVAnalisiAggiunte = root.findViewById(R.id.TXVAnalisiAggiunte);
        mAnalisi.observe(getViewLifecycleOwner(), analisis -> {
            //Log.d("LISTA ESAMI", String.valueOf(analisis));
            analisiListAdapter.setAnalisi(analisis);

            if(analisis.size() > 0) TXVAnalisiAggiunte.setVisibility(View.VISIBLE);
            else TXVAnalisiAggiunte.setVisibility(View.GONE);
        });

        //Spinner periodicita
        AutoCompleteTextView Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
        setSpinner(getResources().getStringArray(R.array.periodicita), Speriodicita, getActivity());

        //Spinner tipo
        AutoCompleteTextView Stipo = root.findViewById(R.id.spinnerTipoEsame);
        setSpinner(getResources().getStringArray(R.array.TipoEsame), Stipo, getActivity());

        //Spinner 24h
        SwitchMaterial Sattivazione = root.findViewById(R.id.switchAttivazione24h);
        Sattivazione.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) root.findViewById(R.id.LLRicordami).setVisibility(View.VISIBLE);
            else root.findViewById(R.id.LLRicordami).setVisibility(View.GONE);
        });

        //Bottone salva
        root.findViewById(R.id.buttonSalvaEsame).setOnClickListener(v -> {
            if(checkError(ETNome, TILNomeEsame, "Inserisci un nome valido") && checkError(ETDate, TILDataEsame, "Inserisci una data valida") && checkError(ETTime, TILOraEsame, "Inserisci un orario valido")  && checkList(TILNomeAnalisi))
                buttonAggiungiEsami(ETDate.getText().toString(), ETTime.getText().toString(), ETNome.getText().toString(), Stipo.getText().toString(), Sattivazione.isChecked(), Speriodicita.getText().toString());
        });

        return root;
    }

    private boolean checkList(TextInputLayout textInputLayout) {
        if(mAnalisi.getValue().size() > 0){
            textInputLayout.setErrorEnabled(false);
            return true;
        }
        else {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Aggiungi almeno un'analisi");
            return false;
        }
    }

    private void buttonAggiungiEsami(String data, String ora, String nome, String tipo, boolean Sattivazione, String periodicita) {
            Map<String, Object> esame = new HashMap<>();
            try {
                esame.put(KEY_DATA, ConverterStringToCalendar(data, ora));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            esame.put(KEY_NOME, nome);
            esame.put(KEY_TIPO, tipo);
            esame.put(KEY_ANALISI, mAnalisi.getValue());
            SwitchMaterial Sdigiuno = root.findViewById(R.id.switchDigiunoEsame);
            if(Sdigiuno.isChecked()) {
                Calendar tmp = Calendar.getInstance();
                try {
                    tmp.setTime(ConverterStringToCalendar(data, ora));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tmp.add(Calendar.DATE, -1);
                esame.put(KEY_DIGIUNO, tmp.getTime());
            }
            if(Sattivazione){
                Calendar tmp = Calendar.getInstance();
                try {
                    tmp.setTime(ConverterStringToCalendar(data, ora));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tmp.add(Calendar.DATE, -1);
                esame.put(KEY_ATTIVAZIONE, tmp.getTime());
            }
            TextInputEditText ETRicorda = root.findViewById(R.id.editTextRicorda);
            TextInputEditText ETNote = root.findViewById(R.id.ETNoteEsame);
            if(!periodicita.equals("Altro")) {
                try {
                    esame.put(KEY_PERIODICITA, Util.setPeriodicita(ConverterStringToCalendar(data, ora), periodicita));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(!ETNote.getText().toString().isEmpty()) esame.put(KEY_NOTE, ETNote.getText().toString());
            if(!ETRicorda.getText().toString().isEmpty()) esame.put(KEY_RICORDA, ETRicorda.getText().toString());


            esamiRef.add(esame);
            Toast.makeText(getContext(), "Esame aggiunto", Toast.LENGTH_LONG).show();
            Navigation.findNavController(root).popBackStack();

    }

    //Restituisce false se lo contiene e true se non contiene l'esame
    private boolean contiene(List<Map<String, Object>> tmp, Map<String, Object> analisi) {
        for (int i = 0; i < tmp.size(); i++){
            Log.d("contiene", tmp.get(i).get(KEY_NOME)+ " - "+ analisi.get(KEY_NOME));
            String nome = (String) tmp.get(i).get(KEY_NOME);
            String nome1 = (String) analisi.get(KEY_NOME);
            if(nome.compareTo(nome1) == 0) return false;
        }
        return true;
    }

    private void setDatePicker(MaterialDatePicker materialDatePicker, TextInputEditText etDate) {
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            data = new Date();
            Calendar inizio = Calendar.getInstance();
            inizio.setTimeInMillis((Long) selection);
            data = inizio.getTime();
            etDate.setText(Util.DateToString(data));
        });
    }

    private void setTimePicker(MaterialTimePicker materialTimePicker, TextInputEditText textInputEditText){
        materialTimePicker.addOnPositiveButtonClickListener(v -> {
            ora = new Date();
            ora.setHours(materialTimePicker.getHour());
            ora.setMinutes(materialTimePicker.getMinute());
            textInputEditText.setText(materialTimePicker.getHour()+":"+materialTimePicker.getMinute());
        });
    }

    public static void deleteAnalisi(String nome){
        List<Map<String, Object>> list = mAnalisi.getValue();
        for(int i = 0; i < list.size(); i++){
            String nome1 = (String) list.get(i).get(KEY_NOME);
            if(nome1.compareTo(nome) == 0){
                list.remove(i);
            }
        }
        mAnalisi.setValue(list);
    }
}