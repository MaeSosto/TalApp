package com.example.talapp.Esami;

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

import com.example.talapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.R.string.esami_label1;
import static com.example.talapp.R.string.esami_label2;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.ESAMI_STRUMENTALI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_TIPO;

public class EsamiFragment extends Fragment {
    private static String tipoEsami;
    private EsamiListAdapter esamiListAdapter;
    private View root;

    public static String getTipoEsami() {
        return tipoEsami;
    }
    public static void setTipoEsami(String tipoEsami) {
        EsamiFragment.tipoEsami = tipoEsami;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_esami, container, false);
        this.root = root;

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));
        esamiListAdapter = new EsamiListAdapter(getContext());
        tipoEsami = ESAMI_LABORATORIO;

        //Prende l'ultimo esame
        getUltimoEsame();

        //prende il prossimo esame
        getProssimiEsami();

        //Bottone per aggiungere un esame
        root.findViewById(R.id.buttonAggiungiEsame).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_aggiungiEsamiFragment));

        //GESTIONE DELLE TAB
        setTab();

        return root;
    }

    private void setTab() {
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
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
    }

    public void getUltimoEsame(){
        Button buttonCronologiaEsami = root.findViewById(R.id.buttonCronologiaEsami);
        Button btnAggiungiUltimoEsame = root.findViewById(R.id.buttonAggiungiUltimoEsame);
        esamiRef.whereLessThan(KEY_DATA, Calendar.getInstance().getTime()).whereEqualTo(KEY_TIPO, tipoEsami).orderBy(KEY_DATA, Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
            if(Objects.requireNonNull(task.getResult()).getDocuments().size()> 0){
                //BOTTONE DELLA CRONOLOGIA
                buttonCronologiaEsami.setVisibility(View.VISIBLE);
                buttonCronologiaEsami.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_cronologiaEsamiFragment));

                //BOTTONE AGGIUNGI ULTIMO ESAME
                btnAggiungiUltimoEsame.setVisibility(View.VISIBLE);
                btnAggiungiUltimoEsame.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", task.getResult().getDocuments().get(0).getId());
                    Navigation.findNavController(v).navigate(R.id.action_global_modificaEsamiFragment, bundle);
                });
            }
            else{
                btnAggiungiUltimoEsame.setVisibility(View.GONE);
                buttonCronologiaEsami.setVisibility(View.GONE);
            }
        });
    }

    //Prende i prossimi esami dal database
    private void getProssimiEsami(){
        RecyclerView recyclerView = root.findViewById(R.id.RecyclerViewProssimiEsami);
        TextView TXVProssimiEsami = root.findViewById(R.id.TXVProssimiEsami);
        esamiRef.whereGreaterThan(KEY_DATA, Calendar.getInstance().getTime()).whereEqualTo(KEY_TIPO, tipoEsami).orderBy(KEY_DATA, Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if(Objects.requireNonNull(task.getResult()).getDocuments().size() > 0){
                QuerySnapshot mEsami = task.getResult();
                TXVProssimiEsami.setVisibility(View.VISIBLE);
                TXVProssimiEsami.setText(esami_label2);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(esamiListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                esamiListAdapter.setEsami(mEsami.getDocuments());
            }
            else {
                TXVProssimiEsami.setVisibility(View.VISIBLE);
                TXVProssimiEsami.setText(esami_label1);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}