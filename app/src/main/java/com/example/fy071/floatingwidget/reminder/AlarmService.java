package com.example.fy071.floatingwidget.reminder;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    AlarmReceiver alarmReceiver = null;

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        registerReceiver(alarmReceiver, intentFilter);
        Log.d(TAG, "onStartCommand: called");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(alarmReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
