package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.esamiViewModel;
import static com.example.talapp.Esami.EsamiFragment.getTipoEsami;


public class CronologiaEsamiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cronologia_esami, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_cronologia_esami);
        esamiViewModel.getAllEsami(recyclerView, getTipoEsami());
        return root;
    }
}