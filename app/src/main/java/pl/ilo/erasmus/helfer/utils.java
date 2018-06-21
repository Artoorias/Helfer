package pl.ilo.erasmus.helfer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class utils {
    private final static String PREFERENCES_NAME = "conf";
    private final static String APK_VERSION_KEY = "APK_VERSION";
    private final static String DB_VERSION_KEY = "DB_VERSION";
    static String logcat = "";
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;
    public static JSONArray convertToJSON(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {
                JSONObject obj = new JSONObject();
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
                jsonArray.put(obj);
            }
        }
        return jsonArray;
    }
    @NonNull
    //function call if update detected
    public static boolean dbupate(@NonNull Connection target, @NonNull Connection source) throws Exception {
        //TODO
        return true;
    }

    @Nullable
    //helpfull function to returning data
    static String returnData(@NonNull String data) {
        return "{ \"success\": true, \"data\": " + data + " }";
    }

    //check network conection
    static protected boolean check_net(Context cnt) throws Exception {
        ConnectivityManager cm = (ConnectivityManager) cnt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting(); //if netinfo null or not conected return false
    }

    @Nullable
    //helpfull function to returning data (witch not string)
    static String returnData(@NonNull String data, @NonNull boolean is_string) {
        if (is_string) {
            return "{ \"success\": true, \"data\": \"" + data + "\" }";
        } else {
            return "{ \"success\": true, \"data\": " + data + " }";

        }
    }

    @Nullable
    //helpfull function to returning data(error)
    static String returnError(@NonNull String error) {
        return "{ \"success\": false, \"error\": \"" + error + "\" }";
    }

    @NonNull
    //function convert cursor to string
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
                        //detecting and converting types
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

    // helpfull function to show debug info (or write to our logcat)
    static void show_debug_message(String tag, String msg) {
        logcat += tag + ": " + msg + String.valueOf((char) 13) + String.valueOf((char) 10);
        if (config.debug == true) {

            Log.d(tag, msg);
        }
    }

    //check db if exist, need update, etc.
    protected static boolean check_db_updates(final Context cnt) throws Exception {
        try {
            sharedPref = cnt.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
            editor = sharedPref.edit();
            show_debug_message("check_db_updates", "Rozpoczynam");
            String path = "/data/data/pl.ilo.erasmus.helfer/databases";
            String DATABASE_NAME = "database";
            String DATABASE_PATH = "/data/data/pl.ilo.erasmus.helfer/databases/";
            InputStream in = cnt.getResources().openRawResource(pl.ilo.erasmus.helfer.R.raw.baza);
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
                //check if database exist
                if (new File(DATABASE_PATH + DATABASE_NAME).exists() == false) {
                    show_debug_message("check_db_updates", "Bazy nima trzeba skopiować");
                    //copy if not exist
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
                //check working of database
                String url = "jdbc:sqlite:"+(DATABASE_PATH + DATABASE_NAME);
                // create a connection to the database
                MainActivity.sql =  DriverManager.getConnection(url);
                MainActivity.sql.prepareStatement("SELECT * from artykoly;").execute();
            } catch (Exception e) {
                show_debug_message("check_db_updates", "Baza ni działa");
                MainActivity.em = "Baza ni działa";
                throw e;
            }
            show_debug_message("check_db_updates", "Baza działa");
            show_debug_message("check_db_updates", "Weryficaja _conf");
            MainActivity.mhandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.dialog.setMessage("Szukam aktualizacji bazy danych...");
                }
            });
            Thread.sleep(1000);
            ResultSet db_ver = MainActivity.sql.prepareStatement("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";").executeQuery();
            ResultSet apk_ver = MainActivity.sql.prepareStatement("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";").executeQuery();
            apk_ver.beforeFirst();
            apk_ver.next();
            db_ver.beforeFirst();
            db_ver.next();
            String versja = apk_ver.getString(0);
            //check from shared settings apkvbersion
            if (sharedPref.getString(APK_VERSION_KEY, "").equals("")) {
                show_debug_message("check_db_updates", "versja aplikacji nie zapisana (pierwsze uruchomienie)");
                editor.putString(APK_VERSION_KEY, MainActivity.pinfo.versionName);
            } else if (sharedPref.getString(APK_VERSION_KEY, "").equals(MainActivity.pinfo.versionName)) {
                show_debug_message("check_db_updates", "versja aplikacji aktualne");

            } else {
                //update !!! WE NEED TO GET ACCESS TO DATABASE FROM RESOURCES
                show_debug_message("check_db_updates", "versja aplikacji nieaktualne (był update)");
                show_debug_message("check_db_updates", "otwieram baze danych z resoiurces");
                try {
                    //we need to copy
                    out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME + ".bak");
                    in = cnt.getResources().openRawResource(pl.ilo.erasmus.helfer.R.raw.baza);
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
                //get version
                Connection sqtmp = DriverManager.getConnection("jdbc:sqlite:"+(DATABASE_PATH + DATABASE_NAME+ ".bak"));
                db_ver = sqtmp.prepareStatement("Select wartosc from _conf where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";").executeQuery();
                db_ver.beforeFirst();
                db_ver.next();
                String wer = db_ver.getString(0);
                final ResultSet tmp = MainActivity.sql.prepareStatement("Select wartosc from _conf;").executeQuery();
                tmp.beforeFirst();
                tmp.next();
                if (!wer.equals(tmp.getString(0))) {
                    show_debug_message("check_db_updates", "versja bazy danych nie jest aktualne (był update)");
                    if (!utils.dbupate(MainActivity.sql, sqtmp)) {//do updates
                        MainActivity.em = "Brak dodatkowych informacji";
                        throw new Exception("błąd w trakcie aktualizacji bazy danych");
                    }
                    MainActivity.sql.prepareStatement("Update _conf set wartosc = " + String.valueOf((char) 34) + wer + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_db" + String.valueOf((char) 34) + ";").execute();
                    editor.putString(DB_VERSION_KEY, wer);
                } else {
                    show_debug_message("check_db_updates", "versja bazy danych jest aktualna");
                }
                show_debug_message("check_db_updates", "usuwanie tymczasowej bazy danych)");

                new File(DATABASE_PATH + DATABASE_NAME + ".bak").delete(); //adn remove tmp database
                editor.putString(APK_VERSION_KEY, MainActivity.pinfo.versionName);
            }
            //check from database
            if (versja.equals("-1")) {
                show_debug_message("check_db_updates", "versja aplikacji nie zapisana w db (pierwsze uruchomienie)");
                MainActivity.sql.prepareStatement("Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";").execute();
            } else if (versja.equals(MainActivity.pinfo.versionName)) {
                show_debug_message("check_db_updates", "versja aplikacji w db aktualna");
            } else {
                show_debug_message("check_db_updates", "versja aplikacji w db nie aktualna (był update)");
                Log.d("sql", "Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";");
                MainActivity.sql.prepareStatement("Update _conf set wartosc = " + String.valueOf((char) 34) + MainActivity.pinfo.versionName + String.valueOf((char) 34) + " where klucz = " + String.valueOf((char) 34) + "wersja_apk" + String.valueOf((char) 34) + ";").execute();
            }
            //update if db updated
            versja = db_ver.getString(0);
            if (sharedPref.getString(DB_VERSION_KEY, "").equals("")) {
                //register notification channels only for android OREO and latest
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
                final ResultSet tmp = MainActivity.sql.prepareStatement("Select wartosc from _conf;").executeQuery();
                tmp.beforeFirst();
                tmp.next();
                MainActivity.mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(cnt, "Witam w wersji DEBUG, ta wersja nie jest przezbaczona do urzytku pordukcyjnego, wersj: " + MainActivity.pinfo.versionName + " wersja bazy danych to: " + tmp.getString(0) + " miłego debugu", Toast.LENGTH_LONG).show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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
