package com.d.lib.common.data.data;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.data.preference.AbstractPreference;
import com.d.lib.common.util.GsonUtils;

public class UserData extends AbstractPreference {
    private volatile static UserData INSTANCE = null;

    private UserBean mUserBean;

    private interface Keys {
        String KEY_USER_JSON = "key_user_json";
    }

    private UserData(Context context) {
        super(context, "UserData");
        getUserBean();
    }

    public static UserData getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserData.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserData(context);
                }
            }
        }
        return INSTANCE;
    }

    public UserBean getUserBean() {
        if (mUserBean == null) {
            String json = mSettings.getString(Keys.KEY_USER_JSON, "");
            if (!TextUtils.isEmpty(json)) {
                mUserBean = GsonUtils.getInstance().fromJson(json, UserBean.class);
            } else {
                mUserBean = new UserBean();
            }
        }
        return mUserBean;
    }

    public void saveUserBean(UserBean bean) {
        if (bean == null) {
            return;
        }
        mUserBean = bean;
        mEditor.putString(Keys.KEY_USER_JSON, GsonUtils.getInstance().toJson(bean));
        save();
    }

    public void saveUserBean() {
        mEditor.putString(Keys.KEY_USER_JSON, GsonUtils.getInstance().toJson(mUserBean));
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
