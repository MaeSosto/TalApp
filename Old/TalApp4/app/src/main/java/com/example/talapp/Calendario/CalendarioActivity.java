package com.example.talapp.Calendario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.talapp.R;

public class CalendarioActivity extends AppCompatActivity {
    private boolean FAB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
        findViewById(R.id.FABEsami).setVisibility(View.GONE);
        findViewById(R.id.FABTerapie).setVisibility(View.GONE);
        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FAB){
                    FAB = false;
                    findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
                    findViewById(R.id.FABEsami).setVisibility(View.GONE);
                    findViewById(R.id.FABTerapie).setVisibility(View.GONE);
                }
                else{
                    FAB = true;
                    findViewById(R.id.FABTrasfusioni).setVisibility(View.VISIBLE);
                    findViewById(R.id.FABEsami).setVisibility(View.VISIBLE);
                    findViewById(R.id.FABTerapie).setVisibility(View.VISIBLE);
                }
            }
        });
    }
}