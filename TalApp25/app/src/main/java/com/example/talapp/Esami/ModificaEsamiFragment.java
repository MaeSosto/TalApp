package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.talapp.R;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.esamiViewModel;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class ModificaEsamiFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                esamiViewModel.setEsame_old(null);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_modifica_esami, container, false);
        String id = getArguments().getString("ID");

        esamiViewModel.setEsame(id, root);

        root.findViewById(R.id.buttonModifica).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esamiViewModel.updateEsame(root, id);
            }
        });

        root.findViewById(R.id.buttonElimina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esamiViewModel.deleteEsame(root, id);
            }
        });

        return root;
    }
}