package com.example.fy071.floatingwidget.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.service.NotificationListenerMonitorService;
import com.example.fy071.floatingwidget.component.service.RandomDialogService;
import com.example.fy071.floatingwidget.component.service.WeChatNotificationListenerService;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements Drawer.OnDrawerItemClickListener, Drawer.OnDrawerListener {
    public static final long DRAWER_HOME = 1L;
    public static final long DRAWER_REMINDER = 2L;
    public static final long DRAWER_PAIRING = 3L;
    public static final long DRAWER_SETTINGS = 4L;
    public static final long DRAWER_ABOUT = 5L;

    private static final String TAG = "MainActivity";

    private Intent intent;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.drawer_item_home);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.layout_header)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_HOME)
                                .withIcon(R.drawable.ic_home_black_24dp)
                                .withName(R.string.drawer_item_home),
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_REMINDER)
                                .withIcon(R.drawable.ic_alarm_black_24dp)
                                .withName(R.string.drawer_item_reminder)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_PAIRING)
                                .withIcon(R.drawable.ic_bluetooth_black_24dp)
                                .withName(R.string.drawer_item_pairing)
                                .withSelectable(false)
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_SETTINGS)
                                .withIcon(R.drawable.ic_settings_black_24dp)
                                .withName(R.string.drawer_item_settings)
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withIcon(R.drawable.ic_info_black_24dp)
                                .withName(R.string.drawer_item_about)
                                .withSelectable(false)
                )
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .withActionBarDrawerToggle(true)
                .withCloseOnClick(true)
                .build();

        //为工具栏加入打开抽屉的按钮
        drawer.setToolbar(this, toolbar, true);

        Intent serviceIntent = new Intent(this, RandomDialogService.class);
        startService(serviceIntent);

        serviceIntent = new Intent(this, NotificationListenerMonitorService.class);
        startService(serviceIntent);

        serviceIntent = new Intent(this, WeChatNotificationListenerService.class);
        startService(serviceIntent);

        if (!notificationListenerEnable()) {
            openNotificationListenSettings();
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        final long id = drawerItem.getIdentifier();
        if (id == DRAWER_HOME) {
            intent = null;
        } else if (id == DRAWER_REMINDER) {
            intent = new Intent(MainActivity.this, ReminderConfigActivity.class);
            // TODO: 2018/4/8 change activity to ReminderActivity after it's completely written
        } else if (id == DRAWER_PAIRING) {
            intent = new Intent(MainActivity.this, PairingActivity.class);
        } else if (id == DRAWER_SETTINGS) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        } else if (id == DRAWER_ABOUT) {
            new LibsBuilder()
                    .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                    .withActivityTitle(getResources().getString(R.string.drawer_item_about))
                    .start(this);
        } else {
            intent = null;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            intent = null;
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        //required override method
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (intent != null) {
            startActivity(intent);
            intent = null;
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        //required ride method
    }

    public boolean notificationListenerEnable() {
        boolean enable = false;
        String packageName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            enable = flat.contains(packageName);
        }
        return enable;
    }

    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
