package com.example.filip.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class Main3Activity extends AppCompatActivity {
    String msg;
    String addmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().setTitle("Helfer Error reciver");
        Exception e = (Exception) getIntent().getExtras().getSerializable("exception");
        msg = Arrays.toString(e.getStackTrace());
        msg += " msg = " + e.getMessage();
        addmsg = getIntent().getExtras().getString("add_info");
        ((TextView) findViewById(R.id.textView4)).setText(e.getMessage());
        if (addmsg.equals("")) {
            addmsg = "brak dodatkowych informacji";
        }
        ((TextView) findViewById(R.id.textView5)).setText(addmsg);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                Runtime.getRuntime().exit(0);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main3Activity.this);
                alertDialog.setTitle("Czy wysłać?");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNeutralButton("Wyślij", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{config.email});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "We found a bug");
                        intent.putExtra(Intent.EXTRA_TEXT, "Dear Developer" + String.valueOf((char) 13) + String.valueOf((char) 10) + "In my android " + android.os.Build.VERSION.SDK_INT + " phone Your application catch unhandled exeption. This is Stack trace and message:" + String.valueOf((char) 13) + String.valueOf((char) 10) + msg + String.valueOf((char) 13) + String.valueOf((char) 10) + "But this is additional message = " + addmsg + String.valueOf((char) 13) + String.valueOf((char) 10) + "may be you will be intrested about my log cat: " + String.valueOf((char) 13) + String.valueOf((char) 10) + utils.logcat);
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                });
                alertDialog.show();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main3Activity.this);
                alertDialog.setTitle("informacje");
                alertDialog.setMessage(msg + String.valueOf((char) 13) + String.valueOf((char) 10) + "log cat :" + utils.logcat);
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", msg + String.valueOf((char) 13) + String.valueOf((char) 10) + "log cat :" + utils.logcat);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "skopiowano", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.show();
            }
        });

    }
}
