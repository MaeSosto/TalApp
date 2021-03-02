package com.MartinaSosto.talapp.Home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.MartinaSosto.talapp.R;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Tumbleweed)));


        root.findViewById(R.id.CVCalendario).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_calendarioFragment));

        root.findViewById(R.id.CVTrasfusioni).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_trasfusioniFragment));

        root.findViewById(R.id.CVEsami).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_esamiFragment));

        root.findViewById(R.id.CVTerapie).setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_terapieFragment));

        //root.findViewById(R.id.buttonDB).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        saveTrasfusione(root);
        //    }
        //});
        return root;
    }

    //public void saveTrasfusione(View v){
    //    Map<String, Object> trasfusione = new HashMap<>();
    //    trasfusione.put(KEY_UNITA, "2");
    //    trasfusione.put(KEY_DATA, Calendar.getInstance());
    //    trasfusione.put(KEY_HB, 12.3);
//
    //
//
    //    //db.collection("DB_TalApp").document("Trasfusione").set
    //    //db.collection("DB_TalApp").add(trasfusione);
    //}
}