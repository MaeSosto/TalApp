package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Calendar;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.R.string.esami_label1;
import static com.example.talapp.R.string.esami_label2;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.ESAMI_STRUMENTALI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_TIPO;

public class EsamiFragment extends Fragment {
    private static String tipoEsami;
    private static String idUltimoEsame;
    private static Button btnAggiungiUltimoEsame;
    private static TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TextView TXVProssimiEsami;

    public static String getTipoEsami() {
        return tipoEsami;
    }

    public static void setTipoEsami(String tipoEsami) {
        EsamiFragment.tipoEsami = tipoEsami;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tipoEsami = ESAMI_LABORATORIO;
        //SETTO GLI ESAMI
        getUltimoEsame();
        getProssimiEsami();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_esami, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        TXVProssimiEsami = root.findViewById(R.id.TXVProssimiEsami);

        recyclerView = root.findViewById(R.id.RecyclerViewProssimiEsami);
        //GESTIONE DELLE TAB
        tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1) tipoEsami = ESAMI_STRUMENTALI;
                else tipoEsami = ESAMI_LABORATORIO;
                getUltimoEsame();
                getProssimiEsami();
                //setEsamiStatus(root, TipoEsami);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //BOTTONE DELLA CRONOLOGIA
        root.findViewById(R.id.buttonCronologiaEsami).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_cronologiaEsamiFragment);
            }
        });

        //BOTTONE AGGIUNGI ULTIMO ESAME
        btnAggiungiUltimoEsame = root.findViewById(R.id.buttonAggiungiUltimoEsame);
        btnAggiungiUltimoEsame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", idUltimoEsame);
                Navigation.findNavController(v).navigate(R.id.action_global_modificaEsamiFragment, bundle);
            }
        });

        return root;
    }

    public void getUltimoEsame(){
        esamiRef.whereLessThan(KEY_DATA, Calendar.getInstance().getTime())
                .whereEqualTo(KEY_TIPO, tipoEsami)
                .orderBy(KEY_DATA, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().getDocuments().size()> 0){
                            idUltimoEsame = task.getResult().getDocuments().get(0).getId();
                            btnAggiungiUltimoEsame.setVisibility(View.VISIBLE);
                        }
                        else{
                            btnAggiungiUltimoEsame.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERR", "Ultimo esame");
                    }
                });
    }

    private void getProssimiEsami(){
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime())
                .whereEqualTo(KEY_TIPO, tipoEsami)
                .orderBy(KEY_DATA, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().getDocuments().size() > 0){
                            QuerySnapshot mEsami = task.getResult();
                            TXVProssimiEsami.setText(esami_label2);
                            EsamiListAdapter esamiListAdapter = new EsamiListAdapter(getContext());
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(esamiListAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            esamiListAdapter.setEsami(mEsami.getDocuments());
                        }
                        else {
                            TXVProssimiEsami.setText(esami_label1);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERR","Lista prossimi esami");
                    }
                });
    }
}