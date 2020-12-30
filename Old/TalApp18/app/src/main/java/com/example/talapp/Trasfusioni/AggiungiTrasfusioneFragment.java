package com.example.talapp.Trasfusioni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;

import com.example.talapp.Database.TrasfusioniViewModel;
import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;

import java.text.ParseException;
import java.util.Calendar;

import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class AggiungiTrasfusioneFragment extends Fragment {

    private EditText ETDate, ETTime, ETNote;
    private String Error = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aggiungi_trasfusione, container, false);

        ETDate = root.findViewById(R.id.editTextDateTrasfusione);
        ETDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDate);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        ETTime = root.findViewById(R.id.editTextTimeTrasfusione);
        ETTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(ETTime);
                dialogFragment.show(getParentFragmentManager(), "timepicker");
            }
        });

        ETNote = root.findViewById(R.id.editTextNoteTrasfusione);
        Spinner Sunita = root.findViewById(R.id.spinnerUnitaTrasfusione);

        root.findViewById(R.id.buttonSalvaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!check()){
                    Toast.makeText(getContext(), Error, Toast.LENGTH_LONG);
                }
                else{
                    Calendar date = Calendar.getInstance();
                    try {
                        date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //PROVO AD AGGIUNGERE LA TRASFUSIONE
                    trasfusioniViewModel.InsertTrasfusione(root, Sunita.getSelectedItem().toString(), date, ETNote.getText().toString());
                }
            }
        });
        return root;
    }

    private boolean check(){
        if (ETDate.getText().toString().isEmpty()){
            Error = "Inserisci una data valida";
            return false;
        }
        if(ETTime.getText().toString().isEmpty()){
            Error = "Inserisci un'orario valida";
            return false;
        }
        return true;
    }
}