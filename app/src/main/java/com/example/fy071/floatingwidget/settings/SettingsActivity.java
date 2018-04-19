package com.example.fy071.floatingwidget.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fy071.floatingwidget.BaseActivity;
import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.pet.NotificationListenerMonitorService;
import com.example.fy071.floatingwidget.pet.RandomDialogService;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        initToolbar();

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startServices();
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.drawer_item_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        Intent intent = new Intent(this, NotificationListenerMonitorService.class);
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
