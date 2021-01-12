package com.example.talapp.Trasfusioni;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToLong;
import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.DateToString;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;

import static com.example.talapp.Utils.Util.esamiViewModel;
import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class TrasfusioniViewModel extends AndroidViewModel {
    private static Context context;
    private static Date ultima, prossima; //Mi servono per prendere i valori della shcermata principale della trasfusione

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
           trasfusione.put(KEY_TRASFUSIONE_DATA, date.getTime());
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
                                Timestamp tmp = (Timestamp) trasfusione_old.get(KEY_TRASFUSIONE_DATA);
                                TXVdata.setText("Trasfusione del " + Util.DateToString(tmp.toDate()) + " ore " +  DateToOrario(tmp.toDate()));
                                Sunita.setSelection(((ArrayAdapter) Sunita.getAdapter()).getPosition(trasfusione_old.get(KEY_TRASFUSIONE_UNITA)));
                                if (trasfusione_old.containsKey(KEY_TRASFUSIONE_HB)) {
                                    EThb.setText(String.valueOf((Double) trasfusione_old.get(KEY_TRASFUSIONE_HB)));
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
                trasfusione_old.put(KEY_TRASFUSIONE_HB, Double.parseDouble(EThb.getText().toString()));
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
                            SU.rimuovi(trasfusione_old, trasfusioniRef);
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

    public void setCalendarioTrasfusioni(Calendar cmese, Calendar fmese, Map<String, Util.Evento> giorni) {
        if(isConnectedToInternet(context)){
            trasfusioniRef.whereLessThanOrEqualTo(KEY_TRASFUSIONE_DATA, fmese.getTime())
                    .whereGreaterThanOrEqualTo(KEY_TRASFUSIONE_DATA, cmese.getTime())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                            if(mTrasfusioni.size()>0) {
                                for (int i = 0; i < mTrasfusioni.size(); i++) {
                                    Map<String, Object> map = mTrasfusioni.get(i).getData();
                                    Calendar calendar = Calendar.getInstance();
                                    Timestamp data = (Timestamp) map.get(KEY_TRASFUSIONE_DATA);
                                    calendar.setTime( data.toDate());
                                    //Log.d("MAP", "TRASFUSIONE");
                                    if(!giorni.containsKey(DateToString(calendar.getTime()))){
                                        giorni.put(DateToString(calendar.getTime()), new Util.Evento(true, false, false));
                                    }
                                    else{
                                        Util.Evento e = (Util.Evento) giorni.get(DateToString(calendar.getTime()));
                                        e.setTrasfusione(true);
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

    public void getTrasfusioniGiorno(Calendar giorno, RecyclerView recyclerView){
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
            trasfusioniRef.whereGreaterThanOrEqualTo(KEY_TRASFUSIONE_DATA, inizio.getTime())
                    .whereLessThanOrEqualTo(KEY_TRASFUSIONE_DATA, fine.getTime())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                            TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(context);
                            recyclerView.setAdapter(trasfusioniListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            trasfusioniListAdapter.setTrasfusioni(mTrasfusioni);
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void setTrasfusioniStatus(View root){
        TextView TXVUltima = root.findViewById(R.id.TXVUltimaTrasfusione);
        TextView TXVProssima = root.findViewById(R.id.TXVProssimaTrasfusione);
        ProgressBar progressBar = root.findViewById(R.id.progressBarTrasfusioni);
        Button btnAggiungiDati = root.findViewById(R.id.buttonAggiungiUltimaTrasfusione);
        ultima = null;
        prossima = null;
        Calendar calendar = Calendar.getInstance();
        Date oggi = calendar.getTime();
        if(isConnectedToInternet(context)){
            trasfusioniRef.whereLessThan(KEY_TRASFUSIONE_DATA, oggi)
                    .orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            //Se ho il precedente
                            if(value.getDocuments().size() > 0){
                                Timestamp tmp = (Timestamp) value.getDocuments().get(0).get(KEY_TRASFUSIONE_DATA);
                                ultima =  tmp.toDate();
                                Log.i("Ultima", String.valueOf(value.getDocuments().get(0).get(KEY_TRASFUSIONE_DATA)));
                                TXVUltima.setText("Ultima trasfusione: \n"+ DateToString(ultima));


                                //Bottone aggiungi dati prossima trasfusione
                                btnAggiungiDati.setVisibility(View.VISIBLE);
                                btnAggiungiDati.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String id = value.getDocuments().get(0).getId();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("TrasfusioneID", id);
                                        Navigation.findNavController(v).navigate(R.id.action_global_modificaTrasfusioneFragment, bundle);
                                    }
                                });
                            }
                            else{
                                btnAggiungiDati.setVisibility(View.GONE);
                                TXVUltima.setText("Ultima trasfusione: \n NaN");
                            }

                            Log.i("Oggi", String.valueOf(DateToLong(oggi)));

                            trasfusioniRef.whereGreaterThan(KEY_TRASFUSIONE_DATA, oggi)
                                    .limit(1)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if(value.getDocuments().size() > 0){
                                                Timestamp tmp = (Timestamp) value.getDocuments().get(0).get(KEY_TRASFUSIONE_DATA);
                                                prossima = tmp.toDate();
                                                //Log.i("Prossima", String.valueOf((Long) value.getDocuments().get(0).get(KEY_TRASFUSIONE_DATA)));
                                                TXVProssima.setText("Prossima trasfusione: \n"+ DateToString(prossima));

                                                if(ultima != null && prossima != null){
                                                    long diffInMillies = Math.abs(prossima.getTime() - ultima.getTime());
                                                    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                                    progressBar.setMax((int) diff);
                                                    diffInMillies = Math.abs(oggi.getTime() - ultima.getTime());
                                                    long pro  = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                                    progressBar.setProgress((int) pro);
                                                }
                                                if(prossima != null){
                                                    long diffInMillies = Math.abs(prossima.getTime() - oggi.getTime());
                                                    long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                                    TextView TXVlabel = root.findViewById(R.id.ETProgressBar);
                                                    if(diff <= 24){
                                                        TXVlabel.setText("Tra "+diff + " ore hai il prossimo appuntamento");
                                                    }
                                                    else {
                                                        diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                                        TXVlabel.setText("Mancano " + diff + " giorni alla prossima trasfusione");
                                                    }
                                                }
                                            }
                                            else{
                                                TXVProssima.setText("Prossima trasfusione: \n NaN");
                                            }
                                        }
                                    });
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }

    public void setTrasfusioniLinechart(View root) {
        LineChart chart = root.findViewById(R.id.LineChart);
        ArrayList<Entry> dataVals = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();
        if(isConnectedToInternet(context)){
            trasfusioniRef.orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                            for(int i = 0; i < mTrasfusioni.size(); i++) {
                                if (mTrasfusioni.get(i).contains(KEY_TRASFUSIONE_HB)) {
                                    Double hb = (Double) mTrasfusioni.get(i).get(KEY_TRASFUSIONE_HB);
                                    dataVals.add(new Entry(i,hb.floatValue()));
                                }
                                Timestamp tmp= (Timestamp) mTrasfusioni.get(i).get(KEY_TRASFUSIONE_DATA);
                                xAxisLabel.add( tmp.toDate().getDate()+ "/"+ tmp.toDate().getMonth());
                            }
                            LineDataSet lineDataSet = new LineDataSet(dataVals, "Hb");
                            lineDataSet.setValueTextSize(12);
                            lineDataSet.setCircleColor(Color.rgb(197, 123, 87));
                            lineDataSet.setColor(Color.rgb(197, 123, 87));
                            lineDataSet.setValueTextColor(Color.BLACK);
                            LineData lineData = new LineData(lineDataSet);
                            if(dataVals.size() > 0) {
                                XAxis xAxis = chart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setDrawGridLines(true);
                                xAxis.setGranularity(1);
                                xAxis.setGranularityEnabled(true);
                                chart.setDescription(Util.getDescription());
                                chart.setScaleMinima(0.5f, 1f);
                                chart.animateXY(1000, 1000);
                                chart.setTouchEnabled(true);
                                chart.setPinchZoom(true);
                                chart.setData(lineData);
                                chart.setScaleEnabled(true);
                                chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
                                chart.invalidate();
                            }
                            else  root.findViewById(R.id.LineChart).setVisibility(View.GONE);
                        }
                    });
        }
        else{
            Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
        }
    }
}