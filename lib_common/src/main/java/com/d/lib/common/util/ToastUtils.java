package com.d.lib.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtils {

    private ToastUtils() {
    }

    /**
     * Toast tips
     *
     * @param context Context
     * @param msg     Message
     */
    public static void toast(Context context, String msg) {
        if (context == null || TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast tips
     *
     * @param context Context
     * @param msg     Message
     */
    public static void toastLong(Context context, String msg) {
        if (context == null || TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Toast tips
     *
     * @param context Context
     * @param resId   Resource id
     */
    public static void toast(Context context, int resId) {
        if (context == null) {
            return;
        }
        toast(context, context.getString(resId));
    }
}
