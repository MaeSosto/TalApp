package com.example.talapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.settingsRef;
import static com.example.talapp.Utils.Util.*;

public class HomeActivity extends AppCompatActivity {

    public static ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Rendo la cache unlimited
        FFS = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
//        db.setFirestoreSettings(FFS);

        Log.d("UTENTE", Utente);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container2);
        NavigationUI.setupActionBarWithNavController(this, navController);
        actionBar = getSupportActionBar();

        settingsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Creo le impostazioni
                if(!task.getResult().contains(IMPOSTAZIONI_TRASFUSIONI)){
                    Map<String, Object> data = new HashMap<>();
                    data.put(IMPOSTAZIONI_TRASFUSIONI, false);
                    data.put(IMPOSTAZIONI_ESAME_STRUMENTALI, false);
                    data.put(IMPOSTAZIONI_ESAMI_STRUMENTALI_PERIODICI, false);
                    data.put(IMPOSTAZIONI_ESAME_LABORATORIO, false);
                    data.put(IMPOSTAZIONI_ESAMI_LABORATORIO_PERIODICI, false);

                    db.collection(KEY_UTENTI).document(Utente).set(data);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
        //Log.i("MENU", "CLICK");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), ImpostazioniActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}