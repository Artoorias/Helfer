package com.example.filip.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView web = (WebView) findViewById(R.id.strona);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl("file:///android_res/raw/abc.html");
        web.addJavascriptInterface(new WebAppInterface(this),"Android");


    }
}
