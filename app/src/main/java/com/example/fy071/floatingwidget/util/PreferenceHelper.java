package com.example.fy071.floatingwidget.util;

import android.content.SharedPreferences;

public class PreferenceHelper {
    public static boolean widgetEnabled;
    public static String petName;
    public static String userName;
    public static boolean wechatNotification;
    public static boolean startAtBoot;


    public static void setPreferences(SharedPreferences sharedPreferences) {
        widgetEnabled = sharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);
        petName = sharedPreferences.getString(Key.PET_NAME, "default value");
        userName = sharedPreferences.getString(Key.USER_NAME, "default value");
        wechatNotification = sharedPreferences.getBoolean(Key.WECHAT_NOTIFICATION, false);
        startAtBoot = sharedPreferences.getBoolean(Key.START_AT_BOOT, false);
    }
}
