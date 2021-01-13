package com.example.talapp.Trasfusioni;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToString;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;


public class TrasfusioniFragment extends Fragment {
    private Date ultima, prossima;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trasfusioni, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        TextView TXVUltima = root.findViewById(R.id.TXVUltimaTrasfusione);
        TextView TXVProssima = root.findViewById(R.id.TXVProssimaTrasfusione);
        ProgressBar progressBar = root.findViewById(R.id.progressBarTrasfusioni);
        Button btnAggiungiDati = root.findViewById(R.id.buttonAggiungiUltimaTrasfusione);
        ultima = null;
        prossima = null;
        Calendar calendar = Calendar.getInstance();
        Date oggi = calendar.getTime();

        //Prendo l'ultima trasfusione
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

                        //Log.i("Oggi", String.valueOf(DateToLong(oggi)));
                        //Prendo la prossima trasfusione
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

        LineChart chart = root.findViewById(R.id.LineChart);
        ArrayList<Entry> dataVals = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();

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

        root.findViewById(R.id.buttonCronologiatrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_cronologiaTrasfusioneFragment);
            }
        });

        return root;
    }
}