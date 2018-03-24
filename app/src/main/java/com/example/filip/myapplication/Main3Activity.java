package com.example.filip.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

public class Main3Activity extends AppCompatActivity {
    String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().setTitle("Helfer Error reciver");
        Exception e = (Exception) getIntent().getExtras().getSerializable("exception");
        msg = Arrays.toString(e.getStackTrace());
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

                Runtime.getRuntime().exit(0);
            }
        });

            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main3Activity.this);
                alertDialog.setTitle("informacje");
                alertDialog.setMessage(msg);
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                    }
                });
                alertDialog.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", msg);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(),"skopiowano",Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.show();
            }
        });

    }
}
