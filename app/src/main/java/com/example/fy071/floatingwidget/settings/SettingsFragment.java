package com.example.fy071.floatingwidget.settings;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";

    public static final int DRAW_OVER_OTHER_APP_PERMISSION = 0;
    public static final int NOTIFICATION_ACCESS_PERMISSION = 1;

    SwitchPreference switchPreference;
    EditTextPreference petName;
    EditTextPreference userName;
    ListPreference petModel;
    CheckBoxPreference weChatNotification;
    CheckBoxPreference startAtBoot;
    CheckBoxPreference randomDialog;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        switchPreference = (SwitchPreference) findPreference(Key.ENABLE_WIDGET);
        petName = (EditTextPreference) findPreference(Key.PET_NAME);
        userName = (EditTextPreference) findPreference(Key.USER_NAME);
        petModel = (ListPreference) findPreference(Key.PET_MODEL);
        weChatNotification = (CheckBoxPreference) findPreference(Key.WECHAT_NOTIFICATION);
        startAtBoot = (CheckBoxPreference) findPreference(Key.START_AT_BOOT);
        randomDialog = (CheckBoxPreference) findPreference(Key.RANDOM_DIALOG);

        switchPreference.setOnPreferenceChangeListener(this);
        petName.setOnPreferenceChangeListener(this);
        userName.setOnPreferenceChangeListener(this);
        petModel.setOnPreferenceChangeListener(this);
        weChatNotification.setOnPreferenceChangeListener(this);

        //设置Summary以显示上次设置内容
        petName.setSummary(PreferenceHelper.petName);
        userName.setSummary(PreferenceHelper.userName);
        Log.d(TAG, "onCreate: " + petModel.getEntry());
        petModel.setSummary(petModel.getEntry());

        if (!PreferenceHelper.widgetEnabled) {
            resetWidgetFunctions();
        }
    }

    private boolean isOverlayEnabled() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(getActivity());
    }

    private void checkPermission() {
        if (!isOverlayEnabled()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_title_permission_overlay)
                    .setMessage(R.string.dialog_message_permission_overlay)
                    .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
                            switchPreference.setChecked(false);
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            @SuppressLint("InlinedApi") Intent intent = new Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getActivity().getPackageName())
                            );
                            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, Object newValue) {
        final String key = preference.getKey();
        switch (key) {
            //启用Enable widget开关时
            //如果系统版本高于6.0且无权限，弹窗要求获取权限，根据权限获得情况决定是否保存该改变
            //如果系统版本低于6.0，则保存该开关改变
            case Key.ENABLE_WIDGET:
                if (newValue.equals(true)) {
                    checkPermission();
                } else {
                    resetWidgetFunctions();
                }
                break;
            case Key.PET_NAME:
            case Key.USER_NAME:
                preference.setSummary(newValue.toString());
                break;
            case Key.PET_MODEL:
                //选中选项时设置Summary
                ListPreference listPreference = (ListPreference) preference;
                CharSequence[] entries = listPreference.getEntries();
                int index = listPreference.findIndexOfValue(newValue.toString());
                preference.setSummary(entries[index]);
            case Key.WECHAT_NOTIFICATION:
                if (newValue.equals(true)) {
                    if (!isNotificationListenerEnabled()) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.dialog_title_permission_notification)
                                .setMessage(R.string.dialog_message_permission_notification)
                                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openNotificationListenSettings();
                                    }
                                })
                                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        weChatNotification.setChecked(false);
                                    }
                                })
                                .show();
                    }
                }
            default:
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DRAW_OVER_OTHER_APP_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
                    Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
                    switchPreference.setChecked(false);
                }
                break;
            case NOTIFICATION_ACCESS_PERMISSION:
                if (!isNotificationListenerEnabled()) {
                    Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
                    weChatNotification.setChecked(false);
                }
        }
    }

    private void resetWidgetFunctions() {
        weChatNotification.setChecked(false);
        startAtBoot.setChecked(false);
        randomDialog.setChecked(false);
    }

    private boolean isNotificationListenerEnabled() {
        boolean enable = false;
        String packageName = getActivity().getPackageName();
        String flat = Settings.Secure.getString(getActivity().getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            enable = flat.contains(packageName);
        }
        return enable;
    }

    private void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivityForResult(intent, NOTIFICATION_ACCESS_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}