package com.example.talapp.Trasfusioni;

import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;

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
        trasfusioniViewModel.getAllTrasfusioni(recyclerView);

        return root;
    }
}