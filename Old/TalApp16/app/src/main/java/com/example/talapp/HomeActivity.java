package com.example.talapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.talapp.LauncherActivity.account;
import static com.example.talapp.LauncherActivity.currentUser;

public class HomeActivity extends AppCompatActivity {

    private NavController navController;
    public static String Utente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container2);
        NavigationUI.setupActionBarWithNavController(this, navController);

        if(currentUser != null) {
            Utente = currentUser.getEmail();
        }
        else{
            Utente = account.getEmail();
        }

    }

    public String getUtente() {
        return Utente;
    }

    public void setUtente(String utente) {
        Utente = utente;
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