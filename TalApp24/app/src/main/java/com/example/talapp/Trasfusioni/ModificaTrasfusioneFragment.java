package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.talapp.R;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class ModificaTrasfusioneFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
               trasfusioniViewModel.setTrasfusione_old(null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_modifica_trasfusione, container, false);
        TextView TXVdata = root.findViewById(R.id.TXVDataTrasfusione);
        Spinner Sunita = root.findViewById(R.id.spinnerPeriodicitaEsame);
        EditText EThb = root.findViewById(R.id.editTextTime);
        EditText ETNote = root.findViewById(R.id.ETNoteTrasfusione);
        String id = getArguments().getString("TrasfusioneID");

        trasfusioniViewModel.setTrasfusione(id, TXVdata, Sunita, EThb, ETNote);

        root.findViewById(R.id.buttonModificaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trasfusioniViewModel.updateTrasfusione(root, id, Sunita, EThb, ETNote);
            }
        });

        root.findViewById(R.id.buttonEliminaTrasfusione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               trasfusioniViewModel.deleteTrasfusione(root, id);
            }
        });



        return root;
    }
}