package com.d.lib.common.data.preference.operation;

import android.content.SharedPreferences;

/**
 * Abstract Operation
 * Created by D on 2018/3/14.
 */
public abstract class AbstractOp {
    protected SharedPreferences mSettings;
    protected SharedPreferences.Editor mEditor;

    public AbstractOp(SharedPreferences settings, SharedPreferences.Editor editor) {
        this.mSettings = settings;
        this.mEditor = editor;
    }

    protected void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            mEditor.commit();
        }
    }
}
