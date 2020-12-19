package com.d.lib.common.component.compat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

/**
 * BatteryOptimizationCompat
 * Created by D on 2019/12/26.
 */
public class BatteryOptimizationCompat {

    public static void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (isIgnoringBatteryOptimizations(context)) {
            return;
        }
        requestIgnoreBatteryOptimizations(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
