package com.example.fy071.floatingwidget.component.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.service.notification.NotificationListenerService;

import java.util.List;

public class NotificationListenerMonitorService extends Service {
    private static final String TAG = "MonitorService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ensureCollectorRunning();
        return START_STICKY;
    }

    private void ensureCollectorRunning() {
        ComponentName componentName = new ComponentName(this, WeChatNotificationListenerService.class);

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        boolean collectorRunning = false;

        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            return;
        }

        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(componentName)) {
                if (service.pid == Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }

        if (collectorRunning) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 24) {
            NotificationListenerService.requestRebind(componentName);
        } else {
            toggleNotificationListenerService(componentName);
        }
    }

    private void toggleNotificationListenerService(ComponentName componentName) {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
