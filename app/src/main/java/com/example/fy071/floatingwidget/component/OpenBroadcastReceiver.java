package com.example.fy071.floatingwidget.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.fy071.floatingwidget.component.activity.MainActivity;
import com.example.fy071.floatingwidget.component.service.FloatingViewService;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

public class OpenBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "OpenBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean widgetEnabled = sharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);
        boolean startOnBoot = sharedPreferences.getBoolean(Key.START_AT_BOOT, false);
        Log.d(TAG, "onReceive: "+widgetEnabled+startOnBoot);
        if (widgetEnabled && startOnBoot) {
            Intent openService = new Intent(context, FloatingViewService.class);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                context.startForegroundService(openService);
            }else{
                context.startService(openService);
            }
        }
    }
}