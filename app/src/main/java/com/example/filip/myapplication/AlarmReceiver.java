package com.example.filip.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Tomek on 18.03.2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
/*        Toast.makeText(context, "Recieved!!", Toast.LENGTH_LONG).show();
        intent = new Intent();
        intent.setClass(context, Main2Activity.class); //Test is a dummy class name where to redirect
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
   /*   final WindowManager manager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        //layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = context.getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;*/

        /*final View view = View.inflate(context.getApplicationContext(),R.layout.activity_main, null);
        manager.addView(view, layoutParams);*/
        String nazwa = "";
        String zawartosc = "";
        String ticker = "";
        Bitmap li = null;
        String chan = "";
        int a = intent.getIntExtra("id", 0);
        if (intent.getIntExtra("id", 0) == config.WODA_1 || intent.getIntExtra("id", 0) == config.WODA_2) {
            nazwa = config.nazwa_w;
            zawartosc = config.zawartosc_w;
            ticker = config.ticker_w;
            li = BitmapFactory.decodeResource(context.getResources(), R.drawable.woda_ico);
            chan = "woda";
        } else {
            nazwa = config.nazwa_k;
            zawartosc = config.zawartosc_k;
            ticker = config.ticker_k;
            li = BitmapFactory.decodeResource(context.getResources(), R.drawable.kanapka);
            chan = "kanapka";
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, chan)
                            .setSmallIcon(R.drawable.icon)
                            .setLargeIcon(li)
                            .setContentTitle(nazwa)
                            .setPriority(NotificationManager.IMPORTANCE_MAX)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(zawartosc)
                            .setTicker(ticker)
                            .setAutoCancel(true);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setLights(Color.BLUE, 500, 500);
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
            mBuilder.setVibrate(pattern);
            notificationManager.notify(a, mBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icon)
                            .setLargeIcon(li)
                            .setContentTitle(nazwa)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(zawartosc)
                            .setTicker(ticker)
                            .setWhen(0)
                            .setAutoCancel(true);
            ;
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setLights(Color.BLUE, 500, 500);
            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
            mBuilder.setVibrate(pattern);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            notificationManager.notify(a, mBuilder.build());

        }

    }

}