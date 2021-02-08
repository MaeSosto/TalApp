package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.talapp.Esami.EsamiFragment.getTipoEsami;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_TIPO;


public class CronologiaEsamiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cronologia_esami, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_cronologia_esami);
        esamiRef.whereEqualTo(KEY_TIPO, getTipoEsami())
                .orderBy(KEY_DATA, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                    QuerySnapshot mEsami = task.getResult();
                    EsamiListAdapter esamiListAdapter = new EsamiListAdapter(getContext());
                    recyclerView.setAdapter(esamiListAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    esamiListAdapter.setEsami(mEsami.getDocuments());
                });
        return root;
    }
}