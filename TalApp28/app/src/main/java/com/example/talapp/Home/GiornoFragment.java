package com.example.talapp.Home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.talapp.Esami.EsamiListAdapter;
import com.example.talapp.R;
import com.example.talapp.Trasfusioni.TrasfusioniListAdapter;
import com.example.talapp.Utils.Util;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Calendar;
import java.util.List;
import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_ESAME_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;


public class GiornoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_giorno, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        Long Lgiorno = getArguments().getLong("LongGiorno");
        Calendar giorno = Calendar.getInstance();
        giorno.setTime(Util.LongToDate(Lgiorno));
        Calendar inizio= Calendar.getInstance();
        inizio.setTime(giorno.getTime());
        inizio.set(Calendar.HOUR_OF_DAY, 0);
        inizio.set(Calendar.MINUTE, 0);
        inizio.set(Calendar.SECOND, 0);
        inizio.set(Calendar.MILLISECOND, 0);
        Calendar fine= Calendar.getInstance();
        fine.setTime(giorno.getTime());
        fine.set(Calendar.HOUR_OF_DAY, 24);
        fine.set(Calendar.MINUTE, 59);
        fine.set(Calendar.SECOND, 59);
        fine.set(Calendar.MILLISECOND, 59);


        RecyclerView recyclerViewTrasfusioni = root.findViewById(R.id.RecyclerViewGiornoTrasfusioni);
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_TRASFUSIONE_DATA, inizio.getTime())
                .whereLessThanOrEqualTo(KEY_TRASFUSIONE_DATA, fine.getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                        TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
                        recyclerViewTrasfusioni.setAdapter(trasfusioniListAdapter);
                        recyclerViewTrasfusioni.setLayoutManager(new LinearLayoutManager(getContext()));
                        trasfusioniListAdapter.setTrasfusioni(mTrasfusioni);
                    }
                });


        RecyclerView recyclerViewEsami = root.findViewById(R.id.RecyclerViewGiornoEsami);
        esamiRef.whereGreaterThanOrEqualTo(KEY_ESAME_DATA, inizio.getTime())
                .whereLessThanOrEqualTo(KEY_ESAME_DATA, fine.getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> documents = value.getDocuments();
                        EsamiListAdapter esamiListAdapter = new EsamiListAdapter(getContext());
                        recyclerViewEsami.setAdapter(esamiListAdapter);
                        recyclerViewEsami.setLayoutManager(new LinearLayoutManager(getContext()));
                        esamiListAdapter.setEsami(documents);
                    }
                });

        return root;
    }
}