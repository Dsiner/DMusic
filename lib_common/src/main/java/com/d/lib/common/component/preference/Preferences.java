package com.d.lib.common.component.preference;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.component.preference.operation.OpOnline;
import com.d.lib.common.component.preference.operation.OpSetting;

/**
 * Preferences
 * Created by D on 2017/4/29.
 */
public class Preferences extends AbstractPreference {
    private volatile static Preferences instance;

    private OpSetting opSetting;
    private OpOnline opOnline;

    private Preferences(Context context) {
        super(context);
        initOps();
    }

    /**
     * Initialize operation handle
     */
    private void initOps() {
        opSetting = new OpSetting(settings, editor);
        opOnline = new OpOnline(settings, editor);
    }

    public static Preferences getIns(Context context) {
        if (instance == null) {
            synchronized (Preferences.class) {
                if (instance == null) {
                    instance = new Preferences(context);
                }
            }
        }
        return instance;
    }

    private void save() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public void clearUserData(String account) {
        if (!TextUtils.equals(account, getAccount())) {
            boolean isFirst = getIsFirst();
            clearAllData();
            putIsFirst(isFirst);
        }
    }

    public void clearAllData() {
        editor.clear();
        save();
    }

    /****************************** - Setting - ******************************/
    public OpSetting optSetting() {
        return opSetting;
    }

    /****************************** - Online - ******************************/
    public OpOnline optOnline() {
        return opOnline;
    }


    /************************* 是/否 第一次使用 *************************/
    public void putIsFirst(boolean isFirst) {
        editor.putBoolean(Keys.KEY_IS_FIRST, isFirst);
        save();
    }

    public boolean getIsFirst() {
        return settings.getBoolean(Keys.KEY_IS_FIRST, true);
    }

    /************************* 设置/获取 登录账号 *************************/
    public void putAccount(String account) {
        editor.putString(Keys.KEY_ACCOUNT, account);
        save();
    }

    public String getAccount() {
        return settings.getString(Keys.KEY_ACCOUNT, "");
    }

}
