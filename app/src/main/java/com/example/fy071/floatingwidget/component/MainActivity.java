package com.example.fy071.floatingwidget.component;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.PreferenceHelper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends AppCompatActivity implements
        Drawer.OnDrawerItemClickListener,
        Drawer.OnDrawerListener {
    public static final long DRAWER_HOME = 1L;
    public static final long DRAWER_REMINDER = 2L;
    public static final long DRAWER_SETTINGS = 3L;
    public static final long DRAWER_ABOUT = 4L;
    private static final String TAG = "MainActivity";
    long previousSelectedItem;
    Intent intent;

    Toolbar toolbar;
    Drawer drawer;
    HomeFragment homeFragment;
    SettingsFragment settingsFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    //无法switch(long)故使用if else
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        final long id = drawerItem.getIdentifier();
        if (id == DRAWER_HOME) {
            intent = null;
        } else if (id == DRAWER_REMINDER) {
            intent=new Intent(MainActivity.this,ReminderConfigActivity.class);
            // TODO: 2018/4/8 change activity to ReminderActivity after it's written completed
        } else if (id == DRAWER_SETTINGS) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        } else if (id == DRAWER_ABOUT) {
            //intent=new Intent(MainActivity.this,AboutActivity.class);
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
            startWidget();
        }
    }


    //用户离开当前应用时(点击Home键或多任务键)开启Service
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        startWidget();
    }


    /*Enable Widget开关开启时调用本方法启动Service
    关闭时调用本方法不开启
     */
    private void startWidget() {
        Intent intent = new Intent(this, FloatingViewService.class);
        if (PreferenceHelper.widgetEnabled) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }
}