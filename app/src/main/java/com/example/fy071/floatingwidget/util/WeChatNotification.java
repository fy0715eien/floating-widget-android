package com.example.fy071.floatingwidget.util;


import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.fy071.floatingwidget.component.service.FloatingViewService;


public class WeChatNotification extends NotificationListenerService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ComponentName componentName = new ComponentName(this, FloatingViewService.class);
        if (Build.VERSION.SDK_INT >= 24) {
            requestRebind(componentName);
        } else {
            toggleNotificationListenerService(componentName);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void toggleNotificationListenerService(ComponentName componentName) {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private static final String TAG = "WeChatNotification";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted: " + getPackageName());
        if (!"com.tencent.mm".equals(sbn.getPackageName())) {
            return;
        } //不是微信的通知过滤掉
        Notification notification = sbn.getNotification();
        Log.d(TAG, "onNotificationPosted: " + notification);
        if (notification == null) {
            return;
        }
        Bundle extras = notification.extras;
        Log.d(TAG, "onNotificationPosted: " + extras);

        if (extras != null) {
            //获取标题
            String title = extras.getString(Notification.EXTRA_TITLE, "");
            // 获取通知内容
            String content = extras.getString(Notification.EXTRA_TEXT, "");
            Log.d(TAG, "onNotificationPosted: " + title);
            Log.d(TAG, "onNotificationPosted: " + content);
            System.out.print(content);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

}








