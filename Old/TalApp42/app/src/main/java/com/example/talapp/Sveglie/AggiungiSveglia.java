package com.example.talapp.Sveglie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.talapp.R;

public class AggiungiSveglia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_sveglia);

        TimePicker timePicker = findViewById(R.id.timePickerSveglia);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton RBLun = findViewById(R.id.RBLun);
        RadioButton RBMar = findViewById(R.id.RBMar);
        RadioButton RBMer = findViewById(R.id.RBMer);
        RadioButton RBGio = findViewById(R.id.RBGio);
        RadioButton RBVen = findViewById(R.id.RBVen);
        RadioButton RBSab = findViewById(R.id.RBSab);
        RadioButton RBDom = findViewById(R.id.RBDom);

        Button salva = findViewById(R.id.buttonSalvaSveglia);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroup.
            }
        });
    }
}