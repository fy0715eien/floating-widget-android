package com.example.fy071.floatingwidget.util;

import android.content.SharedPreferences;

public class PreferenceHelper {
    public static SharedPreferences defaultSharedPreferences;
    public static SharedPreferences sharedPreferences;

    //设置界面
    public static boolean widgetEnabled;
    public static String petName;
    public static String userName;
    public static String petModel;
    public static boolean wechatNotification;
    public static boolean startOnBoot;
    public static boolean randomDialog;

    //其他
    public static float petLastX;
    public static float petLastY;

    public static void setPreferences(SharedPreferences dSP, SharedPreferences sP) {
        defaultSharedPreferences = dSP;
        sharedPreferences = sP;

        widgetEnabled = defaultSharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);
        petName = defaultSharedPreferences.getString(Key.PET_NAME, "");
        userName = defaultSharedPreferences.getString(Key.USER_NAME, "");
        petModel = defaultSharedPreferences.getString(Key.PET_MODEL, "");
        wechatNotification = defaultSharedPreferences.getBoolean(Key.WECHAT_NOTIFICATION, false);
        startOnBoot = defaultSharedPreferences.getBoolean(Key.START_AT_BOOT, false);
        startOnBoot = defaultSharedPreferences.getBoolean(Key.RANDOM_DIALOG, false);


        petLastX = sharedPreferences.getFloat(Key.PET_LAST_X, 0);
        petLastY = sharedPreferences.getFloat(Key.PET_LAST_Y, 0);
    }
}
