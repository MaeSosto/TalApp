package com.example.talapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static com.example.talapp.Notification.JobService.ACTION_CLOCK;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION", "RICEVO");
        Intent serviceIntent = new Intent(context, JobService.class);
        if(intent.getAction() == ACTION_CLOCK){
            serviceIntent.putExtra("BUNDLE", intent.getExtras());
        }
        serviceIntent.setAction(intent.getAction());
        JobService.enqueueWork(context, serviceIntent);
    }
}