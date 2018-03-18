package com.example.filip.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Tomek on 18.03.2018.
 */

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Pij wode")
                        .setContentText("Pij wode");
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {    CharSequence name = "woda";
            String description ="powiadomienia o picu wody";
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("woda", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId("woda");
        }
        notificationManager.notify(0, mBuilder.build());

    }

}