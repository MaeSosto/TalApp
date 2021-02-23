package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.ConverterStringToCalendar;
import static com.example.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aggiungi_esami, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

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

        //Nome
        AutoCompleteTextView ETNome = root.findViewById(R.id.editTextNomeEsame);
        TextInputLayout TILNomeEsame = root.findViewById(R.id.TILNomeEsame);
        setListeners(ETNome, TILNomeEsame, "Inserisci un nome valido");
        setSpinner(getResources().getStringArray(R.array.NomiEsami), ETNome, getActivity());

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
            if(checkError(ETDate, TILDataEsame, "Inserisci una data valida") && checkError(ETTime, TILOraEsame, "Inserisci un orario valido") && checkError(ETNome, TILNomeEsame, "Inserisci un nome valido")){

                Map<String, Object> esame = new HashMap<>();
                try {
                    esame.put(KEY_DATA, ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                esame.put(KEY_NOME, ETNome.getText().toString());
                esame.put(KEY_TIPO, Stipo.getText().toString());
                SwitchMaterial Sdigiuno = root.findViewById(R.id.switchDigiunoEsame);
                if(Sdigiuno.isChecked()) {
                    Calendar tmp = Calendar.getInstance();
                    try {
                        tmp.setTime(ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tmp.add(Calendar.DATE, -1);
                    esame.put(KEY_DIGIUNO, tmp.getTime());
                }
                if(Sattivazione.isChecked()){
                    Calendar tmp = Calendar.getInstance();
                    try {
                        tmp.setTime(ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tmp.add(Calendar.DATE, -1);
                    esame.put(KEY_ATTIVAZIONE, tmp.getTime());
                }
                TextInputEditText ETRicorda = root.findViewById(R.id.editTextRicorda);
                TextInputEditText ETNote = root.findViewById(R.id.ETNoteEsame);
                if(!Speriodicita.toString().equals("Altro")) {
                    try {
                        esame.put(KEY_PERIODICITA, Util.setPeriodicita(ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString()), Speriodicita.toString()));
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
        });

        return root;
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
}