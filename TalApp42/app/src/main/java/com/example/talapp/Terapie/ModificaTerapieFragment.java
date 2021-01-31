package com.example.talapp.Terapie;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;


public class ModificaTerapieFragment extends Fragment {

    private Map<String, Object> terapia_old;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));

        //Quando fai back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                terapia_old = null;
                remove();
            }
        };
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_modifica_terapie, container, false);

        String id = getArguments().getString("ID");

        TextView TXVNomeTerapia = root.findViewById(R.id.TXVNomeTerapia);
        EditText editTextDataFineTerapia = root.findViewById(R.id.editTextDataFineTerapia);
        //Setto la data di fine
        editTextDataFineTerapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(editTextDataFineTerapia);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        EditText editTextDoseFarmaco = root.findViewById(R.id.editTextDoseFarmaco);
        EditText editTextNoteTerapiaModifica = root.findViewById(R.id.editTextNoteTerapiaModifica);

        //Prendo la terapia da modificare
        terapieRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                terapia_old = task.getResult().getData();
                TXVNomeTerapia.setText((String)terapia_old.get(KEY_NOME));
                editTextDataFineTerapia.setText(Util.TimestampToStringData((Timestamp) terapia_old.get(KEY_DATA_FINE)));
                editTextDoseFarmaco.setText(String.valueOf((double) terapia_old.get(KEY_DOSE)));
                editTextNoteTerapiaModifica.setText((String) terapia_old.get(KEY_NOTE));
            }
        });

        //Bottone modifica
        root.findViewById(R.id.buttonModificaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextDataFineTerapia.getText().toString().isEmpty() || (Util.StringToDate(editTextDataFineTerapia.getText().toString()).compareTo(Util.TimestampToDate((Timestamp) terapia_old.get(KEY_DATA))) < 0)){
                    Toast.makeText(getContext(), "Inserisci una data di fine terapia valida", Toast.LENGTH_SHORT).show();
                }
                else if(editTextDoseFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una dose valida", Toast.LENGTH_SHORT).show();
                }
                else {
                    terapia_old.put(KEY_DATA_FINE, Util.StringToDate(editTextDataFineTerapia.getText().toString()));
                    if (!editTextDoseFarmaco.getText().toString().isEmpty()) {
                        terapia_old.put(KEY_DOSE, Double.parseDouble(editTextDoseFarmaco.getText().toString()));
                    } else {
                        terapia_old.put(KEY_DOSE, FieldValue.delete());
                    }
                    if (!editTextNoteTerapiaModifica.getText().toString().isEmpty()) {
                        terapia_old.put(KEY_NOTE, editTextNoteTerapiaModifica.getText().toString());
                    } else {
                        terapia_old.put(KEY_NOTE, FieldValue.delete());
                    }

                    terapieRef.document(id).update(terapia_old);
                    Toast.makeText(getContext(), "Terapia aggiornata", Toast.LENGTH_SHORT).show();
                    terapia_old = null;
                    Navigation.findNavController(root).popBackStack();
                }
            }
        });

        //Bottone elimina
        root.findViewById(R.id.buttonEliminaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alarm.removeAlarm(id, ACTION_TRASFUSIONE);
                terapieRef.document(id).delete();
                Navigation.findNavController(root).popBackStack();
                SnackbarUndo SU = new SnackbarUndo();
                SU.rimuovi(terapia_old, terapieRef);
                Snackbar snackbar = Snackbar.make(root, "Terapia rimossa", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setAction("Cancella operazione", SU);
                snackbar.show();
                Navigation.findNavController(root).popBackStack();
            }
        });

        return root;
    }
}