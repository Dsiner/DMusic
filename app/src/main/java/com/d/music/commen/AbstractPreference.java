package com.d.music.commen;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * AbstractPreference
 * Created by D on 2017/4/29.
 */
public class AbstractPreference {
    protected Context context;
    protected SharedPreferences settings;
    protected SharedPreferences.Editor editor;

    public AbstractPreference(Context context) {
        this.context = context.getApplicationContext();
        settings = context.getSharedPreferences(getClass().getSimpleName(), 0);
        editor = settings.edit();
    }

    public void registerOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
