package com.example.fy071.floatingwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends AppCompatActivity implements
        Drawer.OnDrawerItemClickListener,
        Preference.OnPreferenceChangeListener {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 100;
    public static final String KEY_ENABLE_WIDGET ="enable_widget";
    private static final String TAG = "MainActivity";

    public static final long DRAWER_HOME = 200;
    public static final long DRAWER_SETTINGS = 201;
    public static final long DRAWER_ABOUT = 202;

    Toolbar toolbar;
    Drawer drawer;

    SharedPreferences sharedPreferences;
    boolean isPermissionGranted;
    long previousSelectedItem = DRAWER_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //传入设置碎片
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new HomeFragment())
                .commit();
        //工具栏设置标题
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        //建立抽屉
        drawer=new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(DRAWER_HOME)
                                .withIcon(R.drawable.ic_home_black_24dp)
                                .withName("Home"),
                        new PrimaryDrawerItem().withIdentifier(DRAWER_SETTINGS)
                                .withIcon(R.drawable.ic_settings_black_24dp)
                                .withName("Settings")
        )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(DRAWER_ABOUT)
                        .withIcon(R.drawable.ic_info_black_24dp)
                        .withName("About")
                )
                .withOnDrawerItemClickListener(this)
                .withActionBarDrawerToggle(true)
                .build();

        //为工具栏加入打开抽屉的按钮
        drawer.setToolbar(this,toolbar,true);

        //读取SharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(KEY_ENABLE_WIDGET, false)) {
            enableWidget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /*
    当权限不可用，Toast显示权限不可用
    权限可用则打开悬浮窗
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Log.d(TAG, "onActivityResult: " + isPermissionGranted);
                isPermissionGranted = false;
                Toast.makeText(this, "Permission not available", Toast.LENGTH_SHORT).show();
            } else {
                isPermissionGranted = true;
                startService(new Intent(MainActivity.this, FloatingViewService.class));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /*
    无权限，且系统高于6.0时
    启动权限设置Activity，返回后调用onActivityResult()

    有权限，或系统低于6.0时
    直接打开悬浮窗
     */
    private void enableWidget(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            Toast.makeText(this, "Please grant permission", Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            isPermissionGranted = true;
            startService(new Intent(MainActivity.this, FloatingViewService.class));
        }
    }


    /*
    * 抽屉项目监听器
     */
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        long id = drawerItem.getIdentifier();
        if (id == previousSelectedItem) {
            drawer.closeDrawer();
        } else {
            if (id == DRAWER_HOME) {
                toolbar.setTitle("Home");
                previousSelectedItem = DRAWER_HOME;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new HomeFragment())
                        .commit();
            }
            if (id == DRAWER_SETTINGS) {
                toolbar.setTitle("Settings");
                previousSelectedItem = DRAWER_SETTINGS;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new SettingsFragment())
                        .commit();
            }
            // TODO: 2018/3/10 aboutfragment
            drawer.closeDrawer();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Log.d(TAG, "onPreferenceChange: " + key);
        switch (key) {
            /*
            开关被设置为true时：
            若无权限则返回false，不修改SharedPreference
            若有权限则返回true，修改SharedPreference

            开关被设置为false时：
            停止服务
             */
            case KEY_ENABLE_WIDGET:
                Log.d(TAG, "onPreferenceChange: changed");
                if (newValue.equals(true)) {
                    enableWidget();
                    Log.d(TAG, "onPreferenceChange: " + isPermissionGranted);
                    return isPermissionGranted;
                } else {
                    stopService(new Intent(this, FloatingViewService.class));
                    return true;
                }
        }
        return false;
    }
}