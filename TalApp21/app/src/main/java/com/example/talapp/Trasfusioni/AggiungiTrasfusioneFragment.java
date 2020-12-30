package com.example.talapp.Trasfusioni;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.TimePickerFragment;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.isConnectedToInternet;
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

        ETTime = root.findViewById(R.id.editTextHbTrasfusione);
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

                    if(isConnectedToInternet(getContext())){
                        Map<String, Object> trasfusione = new HashMap<>();
                        trasfusione.put(KEY_TRASFUSIONE_DATA, Util.DateToLong(date.getTime()));
                        trasfusione.put(KEY_TRASFUSIONE_UNITA, Sunita.getSelectedItem().toString());
                        if(!ETNote.getText().toString().isEmpty()) {
                            trasfusione.put(KEY_TRASFUSIONE_NOTE, ETNote.getText().toString());
                        }

                        trasfusioniRef.add(trasfusione)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.i("Trasfusione", "Aggiunta");
                                        Toast.makeText(getContext(), "Trasfusione aggiunta", Toast.LENGTH_LONG).show();
                                        Navigation.findNavController(root).popBackStack();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("Trasfusione", "Errore");
                                        Toast.makeText(getContext(), "Errore di aggiunta", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else{
                        Toast.makeText(getContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
                    }
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