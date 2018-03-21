package com.example.fy071.floatingwidget.component;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
    public static final long DRAWER_ABOUT = 3L;
    private static final String TAG = "MainActivity";
    long previousSelectedItem;

    Toolbar toolbar;
    Drawer drawer;
    HomeFragment homeFragment;
    SettingsFragment settingsFragment;

    SharedPreferences sharedPreferences;
    SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.toolbar);

        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();

        displayFragment(homeFragment, R.string.drawer_item_home, DRAWER_HOME);

        drawer=new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.layout_header)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(DRAWER_HOME)
                                .withIcon(R.drawable.ic_home_black_24dp)
                                .withName(R.string.drawer_item_home)
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
        } else {
            drawer.setSelection(DRAWER_HOME);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }
}