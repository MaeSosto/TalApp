package com.example.talapp.Esami;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.talapp.R;
import com.google.android.material.tabs.TabLayout;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.ESAMI_STRUMENTALI;
import static com.example.talapp.Utils.Util.esamiViewModel;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class EsamiFragment extends Fragment {
    private static String TipoEsami = ESAMI_LABORATORIO;

    public static String getTipoEsami() {
        return TipoEsami;
    }

    public void setTipoEsami(String tipoEsami) {
        TipoEsami = tipoEsami;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_esami, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1) TipoEsami = ESAMI_STRUMENTALI;
                else TipoEsami = ESAMI_LABORATORIO;
                Log.d("TAB", TipoEsami);
                esamiViewModel.setEsamiStatus(root, TipoEsami);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                TipoEsami = ESAMI_LABORATORIO;
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        Log.d("TAB", TipoEsami);
        esamiViewModel.setEsamiStatus(root, TipoEsami);

        root.findViewById(R.id.buttonCronologiaEsami).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_esamiFragment_to_cronologiaEsamiFragment);
            }
        });


        return root;
    }
}