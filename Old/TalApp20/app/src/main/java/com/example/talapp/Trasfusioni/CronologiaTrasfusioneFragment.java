package com.example.talapp.Trasfusioni;

import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;

public class CronologiaTrasfusioneFragment extends Fragment {
    public final static CollectionReference trasfusioniRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_TRASFUSIONE);
    private QuerySnapshot mTrasfusioni;
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_cronologia_trasfusioni, container, false);

        new getTrasfusioni().execute();

        return root;
    }

    private class getTrasfusioni extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            trasfusioniRef.orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.i("Trasfusioni", "Data: " + document.get(KEY_TRASFUSIONE_DATA) + " Unita " + document.get(KEY_TRASFUSIONE_UNITA));
                        }
                        mTrasfusioni = task.getResult();
                        Log.i("Trasfusioni", String.valueOf(mTrasfusioni));
                        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_cronologia_trasfusioni);
                        TrasfusioniListAdapter trasfusioniListAdapter = new TrasfusioniListAdapter(getContext());
                        recyclerView.setAdapter(trasfusioniListAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        Log.i("Trasfusioni", "Prendo");
                        Log.i("Trasfusioni", String.valueOf(mTrasfusioni.getDocuments()));
                        trasfusioniListAdapter.setTrasfusioni(mTrasfusioni.getDocuments());

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
            return null;
        }
    }
}