package com.example.talapp.Trasfusioni;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;

public class TrasfusioniViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String KEY_TRASFUSIONE = "Trasfusione";
    private final String KEY_TRASFUSIONE_UNITA = "unita";
    private final String KEY_TRASFUSIONE_DATA = "data";
    private final String KEY_TRASFUSIONE_HB = "hb";
    private final String KEY_TRASFUSIONE_NOTE = "note";
    private boolean listerner = false;


    public boolean InsertTrasfusione(String user, String unita, Calendar data, String note) {
        Map<String, Object> trasfusione = new HashMap<>();
        trasfusione.put(KEY_TRASFUSIONE_UNITA, unita);
        trasfusione.put(KEY_TRASFUSIONE_DATA, data);
        //trasfusione.put(KEY_TRASFUSIONE_HB, hb);
        trasfusione.put(KEY_TRASFUSIONE_NOTE, note);


        db.collection(user).document(KEY_TRASFUSIONE).set(trasfusione)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        listerner = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        listerner = false;
                    }
                });
        return listerner;
    }
}
