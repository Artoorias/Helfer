package com.example.filip.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class utils {
    private final static String PREFERENCES_NAME = "conf";
    private final static String APK_VERSION_KEY = "APK_VERSION";
    private final static String DB_VERSION_KEY = "DB_VERSION";
    static String logcat = "";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    public static boolean dbupate(SQLiteDatabase target, SQLiteDatabase source)throws Exception{
    //TODO
    return true;
    }

    public static String cursorToString(@NonNull Cursor crs) {
        JSONArray arr = new JSONArray();
        crs.moveToFirst();
        while (!crs.isAfterLast()) {
            int nColumns = crs.getColumnCount();
            JSONObject row = new JSONObject();
            for (int i = 0; i < nColumns; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {
                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(colName, Base64.encodeToString(crs.getBlob(i), Base64.NO_WRAP));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(colName, crs.getDouble(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(colName, crs.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                row.put(colName, null);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                row.put(colName, crs.getString(i));
                                break;
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            arr.put(row);
            if (!crs.moveToNext())
                break;
        }
        crs.close();
        return arr.toString();
    }

    static void show_debug_message(String tag, String msg) {
        logcat += tag + ": " + msg + String.valueOf((char) 13) + String.valueOf((char) 10);
        if (config.debug == true) {
            Log.d(tag, msg);
        }
    }

    protected static boolean check_db_updates(final Context cnt) throws Exception {
        try {
            sharedPref = cnt.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
            editor = sharedPref.edit();
            show_debug_message("check_db_updates", "Rozpoczynam");
            String path = "/data/data/com.example.filip.myapplication/databases";
            String DATABASE_NAME = "database";
            String DATABASE_PATH = "/data/data/com.example.filip.myapplication/databases/";
            InputStream in = cnt.getResources().openRawResource(R.raw.baza);
            OutputStream out = null;
            try {
                MainActivity.mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.dialog.setMessage("weryfikacja bazy danych");
                    }
                });
                if (config.debug == true) {
                    Log.d("check_db_updates", "Sprawdzam co z bazą");
                }
                if (new File(DATABASE_PATH + DATABASE_NAME).exists() == false) {
                    show_debug_message("check_db_updates", "Bazy nima trzeba skopiować");

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
                    show_debug_message("check_db_updates", "Baza jest, nie kopiuje");

                }
            } catch (IOException e) {
                if (config.debug == true) {
                    Log.d("check_db_updates", "ProblMainActivity.emy z kopiowaniMainActivity.em bazy: " + e.getMessage());
                }
                e.printStackTrace();
                MainActivity.em = "ProblMainActivity.emy z kopiowaniMainActivity.em bazy";
                throw e;
            }
            show_debug_message("check_db_updates", "Sprawdzam czy baza działa");
            try {
                MainActivity.sql = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                Cursor c = MainActivity.sql.rawQuery("SELECT * from artykoly;", null);
            } catch (Exception e) {
                show_debug_message("check_db_updates", "Baza ni działa");
                MainActivity.em = "Baza ni działa";
                throw e;
            }
            if (config.debug == true) {
                Log.d("check_db_updates", "Baza działa");
                Log.d("check_db_updates", "Weryficaja _conf");
            }
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.setMessage("Szukam aktualizacji bazy danych...");
                }
            });
            Thread.sleep(1000);
            Cursor db_ver = MainActivity.sql.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";", null);
            Cursor apk_ver = MainActivity.sql.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";", null);
            apk_ver.moveToFirst();
            db_ver.moveToFirst();
            String versja = apk_ver.getString(0);
            if (sharedPref.getString(APK_VERSION_KEY, "").equals("")) {
                show_debug_message("check_db_updates", "versja aplikacji nie zapisana (pierwsze uruchomienie)");
                editor.putString(APK_VERSION_KEY, MainActivity.pinfo.versionName);
            } else if (sharedPref.getString(APK_VERSION_KEY, "").equals(MainActivity.pinfo.versionName)) {
                show_debug_message("check_db_updates", "versja aplikacji aktualne");

            } else {
                show_debug_message("check_db_updates", "versja aplikacji nieaktualne (był update)");
                show_debug_message("check_db_updates", "otwieram baze danych z resoiurces");
                try {
                    out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME + ".bak");
                    in = cnt.getResources().openRawResource(R.raw.baza);
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
                    MainActivity.em = "Plik nie znaleziony";
                    throw e;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
                SQLiteDatabase sqtmp = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME + ".bak", null, SQLiteDatabase.OPEN_READWRITE);
                db_ver = sqtmp.rawQuery("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";", null);
                db_ver.moveToFirst();
                String wer = db_ver.getString(0);
                final Cursor tmp = MainActivity.sql.rawQuery("Select wartosc from _conf;", null);
                tmp.moveToFirst();
                if (!wer.equals(tmp.getString(0))) {
                    show_debug_message("check_db_updates", "versja bazy danych nie jest aktualne (był update)");
                    if(!utils.dbupate(MainActivity.sql, sqtmp)){
                        MainActivity.em="Brak dodatkowych informacji";
                        throw new Exception("błąd w trakcie aktualizacji bazy danych");
                    }
                    MainActivity.sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + wer + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";");
                    editor.putString(DB_VERSION_KEY, wer);
                } else {
                    show_debug_message("check_db_updates", "versja bazy danych jest aktualna");
                }
                show_debug_message("check_db_updates", "usuwanie tymczasowej bazy danych)");

                new File(DATABASE_PATH + DATABASE_NAME + ".bak").delete();
                editor.putString(APK_VERSION_KEY, MainActivity.pinfo.versionName);
            }
            if (versja.equals("-1")) {
                show_debug_message("check_db_updates", "versja aplikacji nie zapisana w db (pierwsze uruchomienie)");
                MainActivity.sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
            } else if (versja.equals(MainActivity.pinfo.versionName)) {
                show_debug_message("check_db_updates", "versja aplikacji w db aktualna");
            } else {
                show_debug_message("check_db_updates", "versja aplikacji w db nie aktualna (był update)");
                Log.d("sql", "Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
                MainActivity.sql.execSQL("Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
            }
            versja = db_ver.getString(0);
            if (sharedPref.getString(DB_VERSION_KEY, "").equals("")) {
                show_debug_message("check_db_updates", "versja bazy danych nie istnieje (pierwsze uruchomienie)");
                NotificationManager notificationManager =
                        (NotificationManager) cnt.getSystemService(cnt.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "woda";
                    String description = "powiadomienia o picu wody";
                    NotificationChannel channel = new NotificationChannel("woda", name, NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(description);
                    channel.setLightColor(Color.BLUE);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationManager.createNotificationChannel(channel);
                     name = "kanapka";
                     description = "powiadomienia o braniu kanapek";
                     channel = new NotificationChannel("kanapka", name, NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(description);
                    channel.setLightColor(Color.BLUE);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationManager.createNotificationChannel(channel);
                }
                editor.putString(DB_VERSION_KEY, versja);
            } else {
                show_debug_message("check_db_updates", "versja bazy danych: " + versja + " wersja z shared " + sharedPref.getString(DB_VERSION_KEY, ""));
            }
            show_debug_message("check_db_updates", "aktualnie zainstalowana wersja db " + versja);
            if (config.debug == true) {
                final Cursor tmp = MainActivity.sql.rawQuery("Select wartosc from _conf;", null);
                tmp.moveToFirst();
                MainActivity.mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(cnt, "Witam w wersji DEBUG, ta wersja nie jest przezbaczona do urzytku pordukcyjnego, wersj: " + MainActivity.pinfo.versionName + " wersja bazy danych to: " + tmp.getString(0) + " miłego debugu", Toast.LENGTH_LONG).show();
                    }
                });
            }
            Cursor c = null;
            editor.commit();
        } catch (Exception e) {
            throw e;
        }
        return true;
    }
}
