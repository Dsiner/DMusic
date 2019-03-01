package com.d.lib.common.data.data;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.data.preference.AbstractPreference;
import com.d.lib.common.utils.Util;

public class UserData extends AbstractPreference {
    private static UserData instance = null;

    private UserBean userBean;

    private interface Keys {
        String KEY_USER_JSON = "key_user_json";
    }

    private UserData(Context context) {
        super(context);
        getUserBean();
    }

    public static UserData getIns(Context context) {
        return instance == null ? (instance = new UserData(context)) : instance;
    }

    public UserBean getUserBean() {
        if (userBean == null) {
            String json = mSettings.getString(Keys.KEY_USER_JSON, "");
            if (!TextUtils.isEmpty(json)) {
                userBean = Util.getGsonIns().fromJson(json, UserBean.class);
            } else {
                userBean = new UserBean();
            }
        }
        return userBean;
    }

    public void saveUserBean(UserBean bean) {
        if (bean == null) {
            return;
        }
        userBean = bean;
        mEditor.putString(Keys.KEY_USER_JSON, Util.getGsonIns().toJson(bean));
        save();
    }

    public void saveUserBean() {
        mEditor.putString(Keys.KEY_USER_JSON, Util.getGsonIns().toJson(userBean));
        save();
    }

    public String getUserId() {
        UserBean userBean = getUserBean();
        return userBean != null ? userBean.user_id : "";
    }

    public String getNickname() {
        UserBean userBean = getUserBean();
        return userBean != null ? userBean.nickname : "";
    }
}
