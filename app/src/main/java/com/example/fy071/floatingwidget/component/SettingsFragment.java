package com.example.fy071.floatingwidget.component;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        DialogInterface.OnClickListener {
    private static final String TAG = "SettingsFragment";
    SwitchPreference switchPreference;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        switchPreference = (SwitchPreference) findPreference(Key.ENABLE_WIDGET);
        switchPreference.setOnPreferenceChangeListener(this);
    }


    /*
    启用Enable widget开关时
    如果系统版本高于6.0且无权限，弹窗要求权限获取
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        switch (key) {
            case Key.ENABLE_WIDGET:
                if (newValue.equals(true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Special permission required")
                                .setMessage("Please grant permission for this app in the next page")
                                .setNegativeButton("Cancel", this)
                                .setPositiveButton("OK", this)
                                .show();
                    } else {
                        return true;
                    }
                }
                break;
            default:
        }
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (PreferenceHelper.widgetEnabled) {
            getActivity().startService(new Intent(getActivity(), FloatingViewService.class));
        }
    }

    /*
    对话框按钮监听器
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_NEGATIVE) {
            Toast.makeText(getActivity(), "Permission not available", Toast.LENGTH_SHORT).show();
            switchPreference.setChecked(false);
            dialog.cancel();
        } else if (which == BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
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