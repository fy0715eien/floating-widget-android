package com.example.fy071.floatingwidget.component;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.util.Key;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        DialogInterface.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";
    SharedPreferences sharedPreferences;
    SwitchPreference switchPreference;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        switchPreference = (SwitchPreference) findPreference(Key.ENABLE_WIDGET);
        switchPreference.setOnPreferenceChangeListener(this);
    }


    /*
    当第一个开关被改变，检查系统版本和权限

     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + preference.getKey());
        if (preference.getKey().equals(Key.ENABLE_WIDGET)) {
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferenceHelper.setPreferences(sharedPreferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}