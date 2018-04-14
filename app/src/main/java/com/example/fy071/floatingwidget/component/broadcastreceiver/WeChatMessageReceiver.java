package com.example.fy071.floatingwidget.component.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Vector;

/**
 * Created by Administrator on 2018/4/15.
 */

public class WeChatMessageReceiver extends BroadcastReceiver {
    public Vector<String> message;

    public WeChatMessageReceiver() {
        message=new Vector<>();
    }

    @Override
    public void onReceive(Context context, Intent intent1) {
        message.add(intent1.getStringExtra("content"));
    }
}