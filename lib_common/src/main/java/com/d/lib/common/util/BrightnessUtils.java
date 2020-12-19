package com.d.lib.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

public class BrightnessUtils {

    private BrightnessUtils() {
    }

    /**
     * Return whether automatic brightness mode is enabled.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAutoBrightnessEnabled(Context context) {
        try {
            int mode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Enable or disable automatic brightness mode.
     * <p>Must hold {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean setAutoBrightnessEnabled(Context context, final boolean enabled) {
        return Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                        : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        );
    }

    /**
     * Get screen brightness
     *
     * @return Screen brightness 0-255
     */
    public static int getBrightness(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Set screen brightness
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.WRITE_SETTINGS" />}</p>
     *
     * @param brightness Brightness value
     */
    public static boolean setBrightness(Context context, @IntRange(from = 0, to = 255) final int brightness) {
        ContentResolver resolver = context.getContentResolver();
        boolean b = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null);
        return b;
    }

    /**
     * Set window brightness
     *
     * @param window     Window
     * @param brightness Brightness value
     */
    public static void setWindowBrightness(@NonNull final Window window,
                                           @IntRange(from = 0, to = 255) final int brightness) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255f;
        window.setAttributes(lp);
    }

    /**
     * Get window brightness
     *
     * @param window Window
     * @return Screen brightness 0-255
     */
    public static int getWindowBrightness(Context context, final Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
        if (brightness < 0) {
            return getBrightness(context);
        }
        return (int) (brightness * 255);
    }
}
