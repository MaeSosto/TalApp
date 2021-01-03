package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;

import java.text.ParseException;
import java.util.Calendar;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class AggiungiTrasfusioneFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aggiungi_trasfusione, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        EditText ETDate = root.findViewById(R.id.editTextDate);
        ETDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(ETDate);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        EditText ETTime = root.findViewById(R.id.editTextTime);
        ETTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment(ETTime);
                dialogFragment.show(getParentFragmentManager(), "timepicker");
            }
        });

        EditText ETNote = root.findViewById(R.id.editTextNoteTrasfusione);
        Spinner Sunita = root.findViewById(R.id.spinnerPeriodicitaEsame);

        root.findViewById(R.id.buttonSalvaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ETDate.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una data valida", Toast.LENGTH_SHORT).show();
                }
                if(ETTime.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci un orario valido", Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar date = Calendar.getInstance();
                    try {
                        date = Util.ConverterStringToCalendar(ETDate.getText().toString(), ETTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    trasfusioniViewModel.InsertTrasfusione(root, Sunita, date, ETNote);
                }
            }
        });
        return root;
    }
}