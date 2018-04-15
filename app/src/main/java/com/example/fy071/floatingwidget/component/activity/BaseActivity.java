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
import com.example.fy071.floatingwidget.util.PreferenceHelper;

public class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences defaultSharedPreferences;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceHelper.setPreferences(defaultSharedPreferences);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences);
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
        if (PreferenceHelper.weChatNotification) {
            startService(intent);
        } else {
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
