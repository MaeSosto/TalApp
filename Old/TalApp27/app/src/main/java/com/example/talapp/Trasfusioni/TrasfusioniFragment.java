package com.example.talapp.Trasfusioni;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class TrasfusioniFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trasfusioni, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        trasfusioniViewModel.setTrasfusioniStatus(root);

        trasfusioniViewModel.setTrasfusioniLinechart(root);

        root.findViewById(R.id.buttonCronologiatrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_trasfusioniFragment_to_cronologiaTrasfusioneFragment);
            }
        });

        return root;
    }
}