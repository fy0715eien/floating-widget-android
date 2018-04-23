package com.example.fy071.floatingwidget.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.NotificationChannelsManager;

/**
 * Created by Administrator on 2018/4/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: received");
            /*Log.d(TAG, "onReceive: received app broadcast");
            Log.i("test","闹钟响了");
            //跳转到Activity n //广播接受者中（跳转Activity）
            Intent intent1=new Intent(context,RingActivity.class);
            //给Intent设置标志位
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("content"))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationChannelsManager.ALARM_CHANNEL);
        }

        notificationManager.notify(12345, builder.build());
    }


}