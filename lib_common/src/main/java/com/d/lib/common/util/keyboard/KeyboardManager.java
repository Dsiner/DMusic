package com.d.lib.common.util.keyboard;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;

import com.d.lib.common.util.ScreenUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.util.log.ULog;

/**
 * 软键盘管理
 * Created by D on 2017/8/16.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class KeyboardManager extends AbsKeyboardManager implements View.OnLayoutChangeListener {
    private int mKeyHeight; // 软键盘弹起后所占高度阀值

    public KeyboardManager(Activity activity, EditText commonInput) {
        super(commonInput);
        this.mKeyHeight = ScreenUtils.getScreenSize(activity)[1] / 3;
        ViewHelper.getActivityRoot(activity).addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > mKeyHeight)) {
            ULog.d("dsiner-onLayoutChangeP:" + "soft keyboard pop...");
            if (mListener != null) {
                mListener.onPop();
            }
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > mKeyHeight)) {
            ULog.d("dsiner-onLayoutChangeP:" + "soft keyboard close...");
            if (mListener != null) {
                mListener.onClose();
            }
        }
    }
}
