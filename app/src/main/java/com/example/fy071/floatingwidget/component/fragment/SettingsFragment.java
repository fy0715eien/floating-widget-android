package com.example.fy071.floatingwidget.component.fragment;


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

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class SettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        DialogInterface.OnClickListener {
    private static final String TAG = "SettingsFragment";
    SwitchPreference switchPreference;
    EditTextPreference petName, userName;
    ListPreference petModel;
    CheckBoxPreference wechatNotification, startAtBoot;

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
        wechatNotification = (CheckBoxPreference) findPreference(Key.WECHAT_NOTIFICATION);

        switchPreference.setOnPreferenceChangeListener(this);
        petName.setOnPreferenceChangeListener(this);
        userName.setOnPreferenceChangeListener(this);
        petModel.setOnPreferenceChangeListener(this);
        wechatNotification.setOnPreferenceChangeListener(this);

        //设置Summary以显示上次设置内容
        petName.setSummary(PreferenceHelper.petName);
        userName.setSummary(PreferenceHelper.userName);
        Log.d(TAG, "onCreate: " + petModel.getEntry());
        petModel.setSummary(petModel.getEntry());
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Special permission required")
                    .setMessage("Please grant permission for this app in the next page")
                    .setNegativeButton("Cancel", this)
                    .setPositiveButton("OK", this)
                    .show();
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        switch (key) {
            //启用Enable widget开关时
            //如果系统版本高于6.0且无权限，弹窗要求获取权限，根据权限获得情况决定是否保存该改变
            //如果系统版本低于6.0，则保存该开关改变
            case Key.ENABLE_WIDGET:
                if (newValue.equals(true)) {
                    checkPermission();
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
                    // TODO: 2018/3/26 check permission & require permission
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
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_NEGATIVE) {
            Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
            switchPreference.setChecked(false);
            dialog.cancel();
        } else if (which == BUTTON_POSITIVE) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName())
            );
            startActivityForResult(intent, Key.DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Key.DRAW_OVER_OTHER_APP_PERMISSION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
                    Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
                    switchPreference.setChecked(false);
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}