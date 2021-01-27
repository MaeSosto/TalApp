package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
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


public class AggiungiEsamiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aggiungi_esami, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        EditText ETDate = root.findViewById(R.id.editTextDateEsame);
        ETDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDate);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        EditText ETTime = root.findViewById(R.id.editTextTimeEsame);
        ETTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(ETTime);
                dialogFragment.show(getParentFragmentManager(), "timepicker");
            }
        });
        EditText ETNome = root.findViewById(R.id.editTextNomeEsame);
        root.findViewById(R.id.buttonSalvaEsame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ETDate.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data valida", Toast.LENGTH_SHORT).show();
                }
                if(ETTime.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un orario valido", Toast.LENGTH_SHORT).show();
                }
                if(ETNome.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar date = Calendar.getInstance();
                    try {
                        date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    EditText ETNome = root.findViewById(R.id.editTextNomeEsame);
                    Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
                    Spinner Stipo = root.findViewById(R.id.spinnerTipoEsame);
                    Switch Sdigiuno = root.findViewById(R.id.switchDigiunoEsame);
                    Switch Sattivazione = root.findViewById(R.id.switchAttivazione24h);
                    EditText ETRicorda = root. findViewById(R.id.editTextRicorda);
                    EditText ETNote = root.findViewById(R.id.ETNoteEsame);

                    Map<String, Object> esame = new HashMap<>();
                    esame.put(KEY_DATA, date.getTime());
                    esame.put(KEY_NOME, ETNome.getText().toString());
                    esame.put(KEY_PERIODICITA, Speriodicita.getSelectedItem().toString());
                    esame.put(KEY_TIPO, Stipo.getSelectedItem().toString());
                    esame.put(KEY_DIGIUNO, Sdigiuno.isChecked());
                    esame.put(KEY_ATTIVAZIONE, Sattivazione.isChecked());
                    if(!ETNote.getText().toString().isEmpty()) {
                        esame.put(KEY_NOTE, ETNote.getText().toString());
                    }
                    if(!ETRicorda.getText().toString().isEmpty()) {
                        esame.put(KEY_RICORDA, ETRicorda.getText().toString());
                    }

                    esamiRef.add(esame);
                    Toast.makeText(getContext(), "Esame aggiunto", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(root).popBackStack();
                }
            }
        });

        return root;
    }
}