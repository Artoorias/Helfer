package com.example.filip.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.Serializable;
import java.util.Calendar;

public class InitRunable implements Runnable {
    protected final static int WODA_1 = 0;
    protected final static int WODA_2 = 1;
    protected final static int KANAPKA = 2;
    static Context cnt;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void run() {
        try {
            utils.show_debug_message("Thread", "Rozpoczynam");
            MainActivity.dialog.setProgressStyle(MainActivity.dialog.STYLE_SPINNER);
            MainActivity.dialog.setMessage("Inicjalizacja...");
            MainActivity.dialog.setIndeterminate(true);
            MainActivity.dialog.setCanceledOnTouchOutside(false);
            MainActivity.dialog.setCancelable(false);
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.show();
                }
            });
            SensorManager sensorMgr = (SensorManager) cnt.getSystemService(cnt.SENSOR_SERVICE);
            sensorMgr.registerListener(new sl(cnt),
                    SensorManager.SENSOR_ACCELEROMETER,
                    SensorManager.SENSOR_DELAY_GAME);
            alarmMgr = (AlarmManager) cnt.getSystemService(cnt.ALARM_SERVICE);
            Intent it = new Intent(cnt.getApplicationContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), WODA_1, it, PendingIntent.FLAG_NO_CREATE);
            Calendar calendar = Calendar.getInstance();
            if (alarmIntent == null) {
                utils.show_debug_message("Thread", "woda_1 prepare");
                alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), WODA_1, it, 0);
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 17);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
            }
            alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), WODA_2, it, PendingIntent.FLAG_NO_CREATE);
            if (alarmIntent == null) {
                utils.show_debug_message("Thread", "woda_2 prepare");
                alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), WODA_2, it, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 7);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
            }
            try {
                MainActivity.pinfo = cnt.getPackageManager().getPackageInfo(cnt.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Intent err = new Intent(cnt.getApplicationContext(), Main3Activity.class);
                err.putExtra("exception", (Serializable) e);
                err.putExtra("add_info", "Błąd w trakcie pobierania informacji o pakiecie");
                cnt.startActivity(err);
                ((Activity) cnt).finish();
                e.printStackTrace();
                return;
            }
            utils.show_debug_message("Thread", "check_db");

            try {
                utils.check_db_updates(cnt);
            } catch (Exception e) {
                Intent err = new Intent(cnt.getApplicationContext(), Main3Activity.class);
                err.putExtra("exception", (Serializable) e);
                err.putExtra("add_info", MainActivity.em);
                cnt.startActivity(err);
                ((Activity) cnt).finish();
                e.printStackTrace();
                return;
            }
            utils.show_debug_message("Thread", "check_db_end");
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.setMessage("Powiadomienia...");
                }
            });
            ((Activity) cnt).findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationManager notificationManager =
                                (NotificationManager) cnt.getSystemService(cnt.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(cnt.getApplicationContext(), "woda")
                                        .setSmallIcon(R.drawable.icon)
                                        .setLargeIcon(BitmapFactory.decodeResource(cnt.getResources(), R.drawable.woda_ico))
                                        .setContentTitle("Pij wode")
                                        .setPriority(NotificationManager.IMPORTANCE_MAX)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setContentText("Pij wode")
                                        .setTicker("HELFER")
                                        .setAutoCancel(true);
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                        mBuilder.setLights(Color.BLUE, 500, 500);
                        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
                        mBuilder.setVibrate(pattern);
                        notificationManager.notify(0, mBuilder.build());
                    } else {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(cnt.getApplicationContext())
                                        .setSmallIcon(R.drawable.icon)
                                        .setLargeIcon(BitmapFactory.decodeResource(cnt.getResources(), R.drawable.woda_ico))
                                        .setContentTitle("Pij wode")
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setContentText("Pij wode")
                                        .setTicker("HELFER")
                                        .setWhen(0)
                                        .setAutoCancel(true);
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setSound(alarmSound);
                        mBuilder.setLights(Color.BLUE, 500, 500);
                        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
                        mBuilder.setVibrate(pattern);
                        NotificationManager notificationManager =
                                (NotificationManager) cnt.getSystemService(cnt.NOTIFICATION_SERVICE);
                        notificationManager.notify(0, mBuilder.build());
                    }
                }
            });
            utils.show_debug_message("Thread", "inicjalizacja wyglądu");
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.setMessage("Inicjalizacja wyglądu...");
                    WebView web = (WebView) ((Activity) cnt).findViewById(R.id.strona);
                    web.getSettings().setJavaScriptEnabled(true);
                    web.getSettings().setAllowFileAccess(true);
                    web.getSettings().setAllowContentAccess(true);
                    if (config.debug == true && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        web.setWebContentsDebuggingEnabled(true);
                    }
                    web.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    web.loadUrl("file:///android_res/raw/layout.html");//od tej linii łapy precz
                    web.addJavascriptInterface(new WebAppInterface(cnt, MainActivity.sql), "Android");
                }
            });
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.cancel();
                }
            });
        } catch (Exception e) {
            Intent err = new Intent(cnt.getApplicationContext(), Main3Activity.class);
            err.putExtra("exception", (Serializable) e);
            err.putExtra("add_info", "Brak dodatkowych informacji");
            cnt.startActivity(err);
            ((Activity) cnt).finish();
            e.printStackTrace();
            return;
        }
        utils.show_debug_message("Thread", "Koniec");
    }
}
