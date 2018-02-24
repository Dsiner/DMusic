package com.d.lib.common.utils.keyboard;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.ViewHelper;
import com.d.lib.common.utils.log.ULog;

/**
 * 软键盘管理
 * Created by D on 2017/8/16.
 */
public class KeyboardManager extends AbsKeyboardManager implements View.OnLayoutChangeListener {
    private int keyHeight;//软键盘弹起后所占高度阀值

    public KeyboardManager(Activity activity, EditText commonInput) {
        super(commonInput);
        this.keyHeight = Util.getScreenSize(activity)[1] / 3;
        ViewHelper.getActivityRoot(activity).addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            ULog.d("dsiner-onLayoutChangeP:" + "soft keyboard pop...");
            if (listener != null) {
                listener.onPop();
            }
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            ULog.d("dsiner-onLayoutChangeP:" + "soft keyboard close...");
            if (listener != null) {
                listener.onClose();
            }
        }
    }
}
