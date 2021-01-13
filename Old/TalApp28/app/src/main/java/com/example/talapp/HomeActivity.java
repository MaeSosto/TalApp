package com.example.talapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.google.firebase.firestore.CollectionReference;

import static com.example.talapp.Utils.Util.*;

public class HomeActivity extends AppCompatActivity {

    public static CollectionReference trasfusioniRef = null;
    public static CollectionReference esamiRef = null;
    public static CollectionReference alarmRef = null;
    public static ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        trasfusioniRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_TRASFUSIONE);
        esamiRef = db.collection(KEY_UTENTI).document(Utente).collection(KEY_ESAME);
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