package com.example.talapp.Esami;

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

import static com.example.talapp.Esami.EsamiFragment.setTipoEsami;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_PERIODICITA;


public class ModificaEsamiFragment extends Fragment {
    private static Map<String, Object> esame_old;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
               esame_old = null;
               setTipoEsami(ESAMI_LABORATORIO);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_modifica_esami, container, false);
        String id = getArguments().getString("ID");

        esamiRef.document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            esame_old = task.getResult().getData();
                            Timestamp tmp = (Timestamp) esame_old.get(KEY_DATA);

                            TextView TXVdata = root.findViewById(R.id.TXVDataEsame);
                            TXVdata.setText(esame_old.get(KEY_NOME)+" del " + Util.DateToString(tmp.toDate()) + " ore " +  DateToOrario(tmp.toDate()));

                            EditText ETEsito = root.findViewById(R.id.editTextEsito);
                            if (esame_old.containsKey(KEY_ESITO)) {
                                ETEsito.setText(String.valueOf((Double) esame_old.get(KEY_ESITO)));
                            }

                            Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
                            Speriodicita.setSelection(((ArrayAdapter) Speriodicita.getAdapter()).getPosition(esame_old.get(KEY_PERIODICITA)));

                            EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
                            if (esame_old.containsKey(KEY_NOME)) {
                                ETNote.setText((CharSequence) esame_old.get(KEY_NOME));
                            }
                        }
                    }
                });

        //Bottone modifica esame
        root.findViewById(R.id.buttonModifica).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETEsito = root.findViewById(R.id.editTextEsito);
                if (!ETEsito.getText().toString().isEmpty()) {
                    esame_old.put(KEY_ESITO, Double.parseDouble(ETEsito.getText().toString()));
                } else {
                    esame_old.put(KEY_ESITO, FieldValue.delete());
                }

                Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
                esame_old.put(KEY_PERIODICITA, Speriodicita.getSelectedItem().toString());

                EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
                if (!ETNote.getText().toString().isEmpty()) {
                    esame_old.put(KEY_NOTE, ETNote.getText().toString());
                } else {
                    esame_old.put(KEY_NOTE, FieldValue.delete());
                }

                esamiRef.document(id).update(esame_old);
                Toast.makeText(getContext(), "Esame aggiornata", Toast.LENGTH_SHORT).show();
                esame_old = null;
                Navigation.findNavController(root).popBackStack();
            }
        });

        //Bottone elimina esame
        root.findViewById(R.id.buttonElimina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esamiRef.document(id).delete();
                Navigation.findNavController(root).popBackStack();
                SnackbarUndo SU = new SnackbarUndo();
                SU.rimuovi(esame_old, esamiRef);
                Snackbar snackbar = Snackbar.make(root, "Esame rimosso", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setAction("Cancella operazione", SU);
                snackbar.show();
                Navigation.findNavController(root).popBackStack();
            }
        });

        return root;
    }
}