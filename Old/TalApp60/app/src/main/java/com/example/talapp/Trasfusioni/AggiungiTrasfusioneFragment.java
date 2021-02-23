package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_UNITA;
import static com.example.talapp.Utils.Util.checkError;
import static com.example.talapp.Utils.Util.setListeners;
import static com.example.talapp.Utils.Util.setSpinner;


public class AggiungiTrasfusioneFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aggiungi_trasfusione, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        //Data
        TextInputEditText ETDate = root.findViewById(R.id.editTextDateTrasfusione);
        TextInputLayout TILData = root.findViewById(R.id.TILDataTrasfusione);
        setListeners(ETDate, TILData, "Inserisci una data valida");
        ETDate.setOnClickListener(v -> {
            DialogFragment dialogFragment = new DatePickerFragment(ETDate);
            dialogFragment.show(getParentFragmentManager(), "datePicker");
        });

        //Orario
        TextInputEditText ETTime = root.findViewById(R.id.editTextTimeTrasfusione);
        TextInputLayout TILOra = root.findViewById(R.id.TILOratrasfusione);
        setListeners(ETTime, TILOra, "Inserisci un orario valido");
        ETTime.setOnClickListener(v -> {
            DialogFragment dialogFragment = new TimePickerFragment(ETTime);
            dialogFragment.show(getParentFragmentManager(), "datePicker");
        });

        //Unita'
        AutoCompleteTextView unita = root.findViewById(R.id.spinnerUnita);
        setSpinner(getResources().getStringArray(R.array.unita), unita, getActivity());

        //Bottone salva trasfusione
        root.findViewById(R.id.buttonSalvaTrasfusione).setOnClickListener(v -> {
            if(checkError(ETDate, TILData, "Inserisci una data valida") && checkError(ETTime, TILOra, "Inserisci un orario valido")){
                Calendar date = Calendar.getInstance();
                try {
                    date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //CREO LA TRASFUSIONE
                Map<String, Object> trasfusione = new HashMap<>();
                trasfusione.put(KEY_DATA, date.getTime());
                trasfusione.put(KEY_UNITA, unita.getText().toString());
                TextInputEditText ETNote = root.findViewById(R.id.ETNoteTrasfusione);
                if(!ETNote.getText().toString().isEmpty()) {
                    trasfusione.put(KEY_NOTE, ETNote.getText().toString());
                }

                //AGGIUNGO AL DB
                trasfusioniRef.add(trasfusione);
                Toast.makeText(getContext(), "Trasfusione aggiunta", Toast.LENGTH_LONG).show();
                Navigation.findNavController(root).popBackStack();
            }
        });
        return root;
    }
}