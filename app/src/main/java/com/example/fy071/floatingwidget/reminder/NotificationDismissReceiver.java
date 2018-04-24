package com.example.fy071.floatingwidget.reminder;

/**
 * Created by Administrator on 2018/4/24.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fy071.floatingwidget.reminder.database.DbManager;

public class NotificationDismissReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationDismissReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        DbManager dbManager=new DbManager(context);
        dbManager.delete(intent.getIntExtra("id",0));
    }
}