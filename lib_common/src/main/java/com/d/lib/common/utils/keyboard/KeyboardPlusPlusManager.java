package com.d.lib.common.utils.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.d.lib.common.utils.Util;

/**
 * 软键盘管理++
 * manifest设置activity属性: android:windowSoftInputMode="adjustNothing"
 * Created by D on 2017/8/16.
 */
public class KeyboardPlusPlusManager extends AbsKeyboardManager {
    private KeyboardPopup popup;

    public KeyboardPlusPlusManager(Activity activity, EditText commonInput) {
        super(commonInput);
        init(activity);
    }

    private void init(Activity activity) {
        popup = new KeyboardPopup(activity);
        popup.show();
    }

    @Override
    public void setOnKeyboardEventListener(KeyboardHelper.OnKeyboardEventListener l) {
        super.setOnKeyboardEventListener(l);
        popup.setOnKeyboardEventListener(l);
    }

    @Deprecated
    public static void onScroll(Activity activity, Rect rect, FrameLayout scrollRoot, View scroll) {
        Rect r = new Rect();
        //需要动态调整的View在屏幕中的位置
        scroll.getGlobalVisibleRect(r);

        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        //键盘高度 = 屏幕高度 - popWindow的高度 （需要设置 showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);）
        int heightDiff = metric.heightPixels - rect.bottom;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) scroll.getLayoutParams();
        //计算需要的偏移量
        int offset = heightDiff - (activity.getWindowManager().getDefaultDisplay().getHeight() - r.bottom);
        if (heightDiff == 0) {
            params.bottomMargin = 0;
        } else {
            params.bottomMargin = offset;
        }
        //通过设置View 的bottomMargin改变其位置
        scroll.setLayoutParams(params);
    }

    static class KeyboardPopup extends PopupWindow {
        private Activity activity;
        private View parentView, popupView;
        protected KeyboardHelper.OnKeyboardEventListener listener;

        KeyboardPopup(final Activity activity) {
            super(activity);
            this.activity = activity;
            this.parentView = activity.findViewById(android.R.id.content);
            this.popupView = new FrameLayout(activity);
            this.popupView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            setContentView(popupView);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//为了当键盘变化时调整PopWindow的大小
            setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            setWidth(0);
            setHeight(WindowManager.LayoutParams.MATCH_PARENT);

            final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                private DisplayMetrics metric = new DisplayMetrics();
                private final Rect r = new Rect();
                private final int visibleThreshold = Math.round(Util.dip2px(activity, KeyboardHelper.KEYBOARD_VISIBLE_THRESHOLD_DP));
                private boolean wasOpened = false;

                @Override
                public void onGlobalLayout() {
                    popupView.getWindowVisibleDisplayFrame(r);
                    if (listener != null) {
                        listener.onScroll(r);
                    }
                    activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
                    //键盘高度 = 屏幕高度 - popWindow的高度 （需要设置 showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);）
                    int heightDiff = metric.heightPixels - r.bottom;
                    boolean isOpen = heightDiff > visibleThreshold;
                    if (isOpen == wasOpened) {
                        //keyboard state has not changed
                        return;
                    }
                    wasOpened = isOpen;
                    if (wasOpened) {
                        if (listener != null) {
                            listener.onPop();
                        }
                    } else {
                        if (listener != null) {
                            listener.onClose();
                        }
                    }
                }
            };
            popupView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                activity.getApplication()
                        .registerActivityLifecycleCallbacks(new KeyboardHelper.QMUIActivityLifecycleCallbacks(activity) {
                            @Override
                            protected void onTargetActivityDestroyed() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    popupView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                                } else {
                                    popupView.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                                }
                            }
                        });
            }
        }

        public void show() {
            if (!isShowing() && activity != null && !activity.isFinishing()) {
                showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
            }
        }

        void setOnKeyboardEventListener(KeyboardHelper.OnKeyboardEventListener l) {
            this.listener = l;
        }
    }
}
