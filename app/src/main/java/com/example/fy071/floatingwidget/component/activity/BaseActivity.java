package com.example.fy071.floatingwidget.component.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.example.fy071.floatingwidget.component.service.FloatingViewService;
import com.example.fy071.floatingwidget.component.service.NotificationListenerMonitorService;
import com.example.fy071.floatingwidget.component.service.RandomDialogService;
import com.example.fy071.floatingwidget.component.service.WeChatNotificationListenerService;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

public class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;
    SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startServices();
    }

    //用户离开当前应用时(点击Home键或多任务键)开启Service
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        startServices();
    }

    //根据偏好开启或关闭特定Service
    private void startServices() {
        Intent intent = new Intent(this, FloatingViewService.class);
        if (PreferenceHelper.widgetEnabled) {
            if ((Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this))
                    || Build.VERSION.SDK_INT < 23) {
                startService(intent);
            }
        } else {
            stopService(intent);
        }

        intent = new Intent(this, NotificationListenerMonitorService.class);
        // Enable时启动“保证通知监听服务开启”的服务
        if (PreferenceHelper.wechatNotification) {
            startService(intent);
            // Disable时将两个服务都停止
        } else {
            stopService(intent);
            intent = new Intent(this, WeChatNotificationListenerService.class);
            stopService(intent);
        }

        intent = new Intent(this, RandomDialogService.class);
        if (PreferenceHelper.randomDialog) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }
}
