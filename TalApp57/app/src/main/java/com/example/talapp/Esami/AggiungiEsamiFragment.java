package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aggiungi_esami, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        //Data
        TextInputEditText ETDate = root.findViewById(R.id.editTextDateEsame);
        TextInputLayout TILDataEsame = root.findViewById(R.id.TILDataEsame);
        setListeners(ETDate, TILDataEsame, "Inserisci una data valida");
        ETDate.setOnClickListener(v -> {
            DialogFragment dialogFragment = new DatePickerFragment(ETDate);
            dialogFragment.show(getParentFragmentManager(), "datePicker");
        });

        //Orario
        TextInputEditText ETTime = root.findViewById(R.id.editTextTimeEsame);
        TextInputLayout TILOraEsame = root.findViewById(R.id.TILOraEsame);
        setListeners(ETTime, TILOraEsame, "Inserisci un orario valido");
        ETTime.setOnClickListener(v -> {
            DialogFragment dialogFragment = new TimePickerFragment(ETTime);
            dialogFragment.show(getParentFragmentManager(), "datePicker");
        });

        //Nome
        TextInputEditText ETNome = root.findViewById(R.id.editTextNomeEsame);
        TextInputLayout TILNomeEsame = root.findViewById(R.id.TILNomeEsame);
        setListeners(ETNome, TILNomeEsame, "Inserisci un nome valido");

        //Spinner periodicita
        AutoCompleteTextView Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
        setSpinner(getResources().getStringArray(R.array.periodicita), Speriodicita, getActivity());

        //Spinner tipo
        AutoCompleteTextView Stipo = root.findViewById(R.id.spinnerTipoEsame);
        setSpinner(getResources().getStringArray(R.array.TipoEsame), Stipo, getActivity());

        //Spinner 24h
        SwitchMaterial Sattivazione = root.findViewById(R.id.switchAttivazione24h);
        Sattivazione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) root.findViewById(R.id.LLRicordami).setVisibility(View.VISIBLE);
                else root.findViewById(R.id.LLRicordami).setVisibility(View.GONE);
            }
        });

        //Bottone salva
        root.findViewById(R.id.buttonSalvaEsame).setOnClickListener(v -> {
            if(checkError(ETDate, TILDataEsame, "Inserisci una data valida") && checkError(ETTime, TILOraEsame, "Inserisci un orario valido") && checkError(ETNome, TILNomeEsame, "Inserisci un nome valido")){
                Calendar date = Calendar.getInstance();
                try {
                    date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Map<String, Object> esame = new HashMap<>();
                esame.put(KEY_DATA, date.getTime());
                esame.put(KEY_NOME, ETNome.getText().toString());
                esame.put(KEY_TIPO, Stipo.getText().toString());
                SwitchMaterial Sdigiuno = root.findViewById(R.id.switchDigiunoEsame);
                if(Sdigiuno.isChecked()) {
                    Calendar tmp = Calendar.getInstance();
                    tmp.setTime(date.getTime());
                    tmp.add(Calendar.DATE, -1);
                    esame.put(KEY_DIGIUNO, tmp.getTime());
                }
                if(Sattivazione.isChecked()){
                    Calendar tmp = Calendar.getInstance();
                    tmp.setTime(date.getTime());
                    tmp.add(Calendar.DATE, -1);
                    esame.put(KEY_ATTIVAZIONE, tmp.getTime());
                }
                TextInputEditText ETRicorda = root.findViewById(R.id.editTextRicorda);
                TextInputEditText ETNote = root.findViewById(R.id.ETNoteEsame);
                if(!Speriodicita.toString().equals("Altro")) esame.put(KEY_PERIODICITA, Util.setPeriodicita(date, Speriodicita.toString()));
                if(!ETNote.getText().toString().isEmpty()) esame.put(KEY_NOTE, ETNote.getText().toString());
                if(!ETRicorda.getText().toString().isEmpty()) esame.put(KEY_RICORDA, ETRicorda.getText().toString());


                esamiRef.add(esame);
                Toast.makeText(getContext(), "Esame aggiunto", Toast.LENGTH_LONG).show();
                Navigation.findNavController(root).popBackStack();
            }
        });

        return root;
    }
}