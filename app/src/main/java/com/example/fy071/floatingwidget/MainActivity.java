package com.example.fy071.floatingwidget;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.fy071.floatingwidget.bluetooth.PairingActivity;
import com.example.fy071.floatingwidget.pet.FloatingViewService;
import com.example.fy071.floatingwidget.reminder.ReminderListActivity;
import com.example.fy071.floatingwidget.settings.SettingsActivity;
import com.example.fy071.floatingwidget.util.NotificationChannelsManager;
import com.example.fy071.floatingwidget.util.PreferenceHelper;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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

    @OnClick(R.id.toggle_pet)
    void togglePet() {
        Intent intent = new Intent(this, FloatingViewService.class);
        if (PreferenceHelper.widgetEnabled) {
            if ((Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this))
                    || Build.VERSION.SDK_INT < 23) {
                startService(intent);
            }
        } else {
            Toast.makeText(this, "Please enable widget in settings", Toast.LENGTH_SHORT).show();
            stopService(intent);
        }
    }

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

        // 创建通知通道
        new NotificationChannelsManager(this);
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        final long id = drawerItem.getIdentifier();
        if (id == DRAWER_HOME) {
            intent = null;
        } else if (id == DRAWER_REMINDER) {
            intent = new Intent(MainActivity.this, ReminderListActivity.class);
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
}
