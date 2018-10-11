package com.d.lib.common.component.preference.operation;

import android.content.SharedPreferences;

/**
 * Abstract Operation
 * Created by D on 2018/3/14.
 */
public abstract class AbstractOp {
    protected SharedPreferences settings;
    protected SharedPreferences.Editor editor;

    public AbstractOp(SharedPreferences settings, SharedPreferences.Editor editor) {
        this.settings = settings;
        this.editor = editor;
    }

    protected void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
