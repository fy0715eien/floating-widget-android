package com.example.fy071.floatingwidget;


import android.os.Bundle;
import android.preference.PreferenceFragment;


public class ConfigFragment extends PreferenceFragment {
    private static final String TAG = "ConfigFragment";

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
