package com.example.fy071.floatingwidget.component;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.*;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;



public class MainActivity extends AppCompatActivity implements
        Drawer.OnDrawerItemClickListener {
    private static final String TAG = "MainActivity";

    public static final long DRAWER_HOME = 200;
    public static final long DRAWER_SETTINGS = 201;
    public static final long DRAWER_ABOUT = 202;
    long previousSelectedItem = DRAWER_HOME;

    Toolbar toolbar;
    Drawer drawer;

    SharedPreferences sharedPreferences;

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
        PreferenceHelper.setPreferences(sharedPreferences);
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
                displayFragment(new HomeFragment());
            }
            if (id == DRAWER_SETTINGS) {
                toolbar.setTitle("Settings");
                previousSelectedItem = DRAWER_SETTINGS;
                displayFragment(new SettingsFragment());
            }
            // TODO: 2018/3/10 aboutfragment
            return true;
        }
        return false;
    }


    public void displayFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
        drawer.closeDrawer();
    }



    @Override
    public void onBackPressed() {
        if (previousSelectedItem == DRAWER_HOME) {
            super.onBackPressed();
        } else {
            displayFragment(new HomeFragment());
            toolbar.setTitle("Home");
            previousSelectedItem = DRAWER_HOME;
            drawer.setSelection(DRAWER_HOME);
        }
    }
}