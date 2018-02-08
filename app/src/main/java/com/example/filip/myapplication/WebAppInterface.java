package com.example.filip.myapplication;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kuba on 23.01.2018.
 */

public class WebAppInterface {
    Context mContext;
    Map<Integer, Integer> mapa = new HashMap<Integer,Integer>();
    int b=1;
    int c=1;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void silnia() {
        b = b * c;
        Toast.makeText(mContext, "" + b, Toast.LENGTH_LONG).show();
        c=c+1;

    }
}