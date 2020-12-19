package com.d.lib.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.util.provider.FileProviderCompat;

import java.io.File;
import java.util.List;

public final class AppUtils {

    private AppUtils() {
    }


    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param context The context.
     * @param file    The file.
     */
    public static void installApp(final Context context, final File file) {
        if (context == null || file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        FileProviderCompat.setDataAndType(context, intent, file, type, false);
        context.startActivity(intent);
    }

    /**
     * Install the app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param activity    The activity.
     * @param file        The file.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void installApp(final Activity activity,
                                  final File file,
                                  final int requestCode) {
        if (activity == null || file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        FileProviderCompat.setDataAndType(activity, intent, file, type, false);
        activity.startActivityForResult(intent, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        try {
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Uninstall the app.
     *
     * @param packageName The name of the package.
     */
    public static void uninstallApp(final Context context, final String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Uninstall the app.
     *
     * @param activity    The activity.
     * @param packageName The name of the package.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void uninstallApp(final Activity activity,
                                    final String packageName,
                                    final int requestCode) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Return whether the app is installed.
     *
     * @param pkgName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppInstalled(final Context context, @NonNull final String pkgName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(pkgName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return whether it is a debug application.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug(Context context) {
        return isAppDebug(context, context.getPackageName());
    }

    /**
     * Return whether it is a debug application.
     *
     * @param packageName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppDebug(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return whether it is a system application.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppSystem(final Context context) {
        return isAppSystem(context, context.getPackageName());
    }

    /**
     * Return whether it is a system application.
     *
     * @param packageName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppSystem(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return whether application is foreground.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppForeground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (aInfo.processName.equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether application is foreground.
     * <p>Target APIs greater than 21 must hold
     * {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />}</p>
     *
     * @param packageName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppForeground(final Context context, @NonNull final String packageName) {
        return !TextUtils.isEmpty(packageName) && packageName.equals(getForegroundProcessName(context));
    }

    private static String getForegroundProcessName(final Context context) {
        ActivityManager am =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pInfo = am.getRunningAppProcesses();
        if (pInfo != null && pInfo.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo aInfo : pInfo) {
                if (aInfo.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return aInfo.processName;
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageManager pm = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list =
                    pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            Log.i("ProcessUtils", list.toString());
            if (list.size() <= 0) {
                Log.i("ProcessUtils", "getForegroundProcessName: noun of access to usage information.");
                return "";
            }
            try {
                // Access to usage information.
                ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid,
                        info.packageName) != AppOpsManager.MODE_ALLOWED) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName) != AppOpsManager.MODE_ALLOWED) {
                    Log.i("ProcessUtils", "getForegroundProcessName: refuse to device usage stats.");
                    return "";
                }
                UsageStatsManager usageStatsManager = (UsageStatsManager) context
                        .getSystemService(Context.USAGE_STATS_SERVICE);
                List<UsageStats> usageStatsList = null;
                if (usageStatsManager != null) {
                    long endTime = System.currentTimeMillis();
                    long beginTime = endTime - 86400000 * 7;
                    usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                            beginTime, endTime);
                }
                if (usageStatsList == null || usageStatsList.isEmpty()) return null;
                UsageStats recentStats = null;
                for (UsageStats usageStats : usageStatsList) {
                    if (recentStats == null
                            || usageStats.getLastTimeUsed() > recentStats.getLastTimeUsed()) {
                        recentStats = usageStats;
                    }
                }
                return recentStats == null ? null : recentStats.getPackageName();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Return whether application is running.
     *
     * @param pkgName The name of the package.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isAppRunning(final Context context, @NonNull final String pkgName) {
        int uid;
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(pkgName, 0);
            if (ai == null) return false;
            uid = ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
            if (taskInfo != null && taskInfo.size() > 0) {
                for (ActivityManager.RunningTaskInfo aInfo : taskInfo) {
                    if (pkgName.equals(aInfo.baseActivity.getPackageName())) {
                        return true;
                    }
                }
            }
            List<ActivityManager.RunningServiceInfo> serviceInfo = am.getRunningServices(Integer.MAX_VALUE);
            if (serviceInfo != null && serviceInfo.size() > 0) {
                for (ActivityManager.RunningServiceInfo aInfo : serviceInfo) {
                    if (uid == aInfo.uid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determine if a service is running
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
     * Launch the application.
     *
     * @param packageName The name of the package.
     */
    public static void launchApp(@NonNull final Context context, final String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        Intent launchAppIntent = getLaunchAppIntent(context, packageName, true);
        if (launchAppIntent == null) {
            return;
        }
        context.startActivity(launchAppIntent);
    }

    /**
     * Launch the application.
     *
     * @param activity    The activity.
     * @param packageName The name of the package.
     * @param requestCode If &gt;= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void launchApp(final Activity activity,
                                 final String packageName,
                                 final int requestCode) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        Intent launchAppIntent = getLaunchAppIntent(activity, packageName, false);
        if (launchAppIntent == null) {
            return;
        }
        activity.startActivityForResult(launchAppIntent, requestCode);
    }

    /**
     * Relaunch the application.
     */
    public static void relaunchApp(@NonNull final Context context) {
        relaunchApp(context, false);
    }

    /**
     * Relaunch the application.
     *
     * @param isKillProcess True to kill the process, false otherwise.
     */
    public static void relaunchApp(@NonNull final Context context, final boolean isKillProcess) {
        Intent intent = getLaunchAppIntent(context, context.getPackageName(), true);
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        if (!isKillProcess) return;
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private static Intent getLaunchAppIntent(@NonNull final Context context,
                                             final String packageName,
                                             final boolean isNewTask) {
        String launcherActivity = getLauncherActivity(context, packageName);
        if (!launcherActivity.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, launcherActivity);
            intent.setComponent(cn);
            return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
        }
        return null;
    }

    private static String getLauncherActivity(@NonNull final Context context, @NonNull final String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(pkg);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        int size = info.size();
        if (size == 0) {
            return "";
        }
        for (int i = 0; i < size; i++) {
            ResolveInfo ri = info.get(i);
            if (ri.activityInfo.processName.equals(pkg)) {
                return ri.activityInfo.name;
            }
        }
        return info.get(0).activityInfo.name;
    }

    /**
     * Launch the application's details settings.
     */
    public static void launchAppDetailsSettings(Context context) {
        launchAppDetailsSettings(context, context.getPackageName());
    }

    /**
     * Launch the application's details settings.
     *
     * @param packageName The name of the package.
     */
    public static void launchAppDetailsSettings(final Context context, final String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * Return the application's icon.
     *
     * @return the application's icon
     */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /**
     * Return the application's icon.
     *
     * @param packageName The name of the package.
     * @return the application's icon
     */
    public static Drawable getAppIcon(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return the application's icon resource identifier.
     *
     * @return the application's icon resource identifier
     */
    public static int getAppIconId(Context context) {
        return getAppIconId(context, context.getPackageName());
    }

    /**
     * Return the application's icon resource identifier.
     *
     * @param packageName The name of the package.
     * @return the application's icon resource identifier
     */
    public static int getAppIconId(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? 0 : pi.applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Return the application's package name.
     *
     * @return the application's package name
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * Return the application's name.
     *
     * @return the application's name
     */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /**
     * Return the application's name.
     *
     * @param packageName The name of the package.
     * @return the application's name
     */
    public static String getAppName(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the application's path.
     *
     * @return the application's path
     */
    public static String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /**
     * Return the application's path.
     *
     * @param packageName The name of the package.
     * @return the application's path
     */
    public static String getAppPath(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the application's version name.
     *
     * @return the application's version name
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    public static String getAppVersionName(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the application's version code.
     *
     * @return the application's version code
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    public static int getAppVersionCode(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Return the application's signature.
     *
     * @param packageName The name of the package.
     * @return the application's signature
     */
    public static Signature[] getAppSignature(final Context context, final String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Return the application's user-ID.
     *
     * @return the application's signature for MD5 value
     */
    public static int getAppUid(Context context) {
        return getAppUid(context, context.getPackageName());
    }

    /**
     * Return the application's user-ID.
     *
     * @param pkgName The name of the package.
     * @return the application's signature for MD5 value
     */
    public static int getAppUid(final Context context, String pkgName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(pkgName, 0);
            if (ai != null) {
                return ai.uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Exit application
     */
    public static void exit(Context context, int type) {
        switch (type) {
            case 0:
                // Method 3-1
                System.exit(0);
                break;

            case 1:
                // Method 3-2
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                break;

            case 2:
                // Method 3-3
                ActivityManager manager = (ActivityManager) context.getApplicationContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);
                if (manager != null) {
                    manager.killBackgroundProcesses(context.getApplicationContext().getPackageName());
                }
                break;
        }
    }
}
