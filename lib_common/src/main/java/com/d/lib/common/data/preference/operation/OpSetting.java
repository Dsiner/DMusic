package com.d.lib.common.data.preference.operation;

import android.content.SharedPreferences;

import com.d.lib.common.data.preference.Keys;

/**
 * Setting Operation
 * Created by D on 2018/3/14.
 */
public class OpSetting extends AbstractOp {

    public OpSetting(SharedPreferences settings, SharedPreferences.Editor editor) {
        super(settings, editor);
    }

    /************************* 是/否 自动升级 *************************/
    public void putIsAutoUpdate(boolean auto) {
        mEditor.putBoolean(Keys.KEY_IS_AUTO_UPDATE, auto);
        save();
    }

    public boolean getIsAutoUpdate() {
        return mSettings.getBoolean(Keys.KEY_IS_AUTO_UPDATE, false);
    }
}
