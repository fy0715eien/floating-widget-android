package com.example.fy071.floatingwidget.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

    public class OpenBroadcastReceiver extends BroadcastReceiver {
        static final String ACTION = "android.intent.action.BOOT_COMPLETED";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)){
                Intent openActivity=new Intent(context,MainActivity.class);
                openActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openActivity);
            }
        }
    }
