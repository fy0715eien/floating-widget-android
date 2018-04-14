package com.example.fy071.floatingwidget.component.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.fy071.floatingwidget.component.service.FloatingViewService;
import com.example.fy071.floatingwidget.util.Key;

public class OpenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean startOnBoot = sharedPreferences.getBoolean(Key.START_AT_BOOT, false);

        if (startOnBoot) {
            Intent openService = new Intent(context, FloatingViewService.class);
            //Android 8.0加入后台进程限制必须启动前台服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(openService);
            } else {
                context.startService(openService);
            }
        }
    }
}