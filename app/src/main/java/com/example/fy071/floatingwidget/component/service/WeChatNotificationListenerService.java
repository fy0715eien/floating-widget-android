package com.example.fy071.floatingwidget.component.service;


import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class WeChatNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "WeChatService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.w(TAG, "onNotificationPosted: " + getPackageName());
        if (!"com.tencent.mm".equals(sbn.getPackageName())) {
            return;
        } //不是微信的通知过滤掉
        Notification notification = sbn.getNotification();
        Log.w(TAG, "onNotificationPosted: " + notification);
        if (notification == null) {
            return;
        }
        Bundle extras = notification.extras;
        Log.w(TAG, "onNotificationPosted: " + extras);

        if (extras != null) {
            //获取标题
            String title = extras.getString(Notification.EXTRA_TITLE, "");
            // 获取通知内容
            String content = extras.getString(Notification.EXTRA_TEXT, "");
            Log.w(TAG, "onNotificationPosted: " + title);
            Log.w(TAG, "onNotificationPosted: " + content);
            System.out.print(content);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

}








