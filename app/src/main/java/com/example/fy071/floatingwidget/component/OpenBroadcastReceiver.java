package com.example.fy071.floatingwidget.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.fy071.floatingwidget.component.activity.MainActivity;
import com.example.fy071.floatingwidget.component.service.FloatingViewService;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

public class OpenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean widgetEnabled = sharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);
        boolean startOnBoot = sharedPreferences.getBoolean(Key.START_AT_BOOT, false);
        if (widgetEnabled && startOnBoot) {
            Intent openService = new Intent(context, FloatingViewService.class);
            context.startService(openService);
        }
    }
}