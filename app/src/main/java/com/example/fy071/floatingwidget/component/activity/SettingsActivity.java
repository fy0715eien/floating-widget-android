package com.example.fy071.floatingwidget.component.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fy071.floatingwidget.R;
import com.example.fy071.floatingwidget.component.fragment.SettingsFragment;
import com.example.fy071.floatingwidget.util.PreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    SharedPreferences sharedPreferences;
    SharedPreferences defaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initToolbar();

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        PreferenceHelper.setPreferences(defaultSharedPreferences, sharedPreferences);
    }

    private void initToolbar() {
        toolbar.setTitle(R.string.drawer_item_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
