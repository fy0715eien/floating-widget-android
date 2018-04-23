package com.example.fy071.floatingwidget.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * 用于生成通知通道
 */
public class NotificationChannelsManager extends ContextWrapper {
    public static final String FOREGROUND_SERVICE_CHANNEL = "foreground_service";
    public static final String ALARM_CHANNEL = "alarm_channel";

    public NotificationChannelsManager(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generateChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        // 前台服务通道
        NotificationChannel notificationChannel = new NotificationChannel(
                FOREGROUND_SERVICE_CHANNEL,
                "Foreground service",
                NotificationManager.IMPORTANCE_MIN
        );
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        notificationManager.createNotificationChannel(notificationChannel);

        // 闹钟通道
        notificationChannel = new NotificationChannel(
                ALARM_CHANNEL,
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.YELLOW);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
