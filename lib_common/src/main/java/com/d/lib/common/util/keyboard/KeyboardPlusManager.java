package com.d.lib.common.util.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.widget.EditText;

/**
 * 软键盘管理+
 * Created by D on 2017/8/16.
 */
public class KeyboardPlusManager extends AbsKeyboardManager {

    public KeyboardPlusManager(Activity activity, EditText commonInput) {
        super(commonInput);
        init(activity);
    }

    private void init(Activity activity) {
        KeyboardHelper.setOnKeyboardEventListener(activity, new KeyboardHelper.OnKeyboardEventListener() {
            @Override
            public void onScroll(Rect rect) {
                if (mListener != null) {
                    mListener.onScroll(rect);
                }
            }

            @Override
            public void onPop() {
                if (mListener != null) {
                    mListener.onPop();
                }
            }

            @Override
            public void onClose() {
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });
    }
}
