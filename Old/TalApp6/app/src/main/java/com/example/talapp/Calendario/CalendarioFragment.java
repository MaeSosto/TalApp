package com.example.talapp.Calendario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.talapp.R;

public class CalendarioFragment extends Fragment {

    private boolean FAB = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        FAB(root);
        root.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FAB(root);
            }
        });

        return root;
    }

    private void FAB(View root){
        if(FAB){
            FAB = false;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
            root.findViewById(R.id.FABEsami).setVisibility(View.GONE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.GONE);
        }
        else{
            FAB = true;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABEsami).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.VISIBLE);
        }
    }
}