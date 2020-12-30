package com.example.talapp.Database;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.Navigation;

import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.db;
import static com.example.talapp.Utils.Util.KEY_UTENTI;
import static com.example.talapp.Utils.Util.Utente;

import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class TrasfusioniViewModel extends AndroidViewModel {
    private static Context context;

    public TrasfusioniViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

   public void InsertTrasfusione(View root, String unita, Calendar data, String note) {

       if(isConnectedToInternet(context)){
           Map<String, Object> trasfusione = new HashMap<>();
            trasfusione.put(KEY_TRASFUSIONE_DATA, Util.DateToLong(data.getTime()));
            trasfusione.put(KEY_TRASFUSIONE_UNITA, unita);
            if(!note.isEmpty()) {
                trasfusione.put(KEY_TRASFUSIONE_NOTE, note);
            }

           trasfusioniRef.add(trasfusione)
                   .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                       @Override
                       public void onSuccess(DocumentReference documentReference) {
                           Log.i("Trasfusione", "Aggiunta");
                           Toast.makeText(context, "Trasfusione aggiunta", Toast.LENGTH_LONG).show();
                           Navigation.findNavController(root).popBackStack();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Log.i("Trasfusione", "Errore");
                           Toast.makeText(context, "Errore di aggiunta", Toast.LENGTH_LONG).show();
                       }
                   });
       }
       else{
           Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show();
       }
    }

    public void updateTrasfusione(View root, String unita, Calendar data, String note){


        Map<String, Object> trasfusione = new HashMap<>();
        trasfusione.put(KEY_TRASFUSIONE_DATA, Util.DateToLong(data.getTime()));
        trasfusione.put(KEY_TRASFUSIONE_UNITA, unita);
        if(!note.isEmpty()) {
            trasfusione.put(KEY_TRASFUSIONE_NOTE, note);
        }
    }


}
