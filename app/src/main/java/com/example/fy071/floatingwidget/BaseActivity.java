package com.example.fy071.floatingwidget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.fy071.floatingwidget.util.PreferenceHelper;

public class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences defaultSharedPreferences;

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceHelper.setPreferences(defaultSharedPreferences);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences);
    }

}
