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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_HB;


public class TrasfusioniFragment extends Fragment {

    private Date ultima, prossima;
    private View root;
    private TrasfusioniListAdapter trasfusioniListAdapter;

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
        this.root = root;

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());

        //Bottone per aggiungere una trasfusione
        root.findViewById(R.id.buttonAggiungiTrasfusione).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_aggiungiTrasfusioneFragment));

        //Prendo la scorsa trasfusione
        getLastTrasfusione();

        //Disegna il grafo dei valori Hb
        disegnaGrafico();

        return root;
    }

    private void getLastTrasfusione() {
        trasfusioniRef.whereLessThan(KEY_DATA, Calendar.getInstance().getTime()).orderBy(KEY_DATA, Query.Direction.DESCENDING).limit(1).addSnapshotListener((value, error) -> {
            if (Objects.requireNonNull(value).getDocuments().size() > 0) {
                ultima = Util.TimestampToDate((Timestamp) Objects.requireNonNull(value.getDocuments().get(0).get(KEY_DATA)));
                TextView TXVUltima = root.findViewById(R.id.TXVUltimaTrasfusione);
                TXVUltima.setVisibility(View.VISIBLE);
                TXVUltima.setText("L'ultima trasfusione è stata il " + Util.DateToString(ultima));

                //Bottone aggiungi dati prossima trasfusione
                Button btnAggiungiDati = root.findViewById(R.id.buttonAggiungiUltimaTrasfusione);
                btnAggiungiDati.setVisibility(View.VISIBLE);
                btnAggiungiDati.setOnClickListener(v -> {
                    String id = value.getDocuments().get(0).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", id);
                    Navigation.findNavController(v).navigate(R.id.action_global_modificaTrasfusioneFragment, bundle);
                });

                //Bottone cronologia trasfusioni
                root.findViewById(R.id.buttonCronologiatrasfusioni).setVisibility(View.VISIBLE);
                root.findViewById(R.id.buttonCronologiatrasfusioni).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_cronologiaTrasfusioneFragment));
            }

            //Prendo la prossima trasfusione
            getNextTrasfusione();

        });
    }

    private void getNextTrasfusione() {
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).limit(1).get().addOnCompleteListener(task -> {
            if (Objects.requireNonNull(task.getResult()).getDocuments().size() > 0) {
                prossima = Util.TimestampToDate((Timestamp) Objects.requireNonNull(task.getResult().getDocuments().get(0).get(KEY_DATA)));
                TextView TXVProssima = root.findViewById(R.id.TXVProssimaTrasfusione);
                TXVProssima.setVisibility(View.VISIBLE);
                TXVProssima.setText("La prossima trasfusione è " + Util.DateToString(prossima));
            }

            //Nessuna delle due
            none();

            //Ho entrambe
            both();

            //Ho solo la prossima
            nextOnly();

            //Prendi la lista delle prossime trasfusioni
            getNextTrasfusioniList();
        });
    }

    private void getNextTrasfusioniList() {
        trasfusioniRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).addSnapshotListener((value, error) -> {
            if(Objects.requireNonNull(value).getDocuments().size() > 0){
                TextView TXVFuture = root.findViewById(R.id.TXVTrasfusioniFuture);
                TXVFuture.setVisibility(View.VISIBLE);
                TXVFuture.setText("Prossime trasfusioni");
                RecyclerView recyclerView = root.findViewById(R.id.recyclerViewTrasfusioni);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(trasfusioniListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                trasfusioniListAdapter.setTrasfusioni(value.getDocuments());
            }
        });
    }

    private void none() {
        TextView TXVUltima = root.findViewById(R.id.TXVTrasfusioniFuture);
        if (ultima == null && prossima == null) {
            TXVUltima.setVisibility(View.VISIBLE);
            TXVUltima.setText(R.string.fragment_trasfusioni_label1);
        }
        else TXVUltima.setVisibility(View.GONE);
    }

    private void both() {
        if (ultima != null && prossima != null) {
            ProgressBar progressBar = root.findViewById(R.id.progressBarTrasfusioni);
            progressBar.setVisibility(View.VISIBLE);
            long diffMillTOT = prossima.getTime() - ultima.getTime();
            long diffHourTOT = TimeUnit.HOURS.convert(diffMillTOT, TimeUnit.MILLISECONDS);
            progressBar.setMax((int) diffMillTOT);
            long diffMillOGGI = Calendar.getInstance().getTime().getTime() - ultima.getTime();
            long diffHourProssime = TimeUnit.HOURS.convert(prossima.getTime() - Calendar.getInstance().getTime().getTime(), TimeUnit.MILLISECONDS);
            progressBar.setProgress((int) diffMillOGGI);
            TextView TXVlabel = root.findViewById(R.id.ETProgressBar);
            TXVlabel.setVisibility(View.VISIBLE);
            Log.d("ORE", String.valueOf(diffHourProssime));
            if (diffHourProssime <= 24) TXVlabel.setText("Tra " + diffHourProssime + " ore hai la prossima trasfusione");
            else {
                diffHourProssime = TimeUnit.DAYS.convert(diffHourProssime, TimeUnit.HOURS);
                TXVlabel.setText("Mancano " + diffHourProssime + " giorni alla prossima trasfusione");
            }
        }
    }

    private void nextOnly() {
        if (ultima == null && prossima != null) {
            long diffMillTOT = prossima.getTime() - Calendar.getInstance().getTime().getTime();
            long diffHourTOT = TimeUnit.HOURS.convert(diffMillTOT, TimeUnit.MILLISECONDS);
            TextView TXVlabel = root.findViewById(R.id.ETProgressBar);
            TXVlabel.setVisibility(View.VISIBLE);
            if (diffHourTOT <= 24) {
                TXVlabel.setText("Tra " + diffHourTOT + " ore hai la prossima trasfusione");
            } else {
                diffHourTOT = TimeUnit.DAYS.convert(diffMillTOT, TimeUnit.MILLISECONDS);
                TXVlabel.setText("Mancano " + diffHourTOT + " giorni alla prossima trasfusione");
            }
        }
    }

    private void disegnaGrafico() {
        trasfusioniRef.orderBy(KEY_DATA, Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            ArrayList<Entry> dataVals = new ArrayList<>();
            ArrayList<String> xAxisLabel = new ArrayList<>();
            List<DocumentSnapshot> mTrasfusioni = Objects.requireNonNull(value).getDocuments();
            for(int i = 0; i < mTrasfusioni.size(); i++) {
                if (mTrasfusioni.get(i).contains(KEY_HB)) {
                    Double hb = (Double) mTrasfusioni.get(i).get(KEY_HB);
                    dataVals.add(new Entry(i, Objects.requireNonNull(hb).floatValue()));
                }
                xAxisLabel.add( Util.TimestampToDate((Timestamp) Objects.requireNonNull(mTrasfusioni.get(i).get(KEY_DATA))).getDate()+ "/"+ Util.TimestampToDate((Timestamp) Objects.requireNonNull(mTrasfusioni.get(i).get(KEY_DATA))).getMonth());
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
        });
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