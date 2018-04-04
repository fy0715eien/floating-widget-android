package com.example.fy071.floatingwidget.component;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final long DRAWER_HOME = 1L;
    public static final long DRAWER_SETTINGS = 2L;
    public static final long DRAWER_REMINDER = 3L;
    public static final long DRAWER_ABOUT = 4L;
    private static final String TAG = "MainActivity";
    long previousSelectedItem;
    private static String startFragment;

    Toolbar toolbar;
    Drawer drawer;
    HomeFragment homeFragment;
    SettingsFragment settingsFragment;

    SharedPreferences sharedPreferences;
    SharedPreferences defaultSharedPreferences;

    public static void startThis(Context context, String fragment) {
        Intent intent = new Intent(context, MainActivity.class);
        startFragment = fragment;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);

        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();


        drawer=new DrawerBuilder()
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
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_SETTINGS)
                                .withIcon(R.drawable.ic_settings_black_24dp)
                                .withName(R.string.drawer_item_settings),
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_ABOUT)
                                .withIcon(R.drawable.ic_info_black_24dp)
                                .withName(R.string.drawer_item_about)
                )
                .withOnDrawerItemClickListener(this)
                .withActionBarDrawerToggle(true)
                .build();

        //为工具栏加入打开抽屉的按钮
        drawer.setToolbar(this,toolbar,true);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
        test();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //以静态变量设置活动开启时的碎片
    private void setStartSelection() {
        if (startFragment == null) {
            displayFragment(homeFragment, R.string.drawer_item_home, DRAWER_HOME);
        } else if (startFragment.equals("ReminderFragment")) {
            drawer.setSelection(DRAWER_REMINDER);
        } else if (startFragment.equals("SettingsFragment")) {
            drawer.setStickyFooterSelection(DRAWER_SETTINGS, true);
        }
        startFragment = null;
    }


    @Override
    protected void onPause() {
        super.onPause();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    //无法switch(long)故使用if else
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        final long id = drawerItem.getIdentifier();
        if (id == previousSelectedItem) {
            drawer.closeDrawer();
        } else {
            if (id == DRAWER_HOME) {
                displayFragment(homeFragment, R.string.drawer_item_home, id);
            } else if (id == DRAWER_REMINDER) {
                //displayFragment(reminderFragment, R.string.drawer_item_home, id);
            } else if (id == DRAWER_SETTINGS) {
                displayFragment(settingsFragment, R.string.drawer_item_settings, id);
            } else if (id == DRAWER_ABOUT) {
                //displayFragment(aboutFragment,R.string.drawer_item_about,id);
            }
            drawer.closeDrawer();
            return true;
        }
        return false;
    }

    /**
     * 点击抽屉项目时的所有操作
     *
     * @param fragment 将要显示的fragment
     * @param titleStringId 将要显示的工具栏标题
     * @param identifier 抽屉id
     */
    public void displayFragment(Fragment fragment, int titleStringId, long identifier) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
        toolbar.setTitle(titleStringId);
        previousSelectedItem = identifier;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (previousSelectedItem == DRAWER_HOME) {
            super.onBackPressed();
            startWidget();
        } else {
            drawer.setSelection(DRAWER_HOME);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
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


    //用于测试其他Activity
    private void test() {
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, FloatingViewService.class);
        stopService(intent);
        setStartSelection();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
}