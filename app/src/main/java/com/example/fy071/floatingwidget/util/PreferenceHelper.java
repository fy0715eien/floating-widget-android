package com.example.fy071.floatingwidget.util;

import android.content.SharedPreferences;

public class PreferenceHelper {
    //设置界面
    public static boolean widgetEnabled;
    public static String petName;
    public static String userName;
    public static boolean wechatNotification;
    public static boolean startAtBoot;

    //其他
    public static float petLastX;
    public static float petLastY;

    public static void setPreferences(SharedPreferences defaultSharedPreferences, SharedPreferences sharedPreferences) {
        widgetEnabled = defaultSharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);
        petName = defaultSharedPreferences.getString(Key.PET_NAME, "default value");
        userName = defaultSharedPreferences.getString(Key.USER_NAME, "default value");
        wechatNotification = defaultSharedPreferences.getBoolean(Key.WECHAT_NOTIFICATION, false);
        startAtBoot = defaultSharedPreferences.getBoolean(Key.START_AT_BOOT, false);

        petLastX = sharedPreferences.getFloat(Key.PET_LAST_X, 0);
        petLastY = sharedPreferences.getFloat(Key.PET_LAST_Y, 0);
    }
}
