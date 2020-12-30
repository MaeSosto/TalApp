package com.example.talapp.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.talapp.Database.TrasfusioniViewModel;
import com.example.talapp.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class CalendarioFragment extends Fragment {

    private boolean FAB = false;
    private final String KEY_TRASFUSIONE_UNITA = "unita";
    private final String KEY_TRASFUSIONE_DATA = "data";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        trasfusioniViewModel.getTrasfusioni();
        //Task<QuerySnapshot> task = trasfusioniViewModel.getTrasfusioni();
        //for (QueryDocumentSnapshot document : task.getResult()) {
        //    Log.d(TAG, "Data: " + document.get(KEY_TRASFUSIONE_DATA) + " Unita " + document.get(KEY_TRASFUSIONE_UNITA));
        //}


        openCloseFAB(root);
        root.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCloseFAB(root);
            }
        });

        root.findViewById(R.id.FABTrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTrasfusioneFragment);
            }
        });

        root.findViewById(R.id.FABEsami).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiEsamiFragment);
            }
        });

        root.findViewById(R.id.FABTerapie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTerapieFragment);
            }
        });

        return  root;
    }

    private void openCloseFAB(View root){
        if(FAB){
            FAB = false;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABEsami).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.VISIBLE);
        }
        else{
            FAB = true;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
            root.findViewById(R.id.FABEsami).setVisibility(View.GONE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.GONE);
        }
    }
}