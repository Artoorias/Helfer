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
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.Calendar;

public class InitRunable implements Runnable {

    static Context cnt;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int debug_button_id = 23342;
    @Override
    //initialize thread
    public void run() {
        try {
            utils.show_debug_message("Thread", "Rozpoczynam");
            utils.show_debug_message("Thread", "Progress dialog");
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
            try {
                //get package info
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
            //register listener to sensor service
            SensorManager sensorMgr = (SensorManager) cnt.getSystemService(cnt.SENSOR_SERVICE);
            MainActivity.listener = new sl(cnt);
            sensorMgr.registerListener(MainActivity.listener,
                    SensorManager.SENSOR_ACCELEROMETER,
                    SensorManager.SENSOR_DELAY_GAME);
            utils.show_debug_message("Thread", "check_db");

            try {
                //check db(update, install, check working)
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
            //prepare alarm menager
            alarmMgr = (AlarmManager) cnt.getSystemService(cnt.ALARM_SERVICE);
            Intent it = new Intent(cnt.getApplicationContext(), AlarmReceiver.class);
            it.putExtra("id", config.WODA_1);
            alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.WODA_1, it, PendingIntent.FLAG_NO_CREATE);
            Calendar calendar = Calendar.getInstance();
            //for WODA_1
            if (alarmIntent == null) { //alarm menager is FLAG_NO_CREATE (if exist i get null)
                utils.show_debug_message("Thread", "woda_1 prepare");
                alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.WODA_1, it, 0);
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
            it.putExtra("id", config.WODA_2);
            alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.WODA_2, it, PendingIntent.FLAG_NO_CREATE);
            // for WODA_2
            if (alarmIntent == null) {
                utils.show_debug_message("Thread", "woda_2 prepare");
                alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.WODA_2, it, 0);
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
            it.putExtra("id", config.KANAPKA);
            alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.KANAPKA, it, PendingIntent.FLAG_NO_CREATE);
            //for KANAPKA
            if (alarmIntent == null) {
                utils.show_debug_message("Thread", "KANAPKA prepare");
                alarmIntent = PendingIntent.getBroadcast(cnt.getApplicationContext(), config.KANAPKA, it, 0);
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 19);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
            }
            //prepare notyfication button
            if (config.debug==true) {
                 MainActivity.mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        params.weight = 90;
                        ((WebView) ((Activity) cnt).findViewById(R.id.strona)).setLayoutParams(params);
                        Button btn = new Button(cnt);
                        btn.setId(debug_button_id);
                        btn.setText("Pokarz powiadomienie");
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //for kommemts look to alarm reciver
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
                        LinearLayout ll = (LinearLayout)((Activity) cnt).findViewById(R.id.linlay);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.weight = 10;
                        ll.addView(btn, lp);
                    }});

            }
            // web view initialize
            utils.show_debug_message("Thread", "inicjalizacja wyglądu");
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.setMessage("Inicjalizacja wyglądu...");
                    WebView web = (WebView) ((Activity) cnt).findViewById(R.id.strona);
                    web.getSettings().setJavaScriptEnabled(true);
                    web.getSettings().setAllowFileAccess(true);
                    web.getSettings().setAllowContentAccess(true);
                    //debuging mode
                    if (config.debug == true && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        web.setWebContentsDebuggingEnabled(true);
                    }
                    web.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    web.loadUrl("file:///android_res/raw/index.html");//od tej linii łapy precz
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
            //if something went worong go to "emergency mode"
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
