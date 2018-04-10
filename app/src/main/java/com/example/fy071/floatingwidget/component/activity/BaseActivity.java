package com.example.fy071.floatingwidget.component.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.service.FloatingViewService;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import static com.example.fy071.floatingwidget.util.PreferenceHelper.defaultSharedPreferences;
import static com.example.fy071.floatingwidget.util.PreferenceHelper.sharedPreferences;

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
        startWidget();
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
        startWidget();
    }

    //用户离开当前应用时(点击Home键或多任务键)开启Service
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        startWidget();
    }

    //Enable Widget开关开启时调用本方法启动Service
    //关闭时调用本方法不开启
    private void startWidget() {
        Intent intent = new Intent(this, FloatingViewService.class);
        if (PreferenceHelper.widgetEnabled) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }
}
