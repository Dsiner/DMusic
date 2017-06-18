package com.d.music.view.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * AbstractDialog
 * Created by D on 2017/4/29.
 */
public abstract class AbstractPopup implements View.OnKeyListener {
    protected Context context;//must be Activity
    protected PopupWindow popupWindow;
    protected View rootView;

    public AbstractPopup(Context context) {
        this(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true, -1);
    }

    public AbstractPopup(Context context, int animationStyle) {
        this(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true, animationStyle);
    }

    public AbstractPopup(Context context, int width, int height, boolean focusable, int animationStyle) {
        this.context = context;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = LayoutInflater.from(context).inflate(getLayoutRes(), null);
        popupWindow = new PopupWindow(rootView, width, height, focusable);
        if (animationStyle != -1) {
            popupWindow.setAnimationStyle(animationStyle);
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        init();
    }

    /**
     * 显示popupWindow
     */
    public void show() {
        if (popupWindow != null && !popupWindow.isShowing() && context != null && !((Activity) context).isFinishing()) {
            popupWindow.showAsDropDown(rootView);
        }
    }

    /**
     * 隐藏popupWindow
     */
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return false;
    }

    protected abstract int getLayoutRes();

    protected abstract void init();
}
