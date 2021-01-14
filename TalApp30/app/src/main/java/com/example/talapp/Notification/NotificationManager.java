package com.example.talapp.Notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.talapp.R;

public class NotificationManager extends IntentService {
    private static final String CHANNEL_ID = "NotificationChannel";

    public NotificationManager(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


}
