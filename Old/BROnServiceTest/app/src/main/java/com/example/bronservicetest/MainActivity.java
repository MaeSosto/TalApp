package com.example.bronservicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static com.example.bronservicetest.ForegroundService.ACTION_TRASFUSIONE;
import static com.example.bronservicetest.ForegroundService.alarmManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = findViewById(R.id.buttonStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForegroundService.startService(getApplicationContext());
            }
        });

        Button buttonStopNotify = findViewById(R.id.buttonMIAO);
        buttonStopNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NOTIFICATION", "ELIMINO");
                Intent intentTest = new Intent(getApplicationContext(), NotificationReceiver.class);
                intentTest.setAction(ACTION_TRASFUSIONE);
                String id = "1";//intentTest.getStringExtra("KEY");
                //Log.d("ID", id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(id), intentTest, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
            }
        });

        Button stop = findViewById(R.id.buttonStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

}