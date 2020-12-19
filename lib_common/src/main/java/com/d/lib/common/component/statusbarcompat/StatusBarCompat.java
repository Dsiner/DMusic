package com.d.lib.common.component.statusbarcompat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.d.lib.common.R;
import com.d.lib.common.util.ConvertUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Utils for status bar
 * Created by qiu on 3/29/16.
 */
public class StatusBarCompat {
    public static final int STYLE_MAIN = 0;
    public static final int STYLE_WHITE = 1;

    @IntDef({STYLE_MAIN, STYLE_WHITE})
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusBarStyle {

    }

    //Get alpha color
    static int calculateStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * set statusBarColor
     *
     * @param statusColor color
     * @param alpha       0 - 255
     */
    public static void setStatusBarColor(@NonNull Activity activity, @ColorInt int statusColor, int alpha) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
    }

    public static void setStatusBarColor(@NonNull Activity activity, @ColorInt int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColor(activity, statusColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor);
        }
    }

    public static void translucentStatusBar(@NonNull Activity activity) {
        translucentStatusBar(activity, false);
    }

    /**
     * change to full screen mode
     *
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    public static void translucentStatusBar(@NonNull Activity activity, boolean hideStatusBarBackground) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.translucentStatusBar(activity, hideStatusBarBackground);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.translucentStatusBar(activity);
        }
    }

    public static void setStatusBarColorForCollapsingToolbar(@NonNull Activity activity, AppBarLayout appBarLayout, CollapsingToolbarLayout collapsingToolbarLayout,
                                                             Toolbar toolbar, @ColorInt int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompatLollipop.setStatusBarColorForCollapsingToolbar(activity, appBarLayout, collapsingToolbarLayout, toolbar, statusColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StatusBarCompatKitKat.setStatusBarColorForCollapsingToolbar(activity, appBarLayout, collapsingToolbarLayout, toolbar, statusColor);
        }
    }

    public static void changeToLightStatusBar(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        View decorView = window.getDecorView();
        if (decorView == null) {
            return;
        }
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void cancelLightStatusBar(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        View decorView = window.getDecorView();
        if (decorView == null) {
            return;
        }
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & (~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    public static void translucentStatusBarPadding(final View view) {
        translucentStatusBarPadding(view, false);
    }

    public static void translucentStatusBarPadding(final View view, final boolean exHeight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    int barHeight = getStatusBarHeight(view.getContext().getApplicationContext());
                    int left = view.getPaddingLeft();
                    int top = view.getPaddingTop();
                    int right = view.getPaddingRight();
                    int bottom = view.getPaddingBottom();
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    if (exHeight) {
                        params.height = (view.getHeight() + barHeight);
                    }
                    view.setLayoutParams(params);
                    view.setPadding(left, top + barHeight, right, bottom);
                }
            });
        }
    }

    private static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38; // 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = ConvertUtils.convertInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static void changeStatusBar(@NonNull Activity activity, @StatusBarStyle int style) {
        switch (style) {
            case STYLE_MAIN:
                setStatusBarColor(activity,
                        ContextCompat.getColor(activity, R.color.lib_pub_color_status_bar_main));
                changeToLightStatusBar(activity);
                break;

            case STYLE_WHITE:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    setStatusBarColor(activity,
                            ContextCompat.getColor(activity, R.color.lib_pub_color_status_bar_white));
                } else {
                    setStatusBarColor(activity,
                            ContextCompat.getColor(activity, R.color.lib_pub_color_bg_sub));
                    changeToLightStatusBar(activity);
                }
                break;
        }
    }
}
