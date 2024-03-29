package com.example.talapp.Trasfusioni;

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
import com.example.talapp.R;;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;

public class CronologiaTrasfusioneFragment extends Fragment {

    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_cronologia_trasfusioni, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TerraCotta)));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_cronologia_trasfusioni);
        trasfusioniRef.orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        QuerySnapshot mTrasfusioni = value;
                        TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
                        recyclerView.setAdapter(trasfusioniListAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        trasfusioniListAdapter.setTrasfusioni(mTrasfusioni.getDocuments());
                    }
                });

        return root;
    }
}