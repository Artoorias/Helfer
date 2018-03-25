package com.example.filip.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private final static String PREFERENCES_NAME = "conf";
    private final static String APK_VERSION_KEY = "APK_VERSION";
    private final static String DB_VERSION_KEY = "DB_VERSION";
    SQLiteDatabase sql = null;
    PackageInfo pinfo;
    ProgressDialog dialog;
    Handler mhandler;
    String em = "";
    android.support.v7.app.AlertDialog al;
    android.support.v7.app.AlertDialog.Builder alertDialog;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    Runnable run = new Runnable() {
        @Override
        public void run() {
                        try {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Inicjalizacja...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
               /* alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent it = new Intent(getApplicationContext(), AlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 123123123, it, 0);
                int i = 60;
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + (i * 1000), (i * 1000), alarmIntent);
*/
                try {
                    pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Intent err = new Intent(getApplicationContext(), Main3Activity.class);
                    err.putExtra("exception", (Serializable) e);
                    err.putExtra("add_info", "Błąd w trakcie pobierania informacji o pakiecie");
                    startActivity(err);
                    finish();
                    e.printStackTrace();
                    return;
                }

                try {
                    check_db_updates();
                } catch (Exception e) {
                    Intent err = new Intent(getApplicationContext(), Main3Activity.class);
                    err.putExtra("exception", (Serializable) e);
                    err.putExtra("add_info", em);
                    startActivity(err);
                    finish();
                    e.printStackTrace();
                    return;
                }

                if (config.debug == true) {
                    Log.d("Create", "Koniec");
                }
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Powiadomienia...");
                    }
                });
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), "woda")
                                            .setSmallIcon(R.drawable.icon)
                                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.woda_ico))
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
                                    new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.icon)
                                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.woda_ico))
                                            .setContentTitle("Pij wode")
                                            .setPriority(Notification.PRIORITY_MAX)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setContentText("Pij wode")
                                            .setTicker("HELFER")
                                            .setWhen(0)
                                            .setAutoCancel(true);
                            ;
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            mBuilder.setSound(alarmSound);
                            mBuilder.setLights(Color.BLUE, 500, 500);
                            long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
                            mBuilder.setVibrate(pattern);
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            notificationManager.notify(0, mBuilder.build());
                        }
                    }
                });
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Inicjalizacja wyglądu...");
                    }
                });
               /* try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WebView web = (WebView) findViewById(R.id.strona);
                        web.getSettings().setJavaScriptEnabled(true);
                        web.getSettings().setAllowFileAccess(true);
                        web.getSettings().setAllowContentAccess(true);
                        if (config.debug == true && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            web.setWebContentsDebuggingEnabled(true);
                        }
                        web.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                        web.loadUrl("file:///android_res/raw/layout.html");//od tej linii łapy precz
                        web.addJavascriptInterface(new WebAppInterface(MainActivity.this, sql), "Android");
                    }
                });
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                });
            } catch (Exception e) {
                Intent err = new Intent(getApplicationContext(), Main3Activity.class);
                err.putExtra("exception", (Serializable) e);
                err.putExtra("add_info", "Brak dodatkowych informacji");
                startActivity(err);
                finish();
                e.printStackTrace();
                return;
            }
        }
    };
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    protected void onStop() {
        if (config.debug == true) {
            Log.d("end", "Kończenie");
        }
        if (sql != null) {
            if (sql.isOpen()) {
                try {
                    if (config.debug == true) {
                        Log.d("end", "czyszczenie");
                    }
                    sql.execSQL("VACUUM;");
                } catch (Exception e) {
                    if (config.debug == true) {
                        Log.d("end", "błąd czyszczenia " + e.getMessage());
                    }
                }
            }
        } else {
            if (config.debug == true) {
                Log.d("end", "null pointer exception :)");
            }
        }
        super.onStop();
        if (config.debug == true) {
            Log.d("end", "Zminimalizowane");
        }
    }

    protected void onDestroy() {
        if (config.debug == true) {
            Log.d("destroy", "Niszczenie");
        }
        if (sql != null) {
            if (sql.isOpen()) {
                sql.close();
            }
        } else {
            if (config.debug == true) {
                Log.d("end", "null pointer exception :)");
            }
        }
        if (config.debug == true) {
            Log.d("end", "Zniszczone");
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == 0 && requestCode == 1 && check_net()) {
                al.cancel();
                new Thread(run).start();
            }
        } catch (Exception e) {
            Intent err = new Intent(getApplicationContext(), Main3Activity.class);
            err.putExtra("exception", (Serializable) e);
            err.putExtra("add_info", "Brak dodatkowych informacji");
            startActivity(err);
            finish();
            e.printStackTrace();
            return;
        }
    }

    protected boolean check_db_updates() throws Exception {
        try {
            sharedPref = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
            editor = sharedPref.edit();
            if (config.debug == true) {
                Log.d("check_db_updates", "Rozpoczynam");
            }
            String path = "/data/data/com.example.filip.myapplication/databases";
            String DATABASE_NAME = "database";
            String DATABASE_PATH = "/data/data/com.example.filip.myapplication/databases/";
            InputStream in = getResources().openRawResource(R.raw.baza);
            OutputStream out = null;
            try {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("weryfikacja bazy danych");
                    }
                });
                //Thread.sleep(1000);
                if (config.debug == true) {
                    Log.d("check_db_updates", "Sprawdzam co z bazą");
                }
                if (new File(DATABASE_PATH + DATABASE_NAME).exists() == false) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "Bazy nima trzeba skopiować");
                    }
                    new File(DATABASE_PATH).mkdirs();
                    out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
                    byte[] buff = new byte[1024];
                    int read = 0;
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                    out.close();
                    in.close();
                } else {
                    if (config.debug == true) {
                        Log.d("Create", "Baza jest, nie kopiuje");
                    }
                }
            } catch (IOException e) {
                if (config.debug == true) {
                    Log.d("check_db_updates", "Problemy z kopiowaniem bazy: " + e.getMessage());
                }
                e.printStackTrace();
                em = "Problemy z kopiowaniem bazy";
                throw e;
            } /*catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }*/
                if (config.debug == true) {
                    Log.d("check_db_updates", "Sprawdzam czy baza działa");
                }
                try {
                    sql = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                    Cursor c = sql.rawQuery("SELECT Dish from Przepisy_do_aplikacji_Erasmus;", null);
                } catch (Exception e) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "Baza ni działa");
                    }
                    em = "Baza ni działa";
                    throw e;
                    //System.exit(0);
                }
                if (config.debug == true) {
                    Log.d("check_db_updates", "Baza działa");
                    Log.d("check_db_updates", "Weryficaja _conf");
                }
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Szukam aktualizacji bazy danych...");
                    }
                });
                Thread.sleep(1000);
                Cursor db_ver = sql.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";", null);
                Cursor apk_ver = sql.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";", null);
                apk_ver.moveToFirst();
                db_ver.moveToFirst();
                String versja = apk_ver.getString(0);
                if (sharedPref.getString(APK_VERSION_KEY, "").equals("")) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja aplikacji nie zapisana (pierwsze uruchomienie)");
                    }
                    editor.putString(APK_VERSION_KEY, pinfo.versionName);
                } else if (sharedPref.getString(APK_VERSION_KEY, "").equals(pinfo.versionName)) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja aplikacji aktualne");
                    }
                } else {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja aplikacji nieaktualne (był update)");
                    }
                    editor.putString(APK_VERSION_KEY, pinfo.versionName);
                }
                if (versja.equals("-1")) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja aplikacji nie zapisana w db (pierwsze uruchomienie)");
                    }
                    sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
                } else if (versja.equals(pinfo.versionName)) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja aplikacji w db aktualna");
                    }
                } else {
                    if (config.debug == true) {
                        Log.d("Create", "versja aplikacji w db nie aktualna (był update)");
                    }
                    Log.d("sql", "Update _conf set wartosc = " + String.valueOf((char) 34) + pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
                    sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
                }
                versja = db_ver.getString(0);
                if (sharedPref.getString(DB_VERSION_KEY, "").equals("")) {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja bazy danych nie istnieje (pierwsze uruchomienie)");
                    }
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "woda";
                        String description = "powiadomienia o picu wody";
                        NotificationChannel channel = new NotificationChannel("woda", name, NotificationManager.IMPORTANCE_HIGH);
                        channel.setDescription(description);
                        channel.setLightColor(Color.BLUE);
                        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                        notificationManager.createNotificationChannel(channel);
                    }
                    editor.putString(DB_VERSION_KEY, versja);
                } else {
                    if (config.debug == true) {
                        Log.d("check_db_updates", "versja bazy danych: " + versja + " wersja z shared " + sharedPref.getString(DB_VERSION_KEY, ""));
                        Log.d("check_db_updates", "otwieram baze danych z resoiurces");
                    }
                    try {
                        out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME + ".bak");
                        in = getResources().openRawResource(R.raw.baza);
                        byte[] buff = new byte[1024];
                        int read = 0;
                        while ((read = in.read(buff)) > 0) {
                            out.write(buff, 0, read);
                        }
                        out.flush();
                        out.close();
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        em = "Plik nie znaleziony";
                        throw e;
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }
                    SQLiteDatabase sqtmp = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME + ".bak", null, SQLiteDatabase.OPEN_READWRITE);
                    db_ver = sqtmp.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";", null);
                    db_ver.moveToFirst();
                    String wer = db_ver.getString(0);
                    if (!wer.equals(versja)) {
                        if (config.debug == true) {
                            Log.d("check_db_updates", "versja bazy danych nie jest aktualne (był update)");
                        }
                        sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + wer + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";");
                        editor.putString(DB_VERSION_KEY, wer);
                    } else {
                        if (config.debug == true) {
                            Log.d("check_db_updates", "versja bazy danych jest aktualna");
                        }
                    }
                    if (config.debug == true) {
                        Log.d("check_db_updates", "usuwanie tymczasowej bazy danych)");
                    }
                    new File(DATABASE_PATH + DATABASE_NAME + ".bak").delete();
                }
                if (config.debug == true) {
                    Log.d("check_db_updates", "aktualnie zainstalowana wersja db " + versja);
                }


            if (config.debug == true) {
                final Cursor tmp = sql.rawQuery("Select wartosc from _conf;", null);
                tmp.moveToFirst();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Witam w wersji DEBUG, ta wersja nie jest przezbaczona do urzytku pordukcyjnego, wersj: "+ pinfo.versionName+" wersja bazy danych to: "+tmp.getString(0)+" miłego debugu", Toast.LENGTH_LONG).show();
                    }
                });
            }
            Cursor c = null;
            if (config.debug == true) {
                Log.d("check_db_updates", "Inicjalizuje web view");
            }
            editor.commit();
        }catch (Exception e){
            throw e;
        }
        return true;
    }

    protected boolean check_net() throws Exception {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            mhandler = new Handler();
            getSupportActionBar().hide();
            dialog = new ProgressDialog(MainActivity.this);
            if (config.debug == true) {
                Log.d("create", "Rozpoczynam");
                Log.d("create", "Progress dialog");
            }
            if (!check_net()) {
                Log.d("create", "Netu nima");
                alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
                alertDialog.setTitle("Nie mamy netu");
                alertDialog.setMessage("Do działania aplikacji wymagane jest połączenie z internetem (narazie)!!!");
                alertDialog.setPositiveButton("Zamknij aplikacje", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("Otwóż ustawienia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intent, 1);
                    }
                });
                al = alertDialog.create();
                al.show();
                al.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intent, 1);
                    }
                });
           } else {
                new Thread(run).start();
            }
        } catch (Exception e) {
            Intent err = new Intent(getApplicationContext(), Main3Activity.class);
            err.putExtra("exception", (Serializable) e);
            err.putExtra("add_info", "Brak dodatkowych informacji");
            startActivity(err);
            finish();
            e.printStackTrace();
            return;
        }
    }
}
