package com.example.talapp.Trasfusioni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;


public class TrasfusioniFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trasfusioni, container, false);

        root.findViewById(R.id.buttonAggiungiUltimaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_modificaTrasfusioneFragment);
            }
        });

        root.findViewById(R.id.buttonCronologiatrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_cronologiaTrasfusioneFragment);
            }
        });

        return root;
    }
}