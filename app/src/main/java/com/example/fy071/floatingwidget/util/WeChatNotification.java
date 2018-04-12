package com.example.fy071.floatingwidget.util;


import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;


public class WeChatNotification extends NotificationListenerService {
    private static final String TAG = "WeChatNotification";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //不是微信的通知过滤掉
        if (!"com.tencent.mm".equals(sbn.getPackageName())) {
            return;
        }
        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        Bundle extras = notification.extras;
        if (extras != null) {
            //获取标题
            String title = extras.getString(Notification.EXTRA_TITLE, "");
            // 获取通知内容
            String content = extras.getString(Notification.EXTRA_TEXT, "");

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

}








