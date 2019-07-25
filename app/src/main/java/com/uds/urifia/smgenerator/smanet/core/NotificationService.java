package com.uds.urifia.smgenerator.smanet.core;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.uds.urifia.smgenerator.MainActivity;
import com.uds.urifia.smgenerator.R;

public class NotificationService {
    String title;
    String body;
    NotificationManager notificationManager;
    Context context;

    public NotificationService(String title, String body,
                               Context context, NotificationManager notificationManager) {
        this.title = title;
        this.body = body;
        this.context = context;
        this.notificationManager = notificationManager;
    }

    public void do_notify(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        notificationManager.notify(0, builder.build());

    }
}