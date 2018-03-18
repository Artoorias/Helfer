package com.example.filip.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Tomek on 18.03.2018.
 */
public class Boot extends BroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        alarmMgr = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent it = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 123123123, it, 0);
        int i = 60;
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), (i * 1000), alarmIntent);
        Toast.makeText(context, "Starting alarm in " + i + " seconds",Toast.LENGTH_LONG).show();
    }

}