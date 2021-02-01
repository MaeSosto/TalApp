package com.example.talapp.Sveglie;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.talapp.R;

import java.util.Calendar;

public class SvegliaFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_sveglia, container, false);

        TimePicker timePicker = root. findViewById(R.id.timePickerSveglia);
        timePicker.setIs24HourView(true);
        timePicker.setHour(Calendar.getInstance().getTime().getHours());
        timePicker.setMinute(Calendar.getInstance().getTime().getMinutes());

        CheckBox checkBoxLun = root.findViewById(R.id.checkBoxLun);
        CheckBox checkBoxMar = root.findViewById(R.id.checkBoxMar);
        CheckBox checkBoxMer = root.findViewById(R.id.checkBoxMer);
        CheckBox checkBoxGio = root.findViewById(R.id.checkBoxGio);
        CheckBox checkBoxVen = root.findViewById(R.id.checkBoxVen);
        CheckBox checkBoxSab = root.findViewById(R.id.checkBoxSab);
        CheckBox checkBoxDom = root.findViewById(R.id.checkBoxDom);

        Button salva = root.findViewById(R.id.buttonSalvaSveglia);
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ora = timePicker.getHour();
                int minuti = timePicker.getMinute();

                Log.d("ORARIO", ora+ ":"+ minuti);

                if(checkBoxLun.isChecked()){ Log.d("GIORNO", "Lun"); }
                if(checkBoxMar.isChecked()){ Log.d("GIORNO", "Mar"); }
                if(checkBoxMer.isChecked()){ Log.d("GIORNO", "Mer"); }
                if(checkBoxGio.isChecked()){ Log.d("GIORNO", "Gio"); }
                if(checkBoxVen.isChecked()){ Log.d("GIORNO", "Ven"); }
                if(checkBoxSab.isChecked()){ Log.d("GIORNO", "Sab"); }
                if(checkBoxDom.isChecked()){ Log.d("GIORNO", "Dom"); }

            }
        });

        return root;
    }
}