package com.example.filip.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

public class InitRunable implements Runnable {
    static Context cnt;
    protected final static int WODA_1 = 0;
    protected final static int WODA_2 = 1;
    protected final static int KANAPKA = 2;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    public void run() {
        try{
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
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            SensorManager sensorMgr = (SensorManager) cnt.getSystemService(cnt.SENSOR_SERVICE);
            sensorMgr.registerListener(new SensorListener() {
                                           private static final int SHAKE_THRESHOLD = 10;
                                           long lastUpdate = 0;
                                           float last_x = 0;
                                           ActivityManager am = (ActivityManager) cnt.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                                           float last_y = 0;
                                           float last_z = 0;
                                           ComponentName cn;
                                           boolean is_s = false;

                                           @Override
                                           public void onSensorChanged(int sensor, float[] values) {
                                               cn = am.getRunningTasks(1).get(0).topActivity;
                                               if ("com.example.filip.myapplication.MainActivity".equals(cn.getClassName())) {
                                                   if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                                                       long curTime = System.currentTimeMillis();
                                                       if ((curTime - lastUpdate) > 100) {
                                                           long diffTime = (curTime - lastUpdate);
                                                           lastUpdate = curTime;

                                                           float x = values[SensorManager.DATA_X];
                                                           float y = values[SensorManager.DATA_Y];
                                                           float z = values[SensorManager.DATA_Z];

                                                           float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                                                           if (speed > SHAKE_THRESHOLD && !is_s) {
                                                               utils.show_debug_message("sensor", "shake detected w/ speed: " + speed);
                                                               View view = ((Activity)cnt).getWindow().getDecorView();
                                                               view.setDrawingCacheEnabled(true);
                                                               view.buildDrawingCache();
                                                               Bitmap b1 = view.getDrawingCache();
                                                               Rect frame = new Rect();
                                                               ((Activity)cnt).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                                                               int statusBarHeight = frame.top;
                                                               int width = ((Activity)cnt).getWindowManager().getDefaultDisplay().getWidth();
                                                               int height = ((Activity)cnt).getWindowManager().getDefaultDisplay().getHeight();
                                                               final Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
                                                               view.destroyDrawingCache();
                                                               android.support.v7.app.AlertDialog.Builder alertdialog = new android.support.v7.app.AlertDialog.Builder(cnt);
                                                               alertdialog.setTitle("Użyłeś opcji potrząsania do wyslania opinii. Czy wysłać?");
                                                               alertdialog.setCancelable(false);
                                                               alertdialog.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                       dialog.cancel();
                                                                       is_s = false;
                                                                   }
                                                               });
                                                               alertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                                   @Override
                                                                   public void onCancel(DialogInterface dialog) {
                                                                       is_s = false;
                                                                   }
                                                               });
                                                               alertdialog.setNeutralButton("Wyślij", new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                       try {
                                                                           is_s = false;
                                                                           File file = new File(cnt.getExternalCacheDir(),"logicchip.png");
                                                                           FileOutputStream fOut = null;
                                                                           fOut = new FileOutputStream(file);
                                                                           b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                                                           fOut.flush();
                                                                           fOut.close();
                                                                           StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                                                           StrictMode.setVmPolicy(builder.build());
                                                                           file.setReadable(true, false);
                                                                           Intent intent = new Intent(Intent.ACTION_SEND);
                                                                           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                           intent.putExtra(Intent.EXTRA_EMAIL, new String[]{config.email});
                                                                           intent.putExtra(Intent.EXTRA_SUBJECT, "I have a idea!");
                                                                           intent.putExtra(Intent.EXTRA_TEXT, "Your message here");
                                                                           intent.setType("text/html");
                                                                           intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                                                           intent.setType("image/jpeg");
                                                                           cnt.startActivity(Intent.createChooser(intent, "Wyślij zrzut"));

                                                                       } catch (FileNotFoundException e) {
                                                                           e.printStackTrace();
                                                                           Toast.makeText(cnt.getApplicationContext(),"Nie mogę zapisać zżutu", Toast.LENGTH_LONG).show();
                                                                       } catch (IOException e) {
                                                                           e.printStackTrace();
                                                                       }

                                                                   }
                                                               });
                                                               alertdialog.show();
                                                               is_s = true;
                                                           }
                                                           last_x = x;
                                                           last_y = y;
                                                           last_z = z;
                                                       }
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onAccuracyChanged(int sensor, int accuracy) {

                                           }
                                       },
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
                ((Activity)cnt).finish();
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
                ((Activity)cnt).finish();
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
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            ((Activity)cnt).findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
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
                }
            });
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    WebView web = (WebView) ((Activity)cnt).findViewById(R.id.strona);
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
            ((Activity)cnt).finish();
            e.printStackTrace();
            return;
        }
        utils.show_debug_message("Thread", "Koniec");
    }
}
