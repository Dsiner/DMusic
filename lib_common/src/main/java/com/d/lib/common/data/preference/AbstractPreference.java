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

    protected SharedPreferences mSettings;
    protected SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    protected AbstractPreference(Context context) {
        mSettings = context.getApplicationContext().getSharedPreferences(PREFIX + getClass().getSimpleName(), 0);
        mEditor = mSettings.edit();
    }

    protected void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }

    protected void clearAllData() {
        mEditor.clear();
        save();
    }

    public void registerOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSettings.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSettings.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
