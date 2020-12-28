package com.example.talapp.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.talapp.Calendario.CalendarioActivity;
import com.example.talapp.R;

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
}