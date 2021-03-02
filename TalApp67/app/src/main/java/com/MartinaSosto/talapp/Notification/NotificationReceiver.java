package com.MartinaSosto.talapp.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.MartinaSosto.talapp.Notification.JobService.ACTION_CLOCK;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("NOTIFICATION", "RICEVO");
        Intent serviceIntent = new Intent(context, JobService.class);
        if(intent.getAction().equals(ACTION_CLOCK)){
            serviceIntent.putExtra("SVEGLIA", intent.getStringExtra("SVEGLIA"));
            //serviceIntent.putExtra("ID", intent.getIntExtra("ID", 0));
        }
        serviceIntent.setAction(intent.getAction());
        JobService.enqueueWork(context, serviceIntent);
    }
}