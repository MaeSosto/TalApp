package com.example.talapp.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.talapp.Esami.EsamiListAdapter;
import com.example.talapp.R;
import com.example.talapp.Terapie.TerapieListAdapter;
import com.example.talapp.Trasfusioni.TrasfusioniListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_NOTE;


public class SearchFragment extends Fragment {

    private String query;
    private MutableLiveData<List<DocumentSnapshot>> trasfusioniList;
    private MutableLiveData<List<DocumentSnapshot>> esamiList;
    private MutableLiveData<List<DocumentSnapshot>> terapieList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trasfusioniList = new MutableLiveData<>();
        trasfusioniList.setValue(new ArrayList<>());
        esamiList = new MutableLiveData<>();
        esamiList.setValue(new ArrayList<>());
        terapieList = new MutableLiveData<>();
        terapieList.setValue(new ArrayList<>());
        query = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_search, container, false);
        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("QUERYSEARCH", newText);
                query = newText;
                Search(trasfusioniList, trasfusioniRef);
                Search(esamiList, esamiRef);
                Search(terapieList, terapieRef);
                return false;
            }
        });

        TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
        RecyclerView recyclerViewTrasfusioni = root.findViewById(R.id.RecyclerViewGiornoTrasfusioni);
        recyclerViewTrasfusioni.setAdapter(trasfusioniListAdapter);
        recyclerViewTrasfusioni.setLayoutManager(new LinearLayoutManager(getContext()));
        trasfusioniList.observe(getViewLifecycleOwner(), trasfusioniListAdapter::setTrasfusioni);

        EsamiListAdapter esamiListAdapter = new EsamiListAdapter(getContext());
        RecyclerView recyclerViewEsami = root.findViewById(R.id.RecyclerViewGiornoEsami);
        recyclerViewEsami.setAdapter(esamiListAdapter);
        recyclerViewEsami.setLayoutManager(new LinearLayoutManager(getContext()));
        esamiList.observe(getViewLifecycleOwner(), esamiListAdapter::setEsami);

        TerapieListAdapter terapieListAdapter = new TerapieListAdapter(getContext());
        RecyclerView recyclerViewTerapie = root.findViewById(R.id.RecyclerViewGiornoTerapie);
        recyclerViewTerapie.setAdapter(terapieListAdapter);
        recyclerViewTerapie.setLayoutManager(new LinearLayoutManager(getContext()));
        terapieList.observe(getViewLifecycleOwner(), terapieListAdapter::setTerapie);

        Search(trasfusioniList, trasfusioniRef);
        Search(esamiList, esamiRef);
        Search(terapieList, terapieRef);

        return root;
    }

    private void Search(MutableLiveData<List<DocumentSnapshot>> list, CollectionReference collectionReference) {
        list.setValue(new ArrayList<>());
        collectionReference.get().addOnCompleteListener(task -> {
            for(int i = 0; i< task.getResult().getDocuments().size(); i++){
                for (Map.Entry<String, Object> entry : task.getResult().getDocuments().get(i).getData().entrySet()) {
                    try {
                        String string = (String) entry.getValue();
                        Log.d("ENTRY", string);
                        if(string.toLowerCase().contains(query.toLowerCase())){
                            List<DocumentSnapshot> tmp = list.getValue();
                            if(!tmp.contains(task.getResult().getDocuments().get(i))) tmp.add(task.getResult().getDocuments().get(i));
                            list.setValue(tmp);
                        }
                    }catch (Exception ignored){}
                }
            }
        });
    }


}