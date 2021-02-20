package com.example.talapp.Home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.Esami.EsamiListAdapter;
import com.example.talapp.R;
import com.example.talapp.Terapie.TerapieListAdapter;
import com.example.talapp.Trasfusioni.TrasfusioniListAdapter;
import com.example.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.getUltimoMinuto;


public class GiornoFragment extends Fragment {

    EsamiListAdapter esamiListAdapter;
    TrasfusioniListAdapter trasfusioniListAdapter;
    TerapieListAdapter terapieListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
        esamiListAdapter = new EsamiListAdapter(getContext());
        terapieListAdapter = new TerapieListAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_giorno, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        Long Lgiorno = getArguments().getLong("LongGiorno");
        Calendar giorno = Calendar.getInstance();
        giorno.setTime(Util.LongToDate(Lgiorno));

        RecyclerView recyclerViewTrasfusioni = root.findViewById(R.id.RecyclerViewGiornoTrasfusioni);
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_DATA, Util.getPrimoMinuto(giorno).getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.getUltimoMinuto(giorno).getTime()).addSnapshotListener((value, error) -> {
            recyclerViewTrasfusioni.setAdapter(trasfusioniListAdapter);
            recyclerViewTrasfusioni.setLayoutManager(new LinearLayoutManager(getContext()));
            trasfusioniListAdapter.setTrasfusioni(value.getDocuments());
        });

        RecyclerView recyclerViewEsami = root.findViewById(R.id.RecyclerViewGiornoEsami);
        esamiRef.whereGreaterThanOrEqualTo(Util.KEY_DATA, Util.getPrimoMinuto(giorno).getTime()).whereLessThanOrEqualTo(Util.KEY_DATA, Util.getUltimoMinuto(giorno).getTime()).addSnapshotListener((value, error) -> {
            recyclerViewEsami.setAdapter(esamiListAdapter);
            recyclerViewEsami.setLayoutManager(new LinearLayoutManager(getContext()));
            esamiListAdapter.setEsami(value.getDocuments());
        });

        RecyclerView RecyclerViewGiornoTerapie = root.findViewById(R.id.RecyclerViewGiornoTerapie);
        terapieRef.addSnapshotListener((value, error) -> {
            RecyclerViewGiornoTerapie.setAdapter(terapieListAdapter);
            RecyclerViewGiornoTerapie.setLayoutManager(new LinearLayoutManager(getContext()));
            terapieListAdapter.setTerapie(getTerapie(value, giorno));
        });

        return root;
    }

    public static List<DocumentSnapshot> getTerapie(QuerySnapshot value, Calendar giorno){
        List<DocumentSnapshot> tmp = new ArrayList<>();

        for (int i = 0; i < value.getDocuments().size(); i++) {
            Calendar inizioTerapia = Calendar.getInstance();
            inizioTerapia.setTime(Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA)));
            Calendar fineTerapia = Calendar.getInstance();
            fineTerapia.setTime(Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA_FINE)));
            Calendar x = inizioTerapia;
            while(inizioTerapia.compareTo(fineTerapia) <= 0) {
                //Log.d("FINE TERAPIA", documents.get(i).get(KEY_NOME)+ " giorno "+ x.getTime() + " / "+ Util.TimestampToDate((Timestamp) documents.get(i).get(KEY_DATA_FINE)));
                if((x.compareTo(Util.getPrimoMinuto(giorno)) >= 0) && (x.compareTo(getUltimoMinuto(giorno)) <= 0) && !tmp.contains(value.getDocuments().get(i)))
                    tmp.add(value.getDocuments().get(i));
                x.add(Calendar.DATE, 1);
            }
        }
        return tmp;
    }
}