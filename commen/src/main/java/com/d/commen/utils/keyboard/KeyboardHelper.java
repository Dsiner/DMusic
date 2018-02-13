package com.d.commen.utils.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.d.commen.utils.Util;
import com.d.commen.utils.ViewHelper;

/**
 * KeyboardHelper
 * https://github.com/yshrsmz/KeyboardVisibilityEvent/blob/master/keyboardvisibilityevent/src/main/java/net/yslibrary/android/keyboardvisibilityevent/KeyboardVisibilityEvent.java
 */
public class KeyboardHelper {
    /**
     * 显示软键盘的延迟时间
     */
    public static final int SHOW_KEYBOARD_DELAY_TIME = 200;
    private static final String TAG = "QMUIKeyboardHelper";
    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;

    /**
     * 针对给定的editText显示软键盘（editText会先获得焦点）. 可以和{@link #hideKeyboard(View)}
     * 搭配使用，进行键盘的显示隐藏控制。
     */
    public static void showKeyboard(final EditText editText, boolean delay) {
        if (editText == null) {
            return;
        }
        editText.requestFocus();
//        if (!editText.requestFocus()) {
//            Log.w(TAG, "showSoftInput() can not get focus");
//            return;
//        }
        if (delay) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//                    imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.SHOW_FORCED);
                }
            }, SHOW_KEYBOARD_DELAY_TIME);
        } else {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//            imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 隐藏软键盘 可以和{@link #showKeyboard(EditText, boolean)}搭配使用，进行键盘的显示隐藏控制。
     *
     * @param view 当前页面上任意一个可用的view
     */
    public static boolean hideKeyboard(final View view) {
        if (null == view) {
            return false;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 即使当前焦点不在editText，也是可以隐藏的。
//        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Set keyboard visibility change event listener.
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     */
    @SuppressWarnings("deprecation")
    public static void setOnKeyboardEventListener(final Activity activity, final OnKeyboardEventListener listener) {
        if (activity == null) {
            throw new NullPointerException("Parameter:activity must not be null");
        }
        if (listener == null) {
            throw new NullPointerException("Parameter:listener must not be null");
        }
        final View activityRoot = ViewHelper.getActivityRoot(activity);
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();
            private final int visibleThreshold = Math.round(Util.dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));
            private boolean wasOpened = false;

            @Override
            public void onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r);
                listener.onScroll(r);
                int heightDiff = activityRoot.getRootView().getHeight() - r.height();
                boolean isOpen = heightDiff > visibleThreshold;
                if (isOpen == wasOpened) {
                    //keyboard state has not changed
                    return;
                }
                wasOpened = isOpen;
                if (wasOpened) {
                    listener.onPop();
                } else {
                    listener.onClose();
                }
            }
        };
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            activity.getApplication()
                    .registerActivityLifecycleCallbacks(new QMUIActivityLifecycleCallbacks(activity) {
                        @Override
                        protected void onTargetActivityDestroyed() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                activityRoot.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                            } else {
                                activityRoot.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                            }
                        }
                    });
        }
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public static boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();
        View activityRoot = ViewHelper.getActivityRoot(activity);
        int visibleThreshold = Math.round(Util.dip2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));
        activityRoot.getWindowVisibleDisplayFrame(r);
        int heightDiff = activityRoot.getRootView().getHeight() - r.height();
        return heightDiff > visibleThreshold;
    }

    public abstract static class OnKeyboardEventListener {
        public void onScroll(Rect rect) {

        }

        /**
         * soft keyboard pop...
         */
        public void onPop() {

        }

        /**
         * soft keyboard close...
         */
        public void onClose() {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    static abstract class QMUIActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        private final Activity mTargetActivity;

        QMUIActivityLifecycleCallbacks(Activity targetActivity) {
            mTargetActivity = targetActivity;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity == mTargetActivity) {
                mTargetActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
                onTargetActivityDestroyed();
            }
        }

        protected abstract void onTargetActivityDestroyed();
    }
}