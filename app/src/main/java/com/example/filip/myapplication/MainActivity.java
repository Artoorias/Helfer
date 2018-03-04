package com.example.filip.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase sql = null;
    private SharedPreferences sharedPref ;
    private SharedPreferences.Editor editor;
    private final static String PREFERENCES_NAME="conf";
    private final static String APK_VERSION_KEY="APK_VERSION";
    private final static String DB_VERSION_KEY="DB_VERSION";
    PackageInfo pinfo;
    protected void onStop() {
        if (config.debug==true) {
        Log.d("end","Kończenie");
        }
            if (sql.isOpen()) {
            try {
                if (config.debug==true) {
                    Log.d("end","czyszczenie");
                }
                sql.execSQL("VACUUM;");
            } catch (Exception e) {
                if (config.debug==true) {
                    Log.d("end","błąd czyszczenia "+e.getMessage());
                }
            }
        }
        super.onStop();
        if (config.debug==true) {
            Log.d("end","Zamknięte");
        }
    }

    protected void onDestroy() {
        if (config.debug==true) {
            Log.d("destroy","Niszczenie");
        }
        if (sql.isOpen()) {
            sql.close();
        }
        if (config.debug==true) {
            Log.d("end","Zniszczone");
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        sharedPref= getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        editor =sharedPref.edit();
        if (config.debug==true) {
            Log.d("Create","Rozpoczynam");
        }
        getSupportActionBar().hide();
        String path = "/data/data/com.example.filip.myapplication/databases";
        String DATABASE_NAME = "database";
        String DATABASE_PATH = "/data/data/com.example.filip.myapplication/databases/";
        InputStream in = getResources().openRawResource(R.raw.baza);
        OutputStream out = null;
        try {
            if (config.debug==true) {
                Log.d("Create","Sprawdzam co z bazą");
            }
            if (new File(DATABASE_PATH + DATABASE_NAME).exists() == false) {
                if (config.debug==true) {
                    Log.d("Create","Bazy nima trzeba skopiować");
                }
                out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
                byte[] buff = new byte[1024];
                int read = 0;
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            }else
            {
                if (config.debug==true) {
                    Log.d("Create","Baza jest, nie kopiuje");
                }
            }
        } catch (IOException e) {
            if (config.debug==true) {
                Log.d("Create","Problemy z kopiowaniem bazy: "+e.getMessage());
            }
            e.printStackTrace();
        } finally {
            if (config.debug==true) {
                Log.d("Create","Sprawdzam czy baza działa");
            }
            try {
                sql = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
                Cursor c = sql.rawQuery("SELECT Dish from Przepisy_do_aplikacji_Erasmus;", null);
            } catch (Exception e) {
                if (config.debug==true) {
                    Log.d("Create","Baza ni działa");
                }
                Toast.makeText(getApplicationContext(), "Mamy porblem", Toast.LENGTH_LONG).show();
                finish();
                System.exit(0);
            }
            if (config.debug==true) {
                Log.d("Create","Baza działa");
                Log.d("Create","Weryficaja _conf");
            }
            Cursor db_ver = sql.rawQuery("Select wartosc from _conf where klucz = "+String.valueOf((char)34)+"wersja_db"+String.valueOf((char)34)+";", null);
            Cursor apk_ver = sql.rawQuery("Select wartosc from _conf where klucz = "+String.valueOf((char)34)+"wersja_apk"+String.valueOf((char)34)+";", null);
            apk_ver.moveToFirst();
            db_ver.moveToFirst();
            String versja = apk_ver.getString(0);
            if (sharedPref.getString(APK_VERSION_KEY,"").equals("")) {
                if (config.debug==true) {
                    Log.d("Create","versja aplikacji nie zapisana (pierwsze uruchomienie)");
                }
                editor.putString(APK_VERSION_KEY,pinfo.versionName);
            }else if(sharedPref.getString(APK_VERSION_KEY,"").equals(pinfo.versionName)){
                if (config.debug==true) {
                    Log.d("Create","versja aplikacji aktualne");
                }
            }else
            {
                if (config.debug==true) {
                    Log.d("Create","versja aplikacji nieaktualne (był update)");
                }
                editor.putString(APK_VERSION_KEY,pinfo.versionName);
            }
            if (versja.equals("-1")){
                if (config.debug==true) {
                    Log.d("Create", "versja aplikacji nie zapisana w db (pierwsze uruchomienie)");
                }
                sql.execSQL("Update _conf set wartosc = "+String.valueOf((char)34)+pinfo.versionName+String.valueOf((char)34)+" where klucz = "+String.valueOf((char)34)+"wersja_apk"+String.valueOf((char)34)+";");
            }else if (versja.equals(pinfo.versionName)){
                if (config.debug==true) {
                    Log.d("Create", "versja aplikacji w db aktualna");
                }
            }else{
                if (config.debug==true) {
                    Log.d("Create", "versja aplikacji w db nie aktualna (był update)");
                }
                Log.d("sql","Update _conf set wartosc = "+String.valueOf((char)34)+pinfo.versionName+String.valueOf((char)34)+" where klucz = "+String.valueOf((char)34)+"wersja_apk"+String.valueOf((char)34)+";");
                sql.execSQL("Update _conf set wartosc = "+String.valueOf((char)34)+pinfo.versionName+String.valueOf((char)34)+" where klucz = "+String.valueOf((char)34)+"wersja_apk"+String.valueOf((char)34)+";");
            }
            versja=db_ver.getString(0);
            if (sharedPref.getString(DB_VERSION_KEY,"").equals("")){
                if (config.debug==true) {
                    Log.d("Create", "versja bazy danych nie istnieje (pierwsze uruchomienie)");
                }
                editor.putString(DB_VERSION_KEY,versja);
            }else{
                if (config.debug==true) {
                    Log.d("Create", "versja bazy danych: " + versja + " wersja z shared " + sharedPref.getString(DB_VERSION_KEY, ""));
                }
            }
            if (config.debug==true) {
                Log.d("Create", "aktualnie zainstalowana wersja db "+versja);
                Log.d("Create", "otwieram baze danych z resoiurces");
            }
            try {
                out = new FileOutputStream(DATABASE_PATH + DATABASE_NAME+".bak");
                byte[] buff = new byte[1024];
                int read = 0;
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            SQLiteDatabase sqtmp = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME+".bak", null, SQLiteDatabase.OPEN_READWRITE);
            db_ver = sqtmp.rawQuery("Select wartosc from _conf where klucz = "+String.valueOf((char)34)+"wersja_db"+String.valueOf((char)34)+";", null);
            db_ver.moveToFirst();
            String wer = db_ver.getString(0);
            if (!wer.equals(versja)){
                if (config.debug==true) {
                    Log.d("Create", "versja bazy danych nie jest aktualne (był update)");
                }
                sql.execSQL("Update _conf set wartosc = "+String.valueOf((char)34)+wer+String.valueOf((char)34)+" where klucz = "+String.valueOf((char)34)+"wersja_db"+String.valueOf((char)34)+";");
                editor.putString(DB_VERSION_KEY,wer);
            }else{
                if (config.debug==true) {
                    Log.d("Create", "versja bazy danych jest aktualna");
                }
            }
            if (config.debug==true) {
                Log.d("Create", "usuwanie tymczasowej bazy danych)");
            }
            new File(DATABASE_PATH + DATABASE_NAME+".bak").delete();
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (config.debug==true) {
                Cursor tmp = sql.rawQuery("Select wartosc from _conf;",null);
                tmp.moveToFirst();
                Toast.makeText(getApplicationContext(),"Witam w wersji DEBUG, ta wersja nie jest przezbaczona do urzytku pordukcyjnego, wersj: "+ pinfo.versionName+" wersja bazy danych to: "+tmp.getString(0)+" miłego debugu", Toast.LENGTH_LONG).show();
        }
        Cursor c = null;
        if (config.debug==true) {
            Log.d("Create","Inicjalizuje web view");
        }
        editor.commit();
        WebView web = (WebView) findViewById(R.id.strona);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setAllowContentAccess(true);
        if (config.debug==true) {
            web.setWebContentsDebuggingEnabled(true);
        }
        web.getSettings().setAllowFileAccessFromFileURLs(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.loadUrl("file:///android_res/raw/layout.html");//od tej linii łapy precz
        web.addJavascriptInterface(new WebAppInterface(this, sql), "Android");
        if (config.debug==true) {
            Log.d("Create","Koniec");
        }
    }
}
