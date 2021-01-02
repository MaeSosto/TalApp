package com.example.talapp.Esami;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Trasfusioni.TrasfusioniListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_ESAME_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_ESAME_DATA;
import static com.example.talapp.Utils.Util.KEY_ESAME_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOME;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOTE;
import static com.example.talapp.Utils.Util.KEY_ESAME_PERIODICITA;
import static com.example.talapp.Utils.Util.KEY_ESAME_RICORDA;
import static com.example.talapp.Utils.Util.KEY_ESAME_TIPO;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class EsamiViewModel extends AndroidViewModel {
    private static Context context;


    public EsamiViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public static void InsertEsame(Calendar date, View root) {
        if(isConnectedToInternet(context)){
            EditText ETNome = root.findViewById(R.id.editTextNomeEsame);
            Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
            Spinner Stipo = root.findViewById(R.id.spinnerTipoEsame);
            Switch Sdigiuno = root.findViewById(R.id.switchDigiunoEsame);
            Switch Sattivazione = root.findViewById(R.id.switchAttivazione24h);
            EditText ETRicorda = root. findViewById(R.id.editTextRicorda);
            EditText ETNote = root.findViewById(R.id.ETNoteEsame);

            Map<String, Object> esame = new HashMap<>();
            esame.put(KEY_ESAME_DATA, date.getTime());
            esame.put(KEY_ESAME_NOME, ETNome.getText().toString());
            esame.put(KEY_ESAME_PERIODICITA, Speriodicita.getSelectedItem().toString());
            esame.put(KEY_ESAME_TIPO, Stipo.getSelectedItem().toString());
            esame.put(KEY_ESAME_DIGIUNO, Sdigiuno.isChecked());
            esame.put(KEY_ESAME_ATTIVAZIONE, Sattivazione.isChecked());
            if(!ETNote.getText().toString().isEmpty()) {
                esame.put(KEY_ESAME_NOTE, ETNote.getText().toString());
            }
            if(!ETRicorda.getText().toString().isEmpty()) {
                esame.put(KEY_ESAME_RICORDA, ETRicorda.getText().toString());
            }

            esamiRef.add(esame)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Esame aggiunto", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(root).popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Errore di aggiunta", Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void getAllEsami(RecyclerView recyclerView) {
        if(isConnectedToInternet(context)){
            esamiRef.orderBy(KEY_ESAME_DATA, Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }

                            QuerySnapshot mEsami = value;
                            EsamiListAdapter esamiListAdapter = new EsamiListAdapter(context);
                            recyclerView.setAdapter(esamiListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            esamiListAdapter.setEsami(mEsami.getDocuments());
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }
}
