package com.d.lib.common.component.netstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.d.lib.taskscheduler.TaskScheduler;

public class NetCompat {

    /**
     * Initialization
     */
    public static void init(Context context) {
        final Context appContext = context.getApplicationContext();
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                resetImplementation(appContext, false);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .build();

            if (cm != null) {
                cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        reset(appContext);
                    }

                    @Override
                    public void onLost(Network network) {
                        reset(appContext);
                    }

                    public void onUnavailable() {
                        reset(appContext);
                    }
                });
            }
        }
    }

    /**
     * Get current network status
     */
    public static int getStatus() {
        int state = NetState.NET_STATUS;
        return state;
    }

    /**
     * Get current network sub status
     */
    public static int getSubStatus() {
        int state = NetSubState.NET_SUB_STATUS;
        return state;
    }

    /**
     * Reset current network status - with broadcast
     */
    static void reset(Context context) {
        final Context appContext = context.getApplicationContext();
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                resetImplementation(appContext, true);
            }
        });
    }

    /**
     * Reset current network status
     *
     * @param broadcast with broadcast
     */
    private static void resetImplementation(Context context, boolean broadcast) {
        int networkType = getNetworkType(context);
        switch (networkType) {
            case NetState.UN_CONNECTED:
                if (NetState.NET_STATUS != NetState.UN_CONNECTED) {
                    NetState.NET_STATUS = NetState.UN_CONNECTED;
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_UNKNOWN;
                    if (broadcast) {
                        NetBus.getIns().onNetChange(NetState.UN_CONNECTED);
                    }
                }
                break;
            case NetState.CONNECTED_MOBILE:
                if (NetState.NET_STATUS != NetState.CONNECTED_MOBILE) {
                    NetState.NET_STATUS = NetState.CONNECTED_MOBILE;
                    if (broadcast) {
                        NetBus.getIns().onNetChange(NetState.CONNECTED_MOBILE);
                    }
                }
                break;
            case NetState.CONNECTED_WIFI:
                if (NetState.NET_STATUS != NetState.CONNECTED_WIFI) {
                    NetState.NET_STATUS = NetState.CONNECTED_WIFI;
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_UNKNOWN;
                    if (broadcast) {
                        NetBus.getIns().onNetChange(NetState.CONNECTED_WIFI);
                    }
                }
                break;
            default:
                if (NetState.NET_STATUS != NetState.NO_AVAILABLE) {
                    NetState.NET_STATUS = NetState.NO_AVAILABLE;
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_UNKNOWN;
                    if (broadcast) {
                        NetBus.getIns().onNetChange(NetState.NO_AVAILABLE);
                    }
                }
                break;
        }
    }

    /**
     * Get the current network type
     *
     * @return type 0: NO_AVAILABLE, 1: UN_CONNECTED, 2: CONNECTED_MOBILE, 3: CONNECTED_WIFI
     */
    @Deprecated
    private static int getNetwork(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.isConnected()) {
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
                return NetState.CONNECTED_WIFI;
            } else if (mobNetInfo != null && mobNetInfo.isConnected()) {
                return NetState.CONNECTED_MOBILE;
            } else {
                return NetState.UN_CONNECTED;
            }
        } else {
            return NetState.NO_AVAILABLE;
        }
    }

    /**
     * Get the current network type
     *
     * @return type 0: NO_AVAILABLE, 1: UN_CONNECTED, 2: CONNECTED_MOBILE, 3: CONNECTED_WIFI
     */
    private static int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            /** No network */
            return NetState.NO_AVAILABLE;
        }
        if (!networkInfo.isConnected()) {
            /** The network is disconnected or closed */
            return NetState.UN_CONNECTED;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            /** Ethernet network */
            return NetState.CONNECTED_MOBILE;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            /** WiFi network, when activated, all data traffic will use this connection by default */
            return NetState.CONNECTED_WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            /** Mobile data connection, cannot coexist with connection, if wifi is turned on, it is automatically closed */
            switch (networkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    /** 2G network */
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_2G;
                    return NetState.CONNECTED_MOBILE;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    /** 3G network */
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_3G;
                    return NetState.CONNECTED_MOBILE;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    /** 4G network */
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_4G;
                    return NetState.CONNECTED_MOBILE;

                default:
                    /** Default 4G network */
                    NetSubState.NET_SUB_STATUS = NetSubState.NETWORK_TYPE_4G;
                    return NetState.CONNECTED_MOBILE;
            }
        }
        /** Unknown network */
        return NetState.NO_AVAILABLE;
    }
}
