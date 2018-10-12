package com.d.lib.common.data.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.d.lib.common.data.Constants;

/**
 * AbstractPreference
 * Created by D on 2017/4/29.
 */
public abstract class AbstractPreference {
    private final static String PREFIX = Constants.PREFIX;

    protected SharedPreferences settings;
    protected SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    protected AbstractPreference(Context context) {
        settings = context.getApplicationContext().getSharedPreferences(PREFIX + getClass().getSimpleName(), 0);
        editor = settings.edit();
    }

    protected void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    protected void clearAllData() {
        editor.clear();
        save();
    }

    public void registerOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settings.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
