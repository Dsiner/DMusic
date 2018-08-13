package com.d.commenplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.d.commenplayer.CommenPlayer;
import com.d.commenplayer.adapter.AdapterPlayer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MUtil {
    private static int STATUS_BAR_HEIGHT = -1;
    private static int NAVIGATION_BAR_HEIGHT = -1;
    public final static int SEEKBAR_MAX = 1000;

    /**
     * 获取屏幕宽度和高度
     */
    public static int[] getScreenSize(Activity activity) {
        int[] size = new int[2];
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        size[0] = metric.widthPixels;
        size[1] = metric.heightPixels;
        return size;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dpValue * (metrics.densityDpi / 160f));
    }

    public static String generateTime(int time) {
        int totalSeconds = time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 获取SeekBar进度
     *
     * @param position:当前播放时间
     * @param duration:播放总时间
     */
    public static int getProgress(int position, int duration) {
        int progress = (int) (1.0f * MUtil.SEEKBAR_MAX * position / duration);
        progress = Math.min(progress, duration);
        progress = Math.max(progress, 0);
        return progress;
    }

    /**
     * 获取SeekBar缓冲进度
     *
     * @param bufferPercentage:缓冲进度bufferPercentage,0-100
     */
    public static int getSecondaryProgress(int bufferPercentage) {
        int secondaryProgress = (int) (1.0f * bufferPercentage / 100 * MUtil.SEEKBAR_MAX);
        secondaryProgress = Math.min(secondaryProgress, MUtil.SEEKBAR_MAX);
        secondaryProgress = Math.max(secondaryProgress, 0);
        return secondaryProgress;
    }

    /**
     * 获取当前播放位置
     *
     * @param progress:seekbar当前进度
     * @param duration:播放总时间
     */
    public static int getPosition(int progress, int duration) {
        int position = (int) (1.0f * progress / MUtil.SEEKBAR_MAX * duration);
        position = Math.min(position, duration);
        position = Math.max(position, 0);
        return position;
    }

    public static void peelInject(CommenPlayer player, ViewGroup root) {
        if (player == null || root == null || player.getParent() == root) {
            return;
        }
        if (player.getParent() != null) {
            if (player.getParent() instanceof AdapterPlayer) {
                ((AdapterPlayer) player.getParent()).recycle(false);
            } else {
                ((ViewGroup) player.getParent()).removeView(player);
            }
        }
        if (root instanceof AdapterPlayer) {
            ((AdapterPlayer) root).inject();
        } else {
            root.removeView(player);
            root.addView(player, 0);
        }
    }

    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && height > width
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

    public static Bitmap getScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bitmap ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        view.destroyDrawingCache();
        bmp.recycle();
        return ret;
    }

    public static Bitmap getFrame(Context context, String url, int pos) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            retriever.setDataSource(url);
            //取得视频的长度(单位为毫秒)
//            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            bitmap = retriever.getFrameAtTime(pos, MediaMetadataRetriever.OPTION_CLOSEST);//获取时间点的缩略图
            retriever.release();
        } catch (Exception e) {
            retriever.release();
            MLog.d("fail to get frame" + e.getMessage());
        }
        return bitmap;
    }

    /**
     * 合成
     */
    private static void drawBitmap(Bitmap bitmap, Bitmap bp, float left, float top) {
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(bp, left, top, null);
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.restore();//存储
    }

    /**
     * 字符串转int
     */
    public static int parseInt(String str) {
        if (TextUtils.isDigitsOnly(str)) {
            int result = 0;
            try {
                result = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return result;
        }
        return 0;
    }

    /**
     * 是否支持沉浸式状态栏
     */
    public static boolean isSupportTranslucentStatusBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (STATUS_BAR_HEIGHT != -1) {
            return STATUS_BAR_HEIGHT;
        }
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        STATUS_BAR_HEIGHT = sbar;
        return sbar;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (NAVIGATION_BAR_HEIGHT != -1) {
            return NAVIGATION_BAR_HEIGHT;
        }
        if (!checkDeviceHasNavigationBar(context)) {
            NAVIGATION_BAR_HEIGHT = 0;
            return 0;
        }
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("navigation_bar_height");
            x = parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        NAVIGATION_BAR_HEIGHT = sbar;
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
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    /**
     * 隐藏状态栏&&导航栏
     */
    public static void hideSystemUI(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //4.1 above API level 16
            int uist = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uist |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(uist);
            //some views can setFitsSystemWindows(true)、setPadding()
            setFitsPadding(0, getStatusBarHeight(activity), getNavigationBarHeight(activity), 0, views);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 显示状态栏&&导航栏
     */
    public static void showSystemUI(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //4.1 above API level 16
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //some views can setFitsSystemWindows(true)、setPadding()、虚拟导航栏高度、ui处理
            setFitsPadding(0, getStatusBarHeight(activity), getNavigationBarHeight(activity), 0, views);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 强制显示状态栏&&导航栏
     */
    public static void showSystemUIFource(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //4.1 above API level 16
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //some views can setFitsSystemWindows(false)、setPadding()
            setFitsPadding(0, 0, 0, 0, views);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void setFitsPadding(int left, int top, int right, int bottom, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (views != null && views.length > 0) {
                for (View v : views) {
                    v.setFitsSystemWindows(false);
                    v.setPadding(left, top, right, bottom);
                }
            }
        }
    }
}
