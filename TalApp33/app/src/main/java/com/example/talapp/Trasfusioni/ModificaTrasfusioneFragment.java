package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_HB;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_UNITA;


public class ModificaTrasfusioneFragment extends Fragment {
    private Map<String, Object> trasfusione_old;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        //Quando fai back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               trasfusione_old = null;
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_modifica_trasfusione, container, false);

        Spinner Sunita = root.findViewById(R.id.spinnerPeriodicitaEsame);
        EditText EThb = root.findViewById(R.id.editTextEsito);
        EditText ETNote = root.findViewById(R.id.ETNoteTrasfusione);
        String id = getArguments().getString("TrasfusioneID");

        //Prendo la trasfusione da modificare
        trasfusioniRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                trasfusione_old = task.getResult().getData();
                TextView TXVdata = root.findViewById(R.id.TXVDataTrasfusione);
                TXVdata.setText("Trasfusione del " + Util.DateToString(Util.TimestampToDate((Timestamp) trasfusione_old.get(KEY_DATA))) + " ore " +  DateToOrario(Util.TimestampToDate((Timestamp) trasfusione_old.get(KEY_DATA))));
                Sunita.setSelection(((ArrayAdapter) Sunita.getAdapter()).getPosition(trasfusione_old.get(KEY_UNITA)));
                if (trasfusione_old.containsKey(KEY_HB)) {
                    EThb.setText(String.valueOf((Double) trasfusione_old.get(KEY_HB)));
                }
                if (trasfusione_old.containsKey(KEY_NOTE)) {
                    ETNote.setText((CharSequence) trasfusione_old.get(KEY_NOTE));
                }
            }
        });

        //Bottone modifica trasfusione
        root.findViewById(R.id.buttonModifica).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trasfusione_old.put(KEY_UNITA, Sunita.getSelectedItem().toString());
                if (!EThb.getText().toString().isEmpty()) {
                    trasfusione_old.put(KEY_HB, Double.parseDouble(EThb.getText().toString()));
                } else {
                    trasfusione_old.put(KEY_HB, FieldValue.delete());
                }
                if (!ETNote.getText().toString().isEmpty()) {
                    trasfusione_old.put(KEY_NOTE, ETNote.getText().toString());
                } else {
                    trasfusione_old.put(KEY_NOTE, FieldValue.delete());
                }

                trasfusioniRef.document(id).update(trasfusione_old);
                Toast.makeText(getContext(), "Trasfusione aggiornata", Toast.LENGTH_SHORT).show();
                trasfusione_old = null;
                Navigation.findNavController(root).popBackStack();
            }
        });

        //Bottone elimina
        root.findViewById(R.id.buttonElimina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alarm.removeAlarm(id, ACTION_TRASFUSIONE);
                trasfusioniRef.document(id).delete();
                Navigation.findNavController(root).popBackStack();
                SnackbarUndo SU = new SnackbarUndo();
                SU.rimuovi(trasfusione_old, trasfusioniRef);
                Snackbar snackbar = Snackbar.make(root, "Trasfusione rimossa", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setAction("Cancella operazione", SU);
                snackbar.show();
                Navigation.findNavController(root).popBackStack();
            }
        });

        return root;
    }
}