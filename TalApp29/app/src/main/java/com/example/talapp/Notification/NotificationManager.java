package com.example.talapp.Notification;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class NotificationManager extends IntentService {

    public NotificationManager(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        //if (intent.getStringExtra("data") != null) {
        //    {
        //        String str=intent.getStringExtra("data");//get data here sended from BroadcastReceiver
        //    }
//
        //    return super.onStartCommand(intent,flags,startId);
    }

}
