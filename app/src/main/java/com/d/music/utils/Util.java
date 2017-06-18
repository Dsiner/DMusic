package com.d.music.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.d.music.module.global.Cst;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Util集合
 * Created by D on 2017/4/27.
 */
public class Util {
    private static Gson gson = new Gson();

    public static Gson getGsonIns() {
        return gson;
    }

    /**
     * Toast提示
     *
     * @param c：   Context
     * @param msg： String
     */
    public static void toast(Context c, String msg) {
        if (c == null || TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast提示
     *
     * @param c：     Context
     * @param resId： resource id
     */
    public static void toast(Context c, int resId) {
        if (c == null) {
            return;
        }
        toast(c, c.getString(resId));
    }

    /**
     * 获取屏幕宽度和高度
     */
    public static void setScreenSize(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        if (metric.widthPixels != Cst.SCREEN_WIDTH) {
            Cst.SCREEN_WIDTH = metric.widthPixels;
            Cst.SCREEN_HEIGHT = metric.heightPixels;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dpValue * (metrics.densityDpi / 160f));
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context:context
     * @param spValue:DisplayMetrics类中属性scaledDensity）
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取字体高度
     */
    public static float getTextHeight(Paint p) {
        Paint.FontMetrics fm = p.getFontMetrics();// 获取字体高度
        return (float) ((Math.ceil(fm.descent - fm.top) + 2) / 2);
    }

    /**
     * format a number properly with the given number of digits
     *
     * @param number the number to format
     * @param digits the number of digits
     * @return
     */
    public static String formatDecimal(double number, int digits) {
        number = roundNumber((float) number, digits);
        StringBuffer a = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                a.append(".");
            a.append("0");
        }
        DecimalFormat nf = new DecimalFormat("###,###,###,##0" + a.toString());
        String formatted = nf.format(number);
        return formatted;
    }

    /**
     * Math.pow(...) is very expensive, so avoid calling it and create it
     * yourself.
     */
    private static final int POW_10[] = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000
    };

    public static float roundNumber(float number, int digits) {
        try {
            if (digits == 0) {
                int r0 = (int) Math.round(number);
                return r0;
            } else if (digits > 0) {
                if (digits > 9)
                    digits = 9;
                StringBuffer a = new StringBuffer();
                for (int i = 0; i < digits; i++) {
                    if (i == 0)
                        a.append(".");
                    a.append("0");
                }
                DecimalFormat nf = new DecimalFormat("#" + a.toString());
                String formatted = nf.format(number);
                return Float.valueOf(formatted);
            } else {
                digits = -digits;
                if (digits > 9)
                    digits = 9;
                int r2 = (int) (number / POW_10[digits] + 0.5);
                return r2 * POW_10[digits];
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return number;
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatuBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = MathManager.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("navigation_bar_height");
            x = MathManager.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 检查是否存在NavigationBar
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    /**
     * 判断某个服务是否正在运行
     */
    public static boolean isServiceRunning(final Context context, final String className) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.size() <= 0) {
            return false;
        }
        final int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化时间，把毫秒转换成分:秒(00:00)格式
     */
    public static String formatTime(int time) {
        StringBuilder sb;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        if (min / 10 < 1) {
            sb = new StringBuilder("0");
            sb.append(String.valueOf(min));
        } else {
            sb = new StringBuilder(String.valueOf(min));
        }
        sb.append(":");
        if (sec / 10 < 1) {
            sb.append("0");
        }
        sb.append(String.valueOf(sec));
        return sb.toString();
    }

    /**
     * 返回数据大小(byte)对应文本
     */
    public static String formatSize(long size) {
        DecimalFormat format = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1024 * 1024) {
            float kb = size / 1024f;
            return format.format(kb) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mb = size / (1024 * 1024f);
            return format.format(mb) + "MB";
        } else {
            float gb = size / (1024 * 1024 * 1024f);
            return format.format(gb) + "GB";
        }
    }
}
