package com.example.talapp.Database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.Utente;

public class TrasfusioniViewModel extends AndroidViewModel {
    private static FirebaseFirestore db;
    private static final String KEY_TRASFUSIONE = "Trasfusione";
    private final String KEY_TRASFUSIONE_UNITA = "unita";
    private final String KEY_TRASFUSIONE_DATA = "data";
    private final String KEY_TRASFUSIONE_HB = "hb";
    private final String KEY_TRASFUSIONE_NOTE = "note";

    public TrasfusioniViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
    }


   //public boolean InsertTrasfusione(String user, String unita, Calendar data, String note) {
   //    Map<String, Object> trasfusione = new HashMap<>();
   //    trasfusione.put(KEY_TRASFUSIONE_UNITA, unita);
   //    trasfusione.put(KEY_TRASFUSIONE_DATA, data);
   //    //trasfusione.put(KEY_TRASFUSIONE_HB, hb);
   //    trasfusione.put(KEY_TRASFUSIONE_NOTE, note);

   //    db.collection(user).document(KEY_TRASFUSIONE).collection(KEY_TRASFUSIONE).add(trasfusione)
   //            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
   //                @Override
   //                public void onSuccess(DocumentReference documentReference) {
   //                    Log.d(TAG, "DocumentSnapshot successfully written!");
   //                    listerner = true;
   //                }
   //            })
   //            .addOnFailureListener(new OnFailureListener() {
   //                @Override
   //                public void onFailure(@NonNull Exception e) {
   //                    Log.w(TAG, "Error writing document", e);
   //                    listerner = false;
   //                }
   //            });
   //    return listerner;
   //}

    //OPERAZIONE DI INSERIMENTO
    private static class InsertAsyncTask extends AsyncTask<Trasfusione, Void, Boolean> {
        boolean listener = false;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }

        @Override
        protected Boolean doInBackground(Trasfusione... trasfusiones) {
            Trasfusione trasfusione = trasfusiones[0];

            db.collection(Utente).document(KEY_TRASFUSIONE).collection(KEY_TRASFUSIONE).add(trasfusione)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            listener = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            listener = false;
                        }
                    });

            return listener;
        }
    }
}
