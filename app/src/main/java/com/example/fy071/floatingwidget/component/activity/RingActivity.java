package com.example.fy071.floatingwidget.component.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fy071.floatingwidget.R;

/**
 * Created by Administrator on 2018/4/18.
 */

public class RingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String channelId = generateChannelId(this);
        Intent intent=getIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId);
        builder.setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("content"))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);
        NotificationManager notificationManager=(NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
    private String generateChannelId(Context context) {
        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "alarm_service";
            String channelName = this.getResources().getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
            );
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            channelId = "";
        }
        return channelId;
    }
}
