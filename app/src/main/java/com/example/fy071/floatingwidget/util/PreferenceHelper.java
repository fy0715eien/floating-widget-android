package com.example.fy071.floatingwidget.util;

import android.content.SharedPreferences;

public class PreferenceHelper {
    public static SharedPreferences defaultSharedPreferences;

    //设置界面
    public static boolean widgetEnabled;
    public static String petName;
    public static String userName;
    public static String petModel;
    public static boolean weChatNotification;
    public static boolean startOnBoot;
    public static boolean randomDialog;

    public static void setPreferences(SharedPreferences dSP) {
        defaultSharedPreferences = dSP;

        widgetEnabled = defaultSharedPreferences.getBoolean(Key.ENABLE_WIDGET, false);

        petName = defaultSharedPreferences.getString(Key.PET_NAME, "");
        userName = defaultSharedPreferences.getString(Key.USER_NAME, "");
        petModel = defaultSharedPreferences.getString(Key.PET_MODEL, "");

        weChatNotification = defaultSharedPreferences.getBoolean(Key.WECHAT_NOTIFICATION, false);
        startOnBoot = defaultSharedPreferences.getBoolean(Key.START_AT_BOOT, false);
        randomDialog = defaultSharedPreferences.getBoolean(Key.RANDOM_DIALOG, false);
    }
}
