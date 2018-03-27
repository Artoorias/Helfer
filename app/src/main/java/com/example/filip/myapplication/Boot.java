package com.example.filip.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Tomek on 18.03.2018.
 */
public class Boot extends BroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent it = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        Calendar calendar = Calendar.getInstance();
        MainActivity.show_debug_message("BOOT","woda_1 prepare");
        alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), MainActivity.WODA_1, it, 0);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        MainActivity.show_debug_message("BOOT","woda_2 prepare");
        alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), MainActivity.WODA_2, it, 0);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()) {
            calendar.add(Calendar.DATE,1);
        }
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}