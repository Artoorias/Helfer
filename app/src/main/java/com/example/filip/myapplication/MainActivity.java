package com.example.filip.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    static SQLiteDatabase sql = null;
    static PackageInfo pinfo;
    static ProgressDialog dialog;
    static Handler mhandler;
    static String em = "";
    android.support.v7.app.AlertDialog al;
    android.support.v7.app.AlertDialog.Builder alertDialog;

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
            InitRunable.cnt = MainActivity.this;
            mhandler = new Handler();
            getSupportActionBar().hide();
            dialog = new ProgressDialog(MainActivity.this);
            utils.show_debug_message("create", "Rozpoczynam");
            utils.show_debug_message("create", "Progress dialog");
            if (!check_net()) {
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
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        while (true) {
                            try {
                                if (check_net()) {
                                    al.cancel();
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
                new Thread(new InitRunable()).start();
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
        utils.show_debug_message("Create", "Koniec");
    }
}
