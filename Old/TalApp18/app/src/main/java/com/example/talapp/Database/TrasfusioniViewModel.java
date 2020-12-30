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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class TrasfusioniViewModel extends AndroidViewModel {
    private static FirebaseFirestore db;
    private static Context context;
    private static final String KEY_UTENTI = "Users";
    private static final String KEY_TRASFUSIONE = "Trasfusione";
    private final String KEY_TRASFUSIONE_UNITA = "unita";
    private final String KEY_TRASFUSIONE_DATA = "data";
    private final String KEY_TRASFUSIONE_HB = "hb";
    private final String KEY_TRASFUSIONE_NOTE = "note";

    public TrasfusioniViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        context = application.getApplicationContext();
    }

   public void InsertTrasfusione(View root, String unita, Calendar data, String note) {
       Map<String, Object> trasfusione = new HashMap<>();
       trasfusione.put(KEY_TRASFUSIONE_UNITA, unita);
       trasfusione.put(KEY_TRASFUSIONE_DATA, data);
       //trasfusione.put(KEY_TRASFUSIONE_HB, hb);
       trasfusione.put(KEY_TRASFUSIONE_NOTE, note);

       if(isConnectedToInternet(context)){
           db.collection(KEY_UTENTI).document(Utente).collection(KEY_TRASFUSIONE).add(trasfusione)
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
}
