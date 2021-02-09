package com.example.talapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static com.example.talapp.Utils.Util.FFS;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.navController;

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