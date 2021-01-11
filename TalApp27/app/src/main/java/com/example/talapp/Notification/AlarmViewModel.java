package com.example.talapp.Notification;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.alarmRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.KEY_ALARM_DATA;
import static com.example.talapp.Utils.Util.KEY_ALARM_ID;
import static com.example.talapp.Utils.Util.KEY_ALARM_PERIODICO;
import static com.example.talapp.Utils.Util.KEY_ALARM_TIPO;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;
import static com.example.talapp.Utils.Util.isConnectedToInternet;

public class AlarmViewModel extends AndroidViewModel {

    private Context context;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void InsertAlarm(Calendar date, String tipo, String id, Boolean periodico) {
            Map<String, Object> alarm = new HashMap<>();
            alarm.put(KEY_ALARM_DATA, date.getTime());
            alarm.put(KEY_ALARM_TIPO, tipo);
            alarm.put(KEY_ALARM_ID, id);
            alarm.put(KEY_ALARM_PERIODICO, periodico);

            //alarmRef.add(alarm);
    }
    
}
