package com.MartinaSosto.talapp.Sveglie;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;

public class SvegliaDialog extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private MutableLiveData<String> orario;

    public SvegliaDialog(MutableLiveData<String> s) {
        this.orario = s;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        orario.setValue(hourOfDay+":"+minute);
    }
}