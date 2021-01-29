package com.example.talapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "RICEVO");
        Intent serviceIntent = new Intent(context, JobService.class);
        serviceIntent.setAction(intent.getAction());
        JobService.enqueueWork(context, serviceIntent);
    }
}