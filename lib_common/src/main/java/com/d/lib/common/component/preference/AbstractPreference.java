package com.d.lib.common.component.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * AbstractPreference
 * Created by D on 2017/4/29.
 */
public class AbstractPreference {
    private final static String PREFIX = "lib_common_";

    protected SharedPreferences settings;
    protected SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public AbstractPreference(Context context) {
        settings = context.getApplicationContext().getSharedPreferences(PREFIX + getClass().getSimpleName(), 0);
        editor = settings.edit();
    }

    public void registerOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
