package com.example.talapp.Home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;

import java.util.Calendar;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class GiornoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_giorno, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        Long Lgiorno = getArguments().getLong("LongGiorno");
        Calendar giorno = Calendar.getInstance();
        giorno.setTime(Util.LongToDate(Lgiorno));

        RecyclerView recyclerView = root.findViewById(R.id.RecyclerViewGiornoTrasfusioni);
        trasfusioniViewModel.getTrasfusioniGiorno(giorno, recyclerView);

        return root;
    }
}