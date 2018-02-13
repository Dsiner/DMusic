package com.d.commen.utils.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.widget.EditText;

/**
 * 软键盘管理+
 * Created by D on 2017/8/16.
 */
public class KeyboardPlusManager extends AbsKeyboardManager {

    public KeyboardPlusManager(Activity activity, EditText commenInput) {
        super(commenInput);
        init(activity);
    }

    private void init(Activity activity) {
        KeyboardHelper.setOnKeyboardEventListener(activity, new KeyboardHelper.OnKeyboardEventListener() {
            @Override
            public void onScroll(Rect rect) {
                if (listener != null) {
                    listener.onScroll(rect);
                }
            }

            @Override
            public void onPop() {
                if (listener != null) {
                    listener.onPop();
                }
            }

            @Override
            public void onClose() {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });
    }
}
