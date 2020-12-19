package com.d.lib.common.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import static android.Manifest.permission.READ_PHONE_STATE;

public final class PhoneUtils {

    private PhoneUtils() {
    }

    /**
     * Return whether the device is phone.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPhone(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }


    private static String getSystemPropertyByReflect(String key) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) {/**/}
        return "";
    }

    /**
     * Return the IMSI.
     * <p>Must hold {@code <uses-permission android:name="android.permission.READ_PHONE_STATE" />}</p>
     *
     * @return the IMSI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMSI(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    /**
     * Returns the current phone type.
     *
     * @return the current phone type
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM }</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA}</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP }</li>
     * </ul>
     */
    public static int getPhoneType(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getPhoneType();
    }

    /**
     * Return whether sim card state is ready.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSimCardReady(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * Return the sim operator name.
     *
     * @return the sim operator name
     */
    public static String getSimOperatorName(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getSimOperatorName();
    }

    /**
     * Return the sim operator using mnc.
     *
     * @return the sim operator
     */
    public static String getSimOperatorByMnc(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (operator == null) return "";
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
            case "46020":
                return "中国移动";
            case "46001":
            case "46006":
            case "46009":
                return "中国联通";
            case "46003":
            case "46005":
            case "46011":
                return "中国电信";
            default:
                return operator;
        }
    }
}
