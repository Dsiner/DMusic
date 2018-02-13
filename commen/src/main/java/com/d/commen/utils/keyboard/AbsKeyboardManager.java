package com.d.commen.utils.keyboard;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * AbsKeyboardManager
 * Created by D on 2017/9/6.
 */
public abstract class AbsKeyboardManager {
    protected EditText commenInput;
    protected KeyboardHelper.OnKeyboardEventListener listener;

    public AbsKeyboardManager(EditText commenInput) {
        this.commenInput = commenInput;
    }

    /**
     * 弹起键盘输入
     */
    public void requestEdit(boolean delay) {
        KeyboardHelper.showKeyboard(commenInput, delay);
    }

    /**
     * 取消EditText焦点
     *
     * @param close:是否关闭软键盘
     */
    public void dismissEdit(boolean close) {
        if (close) {
            KeyboardHelper.hideKeyboard(commenInput);
        }
        commenInput.clearFocus();
    }

    /**
     * 手动滚动底部EditText，以避免被软键盘遮盖
     *
     * @param rect:       rect
     * @param scrollRoot: 目标EditText or 父布局 的父布局
     * @param scroll:     目标EditText or 父布局
     */
    public static void onScroll(Rect rect, ViewGroup scrollRoot, View scroll) {
        if (scrollRoot == null || scroll == null) {
            return;
        }
        int[] location = new int[2];
        scroll.getLocationInWindow(location);
        int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
        if (srollHeight > 0) {
            scrollRoot.scrollTo(0, srollHeight);
        }
    }

    public void setOnKeyboardEventListener(KeyboardHelper.OnKeyboardEventListener l) {
        this.listener = l;
    }
}
