package com.d.lib.common.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.lang.reflect.Field;

/**
 * AbstractPopup
 * Created by D on 2017/4/29.
 */
public abstract class AbstractPopup extends PopupWindow implements View.OnKeyListener {

    /**
     * Must be Activity
     */
    protected Context mContext;
    protected View mRootView;

    private AbstractPopup() {
    }

    public AbstractPopup(Context context, @LayoutRes int resource) {
        this(context, resource, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true, 0);
    }

    public AbstractPopup(Context context, @LayoutRes int resource, int animationStyle) {
        this(context, resource, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true, animationStyle);
    }

    /**
     * Create a new popup window
     *
     * @param context        Context
     * @param resource       The popup's layout resource
     * @param width          The popup's width
     * @param height         The popup's height
     * @param focusable      True if the popup can be focused, false otherwise
     * @param animationStyle Animation style to use when the popup appears
     *                       and disappears.  Set to -1 for the default animation, 0 for no
     *                       animation, or a resource identifier for an explicit animation.
     */
    public AbstractPopup(Context context, @LayoutRes int resource, int width, int height, boolean focusable, int animationStyle) {
        super(LayoutInflater.from(context).inflate(resource, null), width, height, focusable);
        this.mContext = context;
        this.mRootView = getContentView();
        if (animationStyle != -1) {
            setAnimationStyle(animationStyle);
        }
        setOutsideTouchable(true);
        setFocusable(true);
        setClippingEnabled(false);
        fitPopupWindowOverStatusBar(true);
        setBackgroundDrawable(new BitmapDrawable());
        if (isInitEnabled()) {
            bindView(mRootView);
            init();
        }
    }

    protected void fitPopupWindowOverStatusBar(final boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(this, needFullScreen);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Show PopupWindow
     */
    public void show() {
        if (!isShowing() && mContext != null && !((Activity) mContext).isFinishing()) {
            final Activity activity = (Activity) mContext;
            final View parent = ((ViewGroup) activity.findViewById(android.R.id.content))
                    .getChildAt(0);
            showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * Dismiss PopupWindow
     */
    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
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

    protected boolean isInitEnabled() {
        return true;
    }

    protected void bindView(View rootView) {
    }

    protected abstract void init();
}
