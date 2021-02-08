package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarRimuoviEvento;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_HB;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_UNITA;
import static com.example.talapp.Utils.Util.setSpinner;

public class ModificaTrasfusioneFragment extends Fragment {
    private Map<String, Object> trasfusione_old;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        //Quando fai back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               trasfusione_old = null;
               remove();
            }
        };
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_modifica_trasfusione, container, false);
        this.root = root;

        //Unita'
        AutoCompleteTextView unita = root.findViewById(R.id.spinnerUnita);
        setSpinner(getResources().getStringArray(R.array.unita), unita, getActivity());

        //Hb
        TextInputEditText EThb = root.findViewById(R.id.editTextHb);
        TextInputEditText ETNote = root.findViewById(R.id.ETNoteTrasfusione);
        String id = getArguments().getString("ID");

        //Prendo la trasfusione da modificare
        trasfusioniRef.document(id).get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            trasfusione_old = task.getResult().getData();
            MaterialTextView TXVdata = root.findViewById(R.id.TXVDataTrasfusione);
            TXVdata.setText("Trasfusione del " + Util.DateToString(Util.TimestampToDate((Timestamp) trasfusione_old.get(KEY_DATA))) + " ore " + DateToOrario(Util.TimestampToDate((Timestamp) trasfusione_old.get(KEY_DATA))));
            unita.setText((String)trasfusione_old.get(KEY_UNITA));
            if (trasfusione_old.containsKey(KEY_HB)) {
                EThb.setText(String.valueOf((Double) trasfusione_old.get(KEY_HB)));
            }
            if (trasfusione_old.containsKey(KEY_NOTE)) {
                ETNote.setText((CharSequence) trasfusione_old.get(KEY_NOTE));
            }
        });

        //Bottone modifica trasfusione
        root.findViewById(R.id.buttonModificaTerapia).setOnClickListener(v -> {
            trasfusione_old.put(KEY_UNITA, unita.getText().toString());
            if (!EThb.getText().toString().isEmpty()) trasfusione_old.put(KEY_HB, Double.parseDouble(EThb.getText().toString()));
            else trasfusione_old.put(KEY_HB, FieldValue.delete());
            if (!ETNote.getText().toString().isEmpty()) trasfusione_old.put(KEY_NOTE, ETNote.getText().toString());
            else trasfusione_old.put(KEY_NOTE, FieldValue.delete());

            trasfusioniRef.document(id).update(trasfusione_old);
            Toast.makeText(getContext(), "Trasfusione aggiornata", Toast.LENGTH_SHORT).show();
            trasfusione_old = null;
            Navigation.findNavController(root).popBackStack();
        });

        //Bottone elimina
        root.findViewById(R.id.buttonEliminaTerapia).setOnClickListener(v -> {
            //Alarm.removeAlarm(id, ACTION_TRASFUSIONE);
            trasfusioniRef.document(id).delete();
            Navigation.findNavController(root).popBackStack();
            SnackbarRimuoviEvento SU = new SnackbarRimuoviEvento();
            SU.rimuovi(trasfusione_old, trasfusioniRef);
            Snackbar snackbar = Snackbar.make(root, "Trasfusione rimossa", BaseTransientBottomBar.LENGTH_LONG);
            snackbar.setAction("Cancella operazione", SU);
            snackbar.show();
            Navigation.findNavController(root).popBackStack();
        });

        return root;
    }


}