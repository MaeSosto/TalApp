package com.example.bootreceivertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context aContext, Intent aIntent) {
        Log.d("BOOT RECEIVER", "RICEVO");
        Intent serviceIntent = new Intent(aContext, ForegroundService.class);
        ContextCompat.startForegroundService(aContext, serviceIntent);
    }
}
