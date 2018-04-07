package com.example.fy071.floatingwidget.component;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.PreferenceHelper;
import com.example.fy071.floatingwidget.util.ToastUtil;
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
   Timer timer;
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
 //随机话语，每30s出现一次

        timer.schedule(new TimerTask(){
            public void run(){
               Randomdialog();
                timer.cancel();
            }
        },0, 30*1000);

        Intent startIntent=new Intent(this,WeChatNotifacation.class);
        startService(startIntent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, FloatingViewService.class);
        stopService(intent);
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
        } else {
            drawer.setSelection(DRAWER_HOME);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }


    //用户离开当前应用时开启Service
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Intent intent = new Intent(this, FloatingViewService.class);
        if (PreferenceHelper.widgetEnabled) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    private void Randomdialog() {
        String[] dialog = {"幸福就是猫吃鱼，狗吃肉，奥特曼打小怪兽", "每天午睡15到30分钟对身体很有好处，你要注意多休息哦", "主人,让我给你跳个舞好不好呀,吃饱了不运动很容易长胖的呢",
                "主人,我已经下定决心跟你过一辈子了,让我多陪陪你吧", "人是铁,饭是钢,一顿不吃饿的慌,主人,按时吃饭了吗", "我是很笨，但是我很认真，幻想和主人过一生！"
                , "多多微笑，阴天谨防情绪感冒！", "生命在于运动，主人你今天运动了吗", "我现在浑身充满力量，要不要跟我拔河", "踮起脚尖我们就能离幸福更近一点哦",
                "你说小白兔吃胡萝卜，是因为它买不起肉么", "他们都说我丑，其实我只是美得不明显", "我昨晚可是想了你好多次呢,抱抱嘛",
                "刚出炉的面包不宜马上食用哦，因为酵母还没有完全消失", "得之泰然，失之淡然，争其必然，顺气自然，我觉得这句话很有道理耶", "保护生命之水，需要从节约用水做起，主人你说对吗？",
                "酸梅具有减缓老化的作用，我要多吃点，保持青春永驻!", "红灯停，绿灯行，不论是开车还是走路，我们都要遵守交通规则哦", "主人，生活这么美好，我们是不是该微笑面对每一天呢", "人家都说饭后吃水果，其实饭前一小时吃有利于人体免疫系统哦"};
        ToastUtil newtoast = new ToastUtil();
        newtoast.setToastColor(Color.WHITE, Color.YELLOW);
        int min = 0;
        int max = 19;
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        //获取到宠物位置后再改变位置
        newtoast.toast.setGravity(Gravity.LEFT, 50, 0);
        newtoast.toast.makeText(MainActivity.this, dialog[num], Toast.LENGTH_LONG).show();
    }



    private void test() {
        Intent intent = new Intent(this, ReminderConfigActivity.class);
        startActivity(intent);
    }







}

//wechatotification.xml:
//<?xml version="1.0" encoding="utf-8"?>
//<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
//    android:accessibilityEventTypes="typeAllMask"
//    android:accessibilityFeedbackType="feedbackGeneric"
//    android:accessibilityFlags="flagDefault"
//    android:canRetrieveWindowContent="true"
//    android:notificationTimeout="100"
//    android:packageNames="com.tencent.mm" />