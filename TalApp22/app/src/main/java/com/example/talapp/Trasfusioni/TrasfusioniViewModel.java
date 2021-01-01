package com.example.talapp.Trasfusioni;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToLong;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.LongToDate;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;

import static com.example.talapp.Utils.Util.isConnectedToInternet;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;

public class TrasfusioniViewModel extends AndroidViewModel {
    private static Context context;

    public static Map<String, Object> getTrasfusione_old() {
        return trasfusione_old;
    }

    public static void setTrasfusione_old(Map<String, Object> trasfusione_old) {
        TrasfusioniViewModel.trasfusione_old = trasfusione_old;
    }

    private static Map <String, Object> trasfusione_old;

    public TrasfusioniViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        trasfusione_old = null;
    }

   public void InsertTrasfusione(View root, Spinner Sunita, Calendar date, EditText ETNote) {
       if(isConnectedToInternet(context)){
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
                           Toast.makeText(context, "Trasfusione aggiunta", Toast.LENGTH_LONG).show();
                           Navigation.findNavController(root).popBackStack();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Log.i("Trasfusione", "Errore");
                           Toast.makeText(context, "Errore di aggiunta", Toast.LENGTH_LONG).show();
                       }
                   });
       }
       else{
           Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
       }
    }

    public void getAllTrasfusioni(RecyclerView recyclerView){
        if(isConnectedToInternet(context)){
            trasfusioniRef.orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }

                            QuerySnapshot mTrasfusioni = value;
                            TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(context);
                            recyclerView.setAdapter(trasfusioniListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            trasfusioniListAdapter.setTrasfusioni(mTrasfusioni.getDocuments());
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void setTrasfusione(String id, TextView TXVdata, Spinner Sunita, EditText EThb, EditText ETNote){
        if(isConnectedToInternet(context)) {
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
                            }
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void updateTrasfusione(View root, String id, Spinner Sunita, EditText EThb, EditText ETNote){
        if(isConnectedToInternet(context)) {

            trasfusione_old.put(KEY_TRASFUSIONE_UNITA, Sunita.getSelectedItem().toString());
            if (!EThb.getText().toString().isEmpty()) {
                trasfusione_old.put(KEY_TRASFUSIONE_HB, EThb.getText().toString());
            } else {
                trasfusione_old.put(KEY_TRASFUSIONE_HB, FieldValue.delete());
            }
            if (!ETNote.getText().toString().isEmpty()) {
                trasfusione_old.put(KEY_TRASFUSIONE_NOTE, ETNote.getText().toString());
            } else {
                trasfusione_old.put(KEY_TRASFUSIONE_NOTE, FieldValue.delete());
            }

            trasfusioniRef.document(id).update(trasfusione_old)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Trasfusione aggiornata", Toast.LENGTH_SHORT).show();
                            trasfusione_old = null;
                            Navigation.findNavController(root).popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Trasfusione", "Errore");
                            Toast.makeText(context, "Errore di modifica", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteTrasfusione(View root, String id){
        if(isConnectedToInternet(context)) {

            trasfusioniRef.document(id).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Navigation.findNavController(root).popBackStack();
                            SnackbarUndo SU = new SnackbarUndo();
                            SU.trasfusioneRimossa(trasfusione_old, trasfusioniRef);
                            Snackbar snackbar = Snackbar.make(root, "Trasfusione rimossa", BaseTransientBottomBar.LENGTH_LONG);
                            snackbar.setAction("Cancella operazione", SU);
                            snackbar.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Trasfusione", "Errore");
                            Toast.makeText(context, "Errore di eliminazione", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void setCalendarioTrasfusioni(Calendar cmese, Calendar fmese, CalendarView calendarView) {
        if(isConnectedToInternet(context)){
            trasfusioniRef.whereLessThanOrEqualTo(KEY_TRASFUSIONE_DATA, DateToLong(fmese.getTime())).whereGreaterThanOrEqualTo(KEY_TRASFUSIONE_DATA, Util.DateToLong(cmese.getTime()))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                            if(mTrasfusioni.size()>0) {

                                List<EventDay> events = new ArrayList<>();
                                for (int i = 0; i < mTrasfusioni.size(); i++) {
                                    Map<String, Object> map = mTrasfusioni.get(i).getData();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(LongToDate((Long) map.get(KEY_TRASFUSIONE_DATA)));
                                    events.add(new EventDay(calendar, R.drawable.ic_reportevent));
                                    calendarView.setEvents(events);
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void getTrasfusioniGiorno(Calendar giorno, RecyclerView recyclerView){
        if(isConnectedToInternet(context)){
            Calendar ieri = Calendar.getInstance();
            //ieri.setTime(giorno.getTime());
            ieri.set(giorno.getTime().getYear(), giorno.getTime().getMonth(), giorno.getTime().getDate(), 0, 0, 0);
            Log.i("Inizio", String.valueOf(ieri));
            Calendar domani = Calendar.getInstance();
            //domani.setTime(giorno.getTime());
            domani.set(giorno.getTime().getYear(), giorno.getTime().getMonth(), giorno.getTime().getDate(), 23, 59, 59);
            Log.i("Fine", String.valueOf(domani));
            trasfusioniRef.whereLessThanOrEqualTo(KEY_TRASFUSIONE_DATA, DateToLong(domani.getTime())).whereGreaterThanOrEqualTo(KEY_TRASFUSIONE_DATA, Util.DateToLong(ieri.getTime()))
                   // trasfusioniRef.whereEqualTo(KEY_TRASFUSIONE_DATA, giorno)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            QuerySnapshot mTrasfusioni = value;
                            TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(context);
                            recyclerView.setAdapter(trasfusioniListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            trasfusioniListAdapter.setTrasfusioni(mTrasfusioni.getDocuments());
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }
}