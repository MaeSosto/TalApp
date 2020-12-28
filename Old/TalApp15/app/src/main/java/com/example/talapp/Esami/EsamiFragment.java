package com.example.talapp.Esami;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;


public class EsamiFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_esami, container, false);

        root.findViewById(R.id.buttonAggiungiUltimoesame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_modificaEsamiFragment);
            }
        });

        root.findViewById(R.id.buttonCronologiaEsami).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_cronologiaEsamiFragment);
            }
        });


        return root;
    }
}