package com.example.talapp.Terapie;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.Esami.EsamiListAdapter;
import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import static com.example.talapp.Esami.EsamiFragment.getTipoEsami;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_TIPO;
import static com.example.talapp.Utils.Util.getPrimoMinuto;

public class CronologiaTerapieFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_cronologia_terapie, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));

        RecyclerView recyclerViewCronologiaTerapie = root.findViewById(R.id.recyclerViewCronologiaTerapie);
        terapieRef.whereLessThanOrEqualTo(KEY_DATA_FINE, getPrimoMinuto(Calendar.getInstance()).getTime()).orderBy(KEY_DATA_FINE, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                TerapieListAdapter terapieListAdapter = new TerapieListAdapter(getContext());
                recyclerViewCronologiaTerapie.setAdapter(terapieListAdapter);
                recyclerViewCronologiaTerapie.setLayoutManager(new LinearLayoutManager(getContext()));
                terapieListAdapter.setTerapie(task.getResult().getDocuments());
            }
        });
        return root;
    }
}