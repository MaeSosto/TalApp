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
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_DATA;


public class GiornoFragment extends Fragment {

    EsamiListAdapter esamiListAdapter;
    TrasfusioniListAdapter trasfusioniListAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
        esamiListAdapter = new EsamiListAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_giorno, container, false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        Long Lgiorno = getArguments().getLong("LongGiorno");
        Calendar giorno = Calendar.getInstance();
        giorno.setTime(Util.LongToDate(Lgiorno));

        Calendar fine= Calendar.getInstance();
        fine.setTime(giorno.getTime());
        fine.set(Calendar.HOUR_OF_DAY, 24);
        fine.set(Calendar.MINUTE, 59);
        fine.set(Calendar.SECOND, 59);
        fine.set(Calendar.MILLISECOND, 59);


        RecyclerView recyclerViewTrasfusioni = root.findViewById(R.id.RecyclerViewGiornoTrasfusioni);
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_DATA, Util.getPrimoMinuto(giorno).getTime())
                .whereLessThanOrEqualTo(KEY_DATA, fine.getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> mTrasfusioni = value.getDocuments();
                        recyclerViewTrasfusioni.setAdapter(trasfusioniListAdapter);
                        recyclerViewTrasfusioni.setLayoutManager(new LinearLayoutManager(getContext()));
                        trasfusioniListAdapter.setTrasfusioni(mTrasfusioni);
                    }
                });


        RecyclerView recyclerViewEsami = root.findViewById(R.id.RecyclerViewGiornoEsami);
        esamiRef.whereGreaterThanOrEqualTo(Util.KEY_DATA, Util.getPrimoMinuto(giorno).getTime())
                .whereLessThanOrEqualTo(Util.KEY_DATA, fine.getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> documents = value.getDocuments();
                        recyclerViewEsami.setAdapter(esamiListAdapter);
                        recyclerViewEsami.setLayoutManager(new LinearLayoutManager(getContext()));
                        esamiListAdapter.setEsami(documents);
                    }
                });

        return root;
    }
}