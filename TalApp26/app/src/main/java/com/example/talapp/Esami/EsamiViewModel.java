package com.example.talapp.Esami;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.talapp.R;
import com.example.talapp.Trasfusioni.TrasfusioniListAdapter;
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.DateToString;
import static com.example.talapp.Utils.Util.KEY_ESAME_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_ESAME_DATA;
import static com.example.talapp.Utils.Util.KEY_ESAME_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_ESAME_ESITO;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOME;
import static com.example.talapp.Utils.Util.KEY_ESAME_NOTE;
import static com.example.talapp.Utils.Util.KEY_ESAME_PERIODICITA;
import static com.example.talapp.Utils.Util.KEY_ESAME_RICORDA;
import static com.example.talapp.Utils.Util.KEY_ESAME_TIPO;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class EsamiViewModel extends AndroidViewModel {
    private static Context context;
    private static Map <String, Object> esame_old;

    public EsamiViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        esame_old = null;
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

    public static void setEsame_old(Map<String, Object> esame_old) {
        EsamiViewModel.esame_old = esame_old;
    }


    public void setEsame(String id, View root) {
        if(isConnectedToInternet(context)) {
            esamiRef.document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                esame_old = task.getResult().getData();
                                Timestamp tmp = (Timestamp) esame_old.get(KEY_TRASFUSIONE_DATA);

                                TextView TXVdata = root.findViewById(R.id.TXVDataEsame);
                                TXVdata.setText(esame_old.get(KEY_ESAME_NOME)+"del " + Util.DateToString(tmp.toDate()) + " ore " +  DateToOrario(tmp.toDate()));

                                EditText ETEsito = root.findViewById(R.id.editTextEsito);
                                if (esame_old.containsKey(KEY_ESAME_ESITO)) {
                                    ETEsito.setText(String.valueOf((Double) esame_old.get(KEY_ESAME_ESITO)));
                                }

                                Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
                                Speriodicita.setSelection(((ArrayAdapter) Speriodicita.getAdapter()).getPosition(esame_old.get(KEY_ESAME_PERIODICITA)));

                                EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
                                if (esame_old.containsKey(KEY_ESAME_NOME)) {
                                    ETNote.setText((CharSequence) esame_old.get(KEY_ESAME_NOME));
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }

    }

    public void updateEsame(View root, String id){
        if(isConnectedToInternet(context)) {

            EditText ETEsito = root.findViewById(R.id.editTextEsito);
            if (!ETEsito.getText().toString().isEmpty()) {
                esame_old.put(KEY_ESAME_ESITO, Double.parseDouble(ETEsito.getText().toString()));
            } else {
                esame_old.put(KEY_ESAME_ESITO, FieldValue.delete());
            }

            Spinner Speriodicita = root.findViewById(R.id.spinnerPeriodicitaEsame);
            esame_old.put(KEY_ESAME_PERIODICITA, Speriodicita.getSelectedItem().toString());

            EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
            if (!ETNote.getText().toString().isEmpty()) {
                esame_old.put(KEY_ESAME_NOTE, ETNote.getText().toString());
            } else {
                esame_old.put(KEY_ESAME_NOTE, FieldValue.delete());
            }

            esamiRef.document(id).update(esame_old)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Esame aggiornata", Toast.LENGTH_SHORT).show();
                            esame_old = null;
                            Navigation.findNavController(root).popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Errore di modifica", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteEsame(View root, String id){
        if(isConnectedToInternet(context)) {

            esamiRef.document(id).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Navigation.findNavController(root).popBackStack();
                            SnackbarUndo SU = new SnackbarUndo();
                            SU.rimuovi(esame_old, esamiRef);
                            Snackbar snackbar = Snackbar.make(root, "Esame rimosso", BaseTransientBottomBar.LENGTH_LONG);
                            snackbar.setAction("Cancella operazione", SU);
                            snackbar.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Errore di eliminazione", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void setCalendarioEsami(Calendar cmese, Calendar fmese, Map<String, Util.Evento> giorni) {
        if(isConnectedToInternet(context)){
            esamiRef.whereLessThanOrEqualTo(KEY_ESAME_DATA, fmese.getTime())
                    .whereGreaterThanOrEqualTo(KEY_ESAME_DATA, cmese.getTime())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            List<DocumentSnapshot> documents = value.getDocuments();
                            if(documents.size()>0) {
                                for (int i = 0; i < documents.size(); i++) {
                                    Map<String, Object> map = documents.get(i).getData();
                                    Calendar calendar = Calendar.getInstance();
                                    Timestamp data = (Timestamp) map.get(KEY_ESAME_DATA);
                                    calendar.setTime( data.toDate());
                                    if(!giorni.containsKey(DateToString(calendar.getTime()))){
                                        giorni.put(DateToString(calendar.getTime()),new Util.Evento(false, true, false));
                                    }
                                    else{
                                        Util.Evento e = giorni.get(DateToString(calendar.getTime()));
                                        e.setEsame(true);
                                        giorni.put(DateToString(calendar.getTime()), e);
                                    }
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void getEsamiGiorno(Calendar giorno, RecyclerView recyclerView){
        Calendar inizio= Calendar.getInstance();
        inizio.setTime(giorno.getTime());
        inizio.set(Calendar.HOUR_OF_DAY, 0);
        inizio.set(Calendar.MINUTE, 0);
        inizio.set(Calendar.SECOND, 0);
        inizio.set(Calendar.MILLISECOND, 0);
        Calendar fine= Calendar.getInstance();
        fine.setTime(giorno.getTime());
        fine.set(Calendar.HOUR_OF_DAY, 24);
        fine.set(Calendar.MINUTE, 59);
        fine.set(Calendar.SECOND, 59);
        fine.set(Calendar.MILLISECOND, 59);
        if(isConnectedToInternet(context)){
            esamiRef.whereGreaterThanOrEqualTo(KEY_ESAME_DATA, inizio.getTime())
                    .whereLessThanOrEqualTo(KEY_ESAME_DATA, fine.getTime())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            List<DocumentSnapshot> documents = value.getDocuments();
                            EsamiListAdapter esamiListAdapter = new EsamiListAdapter(context);
                            recyclerView.setAdapter(esamiListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            esamiListAdapter.setEsami(documents);
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

}
