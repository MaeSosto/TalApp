package com.example.talapp.Trasfusioni;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.isConnectedToInternet;


public class ModificaTrasfusioneFragment extends Fragment {

    private String id;
    Map<String, Object> trasfusione_old;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("TrasfusioneID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_modifica_trasfusione, container, false);
        TextView TXVdata = root.findViewById(R.id.TXVDataTrasfusione);
        Spinner Sunita = root.findViewById(R.id.spinnerUnitaTrasfusione);
        EditText EThb = root.findViewById(R.id.editTextHbTrasfusione);
        EditText ETNote = root.findViewById(R.id.ETNoteTrasfusione);

        if(isConnectedToInternet(getContext())) {
            trasfusioniRef.document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                trasfusione_old = task.getResult().getData();
                                TXVdata.setText("Trasfusione del " + Util.DateToString(Util.LongToDate((Long) trasfusione_old.get(KEY_TRASFUSIONE_DATA))) + " ore " + Util.LongToDate((Long) trasfusione_old.get(KEY_TRASFUSIONE_DATA)).getHours() + ":" + Util.LongToDate((Long) trasfusione_old.get(KEY_TRASFUSIONE_DATA)).getMinutes());
                                Sunita.setSelection(((ArrayAdapter) Sunita.getAdapter()).getPosition(trasfusione_old.get(KEY_TRASFUSIONE_UNITA)));
                                if (trasfusione_old.containsKey(KEY_TRASFUSIONE_HB)) {
                                    EThb.setText((CharSequence) trasfusione_old.get(KEY_TRASFUSIONE_HB));
                                }
                                if (trasfusione_old.containsKey(KEY_TRASFUSIONE_NOTE)) {
                                    ETNote.setText((CharSequence) trasfusione_old.get(KEY_TRASFUSIONE_NOTE));
                                }

                                root.findViewById(R.id.buttonModificaTrasfusione).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(isConnectedToInternet(getContext())){

                                            trasfusione_old.put(KEY_TRASFUSIONE_UNITA, Sunita.getSelectedItem().toString());
                                            if(!EThb.getText().toString().isEmpty()) {
                                                trasfusione_old.put(KEY_TRASFUSIONE_HB, EThb.getText().toString());
                                            }
                                            else {
                                                trasfusione_old.put(KEY_TRASFUSIONE_HB, FieldValue.delete());
                                            }
                                            if(!ETNote.getText().toString().isEmpty()) {
                                                trasfusione_old.put(KEY_TRASFUSIONE_NOTE, ETNote.getText().toString());
                                            }
                                            else {
                                                trasfusione_old.put(KEY_TRASFUSIONE_NOTE, FieldValue.delete());
                                            }

                                            trasfusioniRef.document(id).update(trasfusione_old)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "Trasfusione aggiornata", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(root).popBackStack();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i("Trasfusione", "Errore");
                                                            Toast.makeText(getContext(), "Errore di modifica", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(getContext(), "Errore di connessione", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                root.findViewById(R.id.buttonEliminaTrasfusione).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(isConnectedToInternet(getContext())){
                                            DocumentReference doc = trasfusioniRef.document(id);
                                            trasfusioniRef.document(id).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //Toast.makeText(getContext(), "Trasfusione eliminata", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(root).popBackStack();
                                                            SnackbarUndo SU = new SnackbarUndo();
                                                            SU.trasfusioneRimossa(trasfusione_old, trasfusioniRef);
                                                            Snackbar snackbar = Snackbar.make(v, "Trasfusione rimossa", BaseTransientBottomBar.LENGTH_LONG);
                                                            snackbar.setAction("Cancella operazione", SU);
                                                            snackbar.show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i("Trasfusione", "Errore");
                                                            Toast.makeText(getContext(), "Errore di eliminazione", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(getContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
        return root;
    }
}