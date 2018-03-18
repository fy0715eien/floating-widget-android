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
        Drawer.OnDrawerItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final long DRAWER_HOME = 200;
    public static final long DRAWER_SETTINGS = 201;
    public static final long DRAWER_ABOUT = 202;
    private static final String TAG = "MainActivity";
    long previousSelectedItem = DRAWER_HOME;

    Toolbar toolbar;
    Drawer drawer;

    SharedPreferences defaultSharedPreferences;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //工具栏
        toolbar=findViewById(R.id.toolbar);

        //传入碎片
        displayFragment(new HomeFragment(), R.string.drawer_item_home);

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
                .withDelayDrawerClickEvent(300)
                .withActionBarDrawerToggle(true)
                .build();

        //为工具栏加入打开抽屉的按钮
        drawer.setToolbar(this,toolbar,true);

        //读取SharedPreference
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
    }


    @Override
    protected void onPause() {
        super.onPause();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        long id = drawerItem.getIdentifier();
        if (id == previousSelectedItem) {
            drawer.closeDrawer();
        } else {
            if (id == DRAWER_HOME) {
                previousSelectedItem = DRAWER_HOME;
                displayFragment(new HomeFragment(), R.string.drawer_item_home);
            }
            if (id == DRAWER_SETTINGS) {
                previousSelectedItem = DRAWER_SETTINGS;
                displayFragment(new SettingsFragment(), R.string.drawer_item_settings);
            }
            // TODO: 2018/3/10 aboutfragment
            return true;
        }
        return false;
    }

    /**
     * 点击抽屉项目时的所有操作
     *
     * @param fragment      将要显示的fragment
     * @param titleStringId 将要显示的工具栏标题
     */
    public void displayFragment(Fragment fragment, int titleStringId) {
        toolbar.setTitle(titleStringId);
        getFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (previousSelectedItem == DRAWER_HOME) {
            super.onBackPressed();
        } else {
            displayFragment(new HomeFragment(), R.string.drawer_item_home);
            previousSelectedItem = DRAWER_HOME;
            drawer.setSelection(DRAWER_HOME);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences defaultSharedPreferences, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }
}