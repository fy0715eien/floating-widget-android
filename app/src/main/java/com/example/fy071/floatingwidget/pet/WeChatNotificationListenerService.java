package com.example.fy071.floatingwidget.pet;


import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.fy071.floatingwidget.util.Key;

public class WeChatNotificationListenerService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!"com.tencent.mm".equals(sbn.getPackageName())) {
            return;
        } //不是微信的通知过滤掉
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
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Key.WECHAT_NOTIFICATION, false)) {
                Intent intent = new Intent();
                //与清单文件的receiver的anction对应
                intent.setAction("com.tofloatingpet.message");
                intent.putExtra("content", content);
                //发送广播
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

}








