package com.example.talapp.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.talapp.Calendario.CalendarioActivity;
import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.example.talapp.R;

import static com.example.talapp.Launcher.LauncherActivity.account;
import static com.example.talapp.Launcher.LauncherActivity.currentUser;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        CardView CVCalendario = findViewById(R.id.CVCalendario);
        CVCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CalendarioActivity = new Intent(getApplicationContext(), com.example.talapp.Calendario.CalendarioActivity.class);
                startActivity(CalendarioActivity);
            }
        });

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