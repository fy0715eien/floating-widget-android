package com.example.fy071.floatingwidget.reminder;

/**
 * Created by Administrator on 2018/4/24.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationClickReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "You click a notification", Toast.LENGTH_LONG).show();
    }
}