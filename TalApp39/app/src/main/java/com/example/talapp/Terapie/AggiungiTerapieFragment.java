package com.example.talapp.Terapie;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.Util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_HB;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_UNITA;

public class AggiungiTerapieFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        Button buttonSalvaTerapia = root.findViewById(R.id.buttonSalvaTerapia);
        buttonSalvaTerapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNomeFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT).show();
                }
                if(ETDateInizio.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data di inizio terapia valida", Toast.LENGTH_SHORT).show();
                }
                if(ETDateFine.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data di fine terapia valida", Toast.LENGTH_SHORT).show();
                }
                if(editTextDoseFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una dose valida", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Creo la terapia
                    Map<String, Object> terapia = new HashMap<>();
                    terapia.put(KEY_NOME, Util.StringToDate(editTextNomeFarmaco.getText().toString()));
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