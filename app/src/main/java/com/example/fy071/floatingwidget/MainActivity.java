package com.example.fy071.floatingwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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
        Drawer.OnDrawerItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 100;
    public static final String KEY_ENABLE_WIDGET ="enable_widget";
    private static final String TAG = "MainActivity";

    public static final int DRAWER_SETTINGS=200;
    public static final int DRAWER_ABOUT=201;

    Toolbar toolbar;
    Drawer drawer;

    SharedPreferences sharedPreferences;
    boolean isWidgetEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UI
        setContentView(R.layout.activity_main);
        //传入设置碎片
        getFragmentManager().beginTransaction()
                .replace(R.id.content,new ConfigFragment())
                .commit();
        //工具栏设置标题
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        //建立抽屉
        drawer=new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
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
        sharedPreferences=getPreferences(MODE_PRIVATE);
        isWidgetEnabled =sharedPreferences.getBoolean(KEY_ENABLE_WIDGET,false);
        Log.d(TAG, "onCreate: "+isWidgetEnabled);
        if(isWidgetEnabled){
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
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    /*
    当权限不可用，Toast显示权限不可用
    权限可用则打开悬浮窗
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission not available", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /*
    系统高于6.0时启动权限设置Activity，返回后调用onActivityResult()
    低于6.0时直接打开悬浮窗
     */
    private void enableWidget(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            startService(new Intent(MainActivity.this, FloatingViewService.class));
        }
    }


    /*
    * 抽屉项目监听器
     */
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        // TODO: 2018/3/9 add listener
        return false;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(TAG, "onSharedPreferenceChanged: "+s);
        if(s.equals(KEY_ENABLE_WIDGET)){
            Log.d(TAG, "onSharedPreferenceChanged: checkbox changed");
            boolean isWidgetEnabled=sharedPreferences.getBoolean(MainActivity.KEY_ENABLE_WIDGET,false);
            if(isWidgetEnabled){
                startService(new Intent(this,FloatingViewService.class));
            }
            stopService(new Intent(this,FloatingViewService.class));
        }
    }
}