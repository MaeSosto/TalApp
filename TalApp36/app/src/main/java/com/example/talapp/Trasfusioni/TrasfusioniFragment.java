package com.example.talapp.Trasfusioni;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_HB;


public class TrasfusioniFragment extends Fragment {
    private Date ultima, prossima;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ultima = null;
        prossima = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trasfusioni, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));





        //Prendo la scorsa trasfusione
        trasfusioniRef.whereLessThan(KEY_DATA, Calendar.getInstance().getTime()).orderBy(KEY_DATA, Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.getDocuments().size() > 0){
                        ultima = Util.TimestampToDate((Timestamp) value.getDocuments().get(0).get(KEY_DATA));
                        TextView TXVUltima = root.findViewById(R.id.TXVUltimaTrasfusione);
                        TXVUltima.setVisibility(View.VISIBLE);
                        TXVUltima.setText("Ultima trasfusione: "+ Util.DateToString(ultima));

                        //Bottone aggiungi dati prossima trasfusione
                        Button btnAggiungiDati = root.findViewById(R.id.buttonAggiungiUltimaTrasfusione);
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

                    //Prendo la prossima trasfusione
                    trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value.getDocuments().size() > 0){
                                prossima = Util.TimestampToDate((Timestamp) value.getDocuments().get(0).get(KEY_DATA));
                                TextView TXVProssima = root.findViewById(R.id.TXVProssimaTrasfusione);
                                TXVProssima.setVisibility(View.VISIBLE);
                                TXVProssima.setText("Prossima trasfusione: "+ Util.DateToString(prossima));
                            }
                            //Nessuna delle due
                            if(ultima == null && prossima == null){
                                TextView TXVUltima = root.findViewById(R.id.TXVUltimaTrasfusione);
                                TXVUltima.setVisibility(View.VISIBLE);
                                TXVUltima.setText("Non ci sono trasfusioni");
                            }
                            //Ho entrambe
                            if(ultima != null && prossima != null){
                                ProgressBar progressBar = root.findViewById(R.id.progressBarTrasfusioni);
                                progressBar.setVisibility(View.VISIBLE);
                                long diffInMillies = Math.abs(prossima.getTime() - ultima.getTime());
                                long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                progressBar.setMax((int) diff);
                                diffInMillies = Math.abs(Calendar.getInstance().getTime().getTime() - ultima.getTime());
                                long pro  = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                progressBar.setProgress((int) pro);
                                TextView TXVlabel = root.findViewById(R.id.ETProgressBar);
                                TXVlabel.setVisibility(View.VISIBLE);
                                if(diff <= 24){
                                    TXVlabel.setText("Tra "+diff + " ore hai il prossimo appuntamento");
                                }
                                else {
                                    diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                    TXVlabel.setText("Mancano " + diff + " giorni alla prossima trasfusione");
                                }
                            }
                            //Ho solo la prossima
                            if(ultima == null && prossima != null){
                                long diffInMillies = Math.abs(prossima.getTime() - Calendar.getInstance().getTime().getTime());
                                long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                TextView TXVlabel = root.findViewById(R.id.ETProgressBar);
                                TXVlabel.setVisibility(View.VISIBLE);
                                if(diff <= 24){
                                    TXVlabel.setText("Tra "+diff + " ore hai il prossimo appuntamento");
                                }
                                else {
                                    diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                                    TXVlabel.setText("Mancano " + diff + " giorni alla prossima trasfusione");
                                }
                            }
                        }
                    });
                }
        });

        ArrayList<Entry> dataVals = new ArrayList<>();
        ArrayList<String> xAxisLabel = new ArrayList<>();

        trasfusioniRef.orderBy(KEY_DATA, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                        for(int i = 0; i < mTrasfusioni.size(); i++) {
                            if (mTrasfusioni.get(i).contains(KEY_HB)) {
                                Double hb = (Double) mTrasfusioni.get(i).get(KEY_HB);
                                dataVals.add(new Entry(i,hb.floatValue()));
                            }
                            xAxisLabel.add( Util.TimestampToDate((Timestamp) mTrasfusioni.get(i).get(KEY_DATA)).getDate()+ "/"+ Util.TimestampToDate((Timestamp) mTrasfusioni.get(i).get(KEY_DATA)).getMonth());
                        }
                        LineDataSet lineDataSet = new LineDataSet(dataVals, "Hb");
                        lineDataSet.setValueTextSize(12);
                        lineDataSet.setCircleColor(Color.rgb(197, 123, 87));
                        lineDataSet.setColor(Color.rgb(197, 123, 87));
                        lineDataSet.setValueTextColor(Color.BLACK);
                        if(dataVals.size() > 0) {
                            root.findViewById(R.id.CardGraficoTrasfusioni).setVisibility(View.VISIBLE);
                            setGraph(root, new LineData(lineDataSet), xAxisLabel);
                        }
                        else  root.findViewById(R.id.CardGraficoTrasfusioni).setVisibility(View.GONE);
                    }
                });

        //Bottone cronologia trasfusioni
        root.findViewById(R.id.buttonCronologiatrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_cronologiaTrasfusioneFragment);
            }
        });

        return root;
    }

    private void setGraph(View root, LineData lineData, ArrayList<String> xAxisLabel){
        LineChart chart = root.findViewById(R.id.LineChart);
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
}