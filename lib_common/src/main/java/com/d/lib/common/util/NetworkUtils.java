package com.d.lib.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.content.Context.WIFI_SERVICE;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public enum NetworkType {
        TYPE_UN_AVAILABLE,
        TYPE_MOBILE,
        NETWORK_TYPE_2G,
        NETWORK_TYPE_3G,
        NETWORK_TYPE_4G,
        NETWORK_TYPE_5G,
        TYPE_WIFI,
        TYPE_ETHERNET,
        TYPE_UNKNOWN
    }

    /**
     * Open the settings of wireless.
     */
    public static void openWirelessSettings(Context context) {
        context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    @Nullable
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }
        return cm.getActiveNetworkInfo();
    }

    /**
     * Return whether network is available.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     * Return whether network is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.isConnected();
    }

    /**
     * Return whether mobile data is enabled.
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    public static boolean isMobileDataEnabled(Context context) {
        try {
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled();
            }
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod =
                    tm.getClass().getDeclaredMethod("getDataEnabled");
            if (getMobileDataEnabledMethod != null) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Return whether using mobile data.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Return whether mobile data is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileDataConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null
                && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE
                && ni.isConnected();
    }

    /**
     * Return whether wifi is enabled.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static boolean isWifiEnabled(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return false;
        }
        return manager.isWifiEnabled();
    }

    /**
     * Enable or disable wifi.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(Context context, final boolean enabled) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (manager == null) {
            return;
        }
        if (enabled == manager.isWifiEnabled()) {
            return;
        }
        manager.setWifiEnabled(enabled);
    }

    /**
     * Return whether wifi is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null
                && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_WIFI
                && ni.isConnected();
    }

    /**
     * Return whether using ethernet.
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR2)
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static boolean isEthernet(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        final NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (info == null) {
            return false;
        }
        NetworkInfo.State state = info.getState();
        if (state == null) {
            return false;
        }
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
    }

    /**
     * Return type of network.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return type of network
     * <ul>
     *
     * <li>{@link NetworkType#TYPE_UN_AVAILABLE      } </li>
     * <li>{@link NetworkType#NETWORK_TYPE_2G      } </li>
     * <li>{@link NetworkType#NETWORK_TYPE_3G      } </li>
     * <li>{@link NetworkType#NETWORK_TYPE_4G      } </li>
     * <li>{@link NetworkType#NETWORK_TYPE_5G      } </li>
     * <li>{@link NetworkType#TYPE_WIFI    } </li>
     * <li>{@link NetworkType#TYPE_ETHERNET} </li>
     * <li>{@link NetworkType#TYPE_UNKNOWN } </li>
     *
     * </ul>
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static NetworkType getNetworkType(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (isEthernet(context)) {
                return NetworkType.TYPE_ETHERNET;
            }
        }
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.TYPE_WIFI;

            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkType.NETWORK_TYPE_2G;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkType.NETWORK_TYPE_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkType.NETWORK_TYPE_4G;

                    default:
                        String subtypeName = info.getSubtypeName();
                        if ("TD-SCDMA".equalsIgnoreCase(subtypeName)
                                || "WCDMA".equalsIgnoreCase(subtypeName)
                                || "CDMA2000".equalsIgnoreCase(subtypeName)) {
                            return NetworkType.NETWORK_TYPE_3G;
                        } else {
                            return NetworkType.NETWORK_TYPE_5G;
                        }
                }

            } else {
                return NetworkType.TYPE_UNKNOWN;
            }
        }
        return NetworkType.TYPE_UN_AVAILABLE;
    }

    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return "";
        }
        return tm.getNetworkOperatorName();
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) {
                            return hostAddress;
                        }
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }

    /**
     * Return the ssid.
     *
     * @return the ssid.
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getSSID(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm == null) {
            return "";
        }
        WifiInfo wi = wm.getConnectionInfo();
        if (wi == null) {
            return "";
        }
        String ssid = wi.getSSID();
        if (TextUtils.isEmpty(ssid)) {
            return "";
        }
        if (ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"') {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }
}
