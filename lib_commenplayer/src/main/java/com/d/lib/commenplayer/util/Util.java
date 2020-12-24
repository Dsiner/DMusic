package com.d.lib.commenplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Util {
    public static final int SEEKBAR_MAX = 1000;

    private static int SCREEN_WIDTH; // Screen width
    private static int SCREEN_HEIGHT; // Screen height

    /**
     * Get screen width and height, in pixel.
     *
     * @return int[]{SCREEN_WIDTH, SCREEN_HEIGHT}, in pixel.
     */
    public static int[] getScreenSize(Activity activity) {
        if (SCREEN_WIDTH > 0 && SCREEN_HEIGHT > 0) {
            return new int[]{SCREEN_WIDTH, SCREEN_HEIGHT};
        }
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        if (metric.widthPixels != SCREEN_WIDTH) {
            SCREEN_WIDTH = metric.widthPixels;
            SCREEN_HEIGHT = metric.heightPixels;
        }
        return new int[]{SCREEN_WIDTH, SCREEN_HEIGHT};
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(@NonNull final Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String generateTime(int time) {
        int totalSeconds = time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Get SeekBar progress
     */
    public static int getProgress(int position, int duration) {
        int progress = (int) (1.0f * Util.SEEKBAR_MAX * position / duration);
        progress = Math.min(progress, duration);
        progress = Math.max(progress, 0);
        return progress;
    }

    /**
     * Get SeekBar buffer progress
     *
     * @param bufferPercentage Buffer progress bufferPercentage, 0-100
     */
    public static int getSecondaryProgress(int bufferPercentage) {
        int secondaryProgress = (int) (1.0f * bufferPercentage / 100 * Util.SEEKBAR_MAX);
        secondaryProgress = Math.min(secondaryProgress, Util.SEEKBAR_MAX);
        secondaryProgress = Math.max(secondaryProgress, 0);
        return secondaryProgress;
    }

    /**
     * Get current playback position
     *
     * @param progress SeekBar current progress
     * @param duration Total playing time
     */
    public static int getPosition(int progress, int duration) {
        int position = (int) (1.0f * progress / Util.SEEKBAR_MAX * duration);
        position = Math.min(position, duration);
        position = Math.max(position, 0);
        return position;
    }

    public static int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // If the device's natural orientation is portrait:
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

    /**
     * This method finds a representative frame at any time position if possible,
     * and returns it as a bitmap. This is useful for generating a thumbnail
     * for an input data source.
     */
    public static Bitmap getFrameAtTime(Context context, String uri) {
        // Also can use ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                    && uri.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                mmr.setDataSource(uri, headers);
            } else {
                mmr.setDataSource(context, Uri.parse(uri));
            }
            // Get the first frame picture
            return mmr.getFrameAtTime();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    /**
     * This method finds a representative frame close to the given time position by considering
     * the given option if possible, and returns it as a bitmap.
     *
     * @param context the Context to use when resolving the Uri
     * @param uri     the Content URI of the data you want to play
     * @param timeUs  The time position where the frame will be retrieved.
     * @return
     */
    public static Bitmap getFrameAtTime(Context context, String uri, int timeUs) {
        // Also can use ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                    && uri.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                mmr.setDataSource(uri, headers);
            } else {
                mmr.setDataSource(context, Uri.parse(uri));
            }
            // Get the first frame picture
            return mmr.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    private static void mergeBitmap(Bitmap bitmap, Bitmap bp, float left, float top) {
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(bp, left, top, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
    }

    public static int convertInt(String str) {
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
     * Whether to support immersive status bar
     */
    public static boolean isSupportTranslucentStatusBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Get status bar height
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = dp2px(context, 38); // The default is 38, which looks like most of them
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = convertInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * Get navigation bar height
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
            x = convertInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * Check if NavigationBar is present
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
     * Hide status bar && navigation bar
     */
    public static void hideSystemUI(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 4.1 above API level 16
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
            // Some views can setFitsSystemWindows(true), setPadding()
            setFitsPadding(0, getStatusBarHeight(activity), getNavigationBarHeight(activity), 0, views);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * Show status bar && navigation bar
     */
    public static void showSystemUI(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 4.1 above API level 16
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            // Some views can setFitsSystemWindows(false), setPadding()
            setFitsPadding(0, getStatusBarHeight(activity),
                    getNavigationBarHeight(activity), 0, views);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * Force display of status bar && navigation bar
     */
    public static void showSystemUIForce(Activity activity, View... views) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 4.1 above API level 16
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // Some views can setFitsSystemWindows(false), setPadding()
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
