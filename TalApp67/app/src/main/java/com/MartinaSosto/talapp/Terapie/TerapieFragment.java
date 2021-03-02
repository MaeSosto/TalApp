package com.MartinaSosto.talapp.Terapie;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinaSosto.talapp.R;
import com.MartinaSosto.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.MartinaSosto.talapp.HomeActivity.actionBar;
import static com.MartinaSosto.talapp.Notification.ForegroundService.terapieRef;
import static com.MartinaSosto.talapp.Utils.Util.KEY_DATA_FINE;
import static com.MartinaSosto.talapp.Utils.Util.getPrimoMinuto;

public class TerapieFragment extends Fragment {

    private RecyclerView RecyclerViewTerapieAttuali;
    private TextView TXVTerapieAttuali;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_terapie, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));

        //Bottone per aggiungere una terapia
        Button buttonAggiungiTerapia = root.findViewById(R.id.buttonAggiungiTerapia);
        buttonAggiungiTerapia.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_terapieFragment_to_aggiungiTerapieFragment));

        //Lista delle terapie attuali
        TXVTerapieAttuali = root.findViewById(R.id.TXVTerapieAttuali);
        RecyclerViewTerapieAttuali = root.findViewById(R.id.RecyclerViewTerapieAttuali);
        getTerapieAttuali();

        //Bottone della cronologia delle terapie
        Button buttonCronologiaTerapie = root.findViewById(R.id.buttonCronologiaTerapie);
        getTerapiePrecedenti(buttonCronologiaTerapie);
        buttonCronologiaTerapie.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_terapieFragment_to_cronologiaTerapieFragment));

        return root;
    }


    private void getTerapieAttuali(){
        terapieRef.get().addOnCompleteListener(task -> {
            List<DocumentSnapshot> tmp = new ArrayList<>();
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                Calendar inizioTerapia = Calendar.getInstance();
                inizioTerapia.setTime(Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(Util.KEY_DATA)));
                Calendar fineTerapia = Calendar.getInstance();
                fineTerapia.setTime(Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(Util.KEY_DATA_FINE)));
                Calendar giorno = inizioTerapia;
                while(inizioTerapia.compareTo(fineTerapia) <= 0) {
                    //Log.d("FINE TERAPIA", documents.get(i).get(KEY_NOME)+ " giorno "+ x.getTime() + " / "+ Util.TimestampToDate((Timestamp) documents.get(i).get(KEY_DATA_FINE)));
                    if((getPrimoMinuto(giorno).compareTo(getPrimoMinuto(Calendar.getInstance())) == 0) && !tmp.contains(task.getResult().getDocuments().get(i)))
                        tmp.add(task.getResult().getDocuments().get(i));
                    giorno.add(Calendar.DATE, 1);
                }
            }
            if(tmp.size() > 0){
                TXVTerapieAttuali.setText("Terapie attuali");
                TerapieListAdapter terapieListAdapter = new TerapieListAdapter(getContext());
                RecyclerViewTerapieAttuali.setVisibility(View.VISIBLE);
                RecyclerViewTerapieAttuali.setAdapter(terapieListAdapter);
                RecyclerViewTerapieAttuali.setLayoutManager(new LinearLayoutManager(getContext()));
                terapieListAdapter.setTerapie(tmp);
            }
            else{
                TXVTerapieAttuali.setText("Non ci sono terapie attuali");
                RecyclerViewTerapieAttuali.setVisibility(View.GONE);
            }
        });
    }

    private void getTerapiePrecedenti(Button buttonCronologiaTerapie){
        terapieRef.whereLessThanOrEqualTo(KEY_DATA_FINE, getPrimoMinuto(Calendar.getInstance()).getTime()).get().addOnCompleteListener(task -> {
            if(task.getResult().getDocuments().size() > 0) buttonCronologiaTerapie.setVisibility(View.VISIBLE);
            else buttonCronologiaTerapie.setVisibility(View.GONE);
        });
    }
}