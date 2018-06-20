package com.example.filip.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    static SQLiteDatabase sql = null;
    static PackageInfo pinfo;
    static ProgressDialog dialog;
    static Handler mhandler;
    static String em = "";
    static sl listener = null;
    android.support.v7.app.AlertDialog al;
    android.support.v7.app.AlertDialog.Builder alertDialog;

    //on stop vacuum database
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

    //on close close database
    protected void onDestroy() {
        if (listener != null) {
            SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorMgr.unregisterListener(listener);
        }
        if (config.debug == true) {
            Log.d("destroy", "Niszczenie");
        }
        //if db open close
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
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            WebView web = (WebView) findViewById(R.id.strona);
            web.loadUrl("javascript:window.back();");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {


            setContentView(R.layout.activity_main);
            InitRunable.cnt = MainActivity.this;
            mhandler = new Handler();
            getSupportActionBar().hide();
            dialog = new ProgressDialog(MainActivity.this);
            utils.show_debug_message("create", "Rozpoczynam");
            //if net is continue
            if (!utils.check_net(getApplicationContext())) {
                //no net
                utils.show_debug_message("create", "Netu nima");
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
                        startActivity(intent);
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
                //create task to checking net witch 5 s interval
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        while (true) {
                            try {
                                if (utils.check_net(getApplicationContext())) {
                                    //is net => close
                                    al.cancel();
                                    //run init thread
                                    new Thread(new InitRunable()).start();
                                    this.cancel(true);
                                    return null;
                                } else {
                                    Thread.sleep(5000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.execute();
            } else {
                //run init thread
                new Thread(new InitRunable()).start();
            }
        } catch (Exception e) {
            // jak cos sie zchrzani wyswietl informacje
            Intent err = new Intent(getApplicationContext(), Main3Activity.class);
            err.putExtra("exception", (Serializable) e);
            err.putExtra("add_info", "Brak dodatkowych informacji");
            startActivity(err);
            finish();
            e.printStackTrace();
            return;
        }
        utils.show_debug_message("Create", "Koniec");
    }
}
