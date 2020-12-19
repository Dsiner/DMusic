package com.d.lib.common.component.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import com.d.lib.common.util.NetworkUtils;
import com.d.lib.taskscheduler.TaskScheduler;

public class NetworkCompat {

    private NetworkUtils.NetworkType mNetworkType;

    private NetworkCompat() {
    }

    private static class Singleton {
        private static final NetworkCompat INSTANCE = new NetworkCompat();
    }

    /**
     * Initialization
     */
    public static void init(Context context) {
        final Context appContext = context.getApplicationContext();
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                getNetworkTypeImpl(appContext, false);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                    .build();

            if (cm != null) {
                cm.requestNetwork(request, new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        getNetworkType(appContext);
                    }

                    @Override
                    public void onLost(Network network) {
                        getNetworkType(appContext);
                    }

                    @Override
                    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                        getNetworkType(appContext);
                    }

                    @Override
                    public void onUnavailable() {
                        getNetworkType(appContext);
                    }
                });
            }
        }
    }

    /**
     * Return the type of network
     */
    public static NetworkUtils.NetworkType getType() {
        return Singleton.INSTANCE.mNetworkType;
    }

    /**
     * Return whether using mobile data.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMobileDataType(NetworkUtils.NetworkType networkType) {
        return networkType == NetworkUtils.NetworkType.NETWORK_TYPE_2G
                || networkType == NetworkUtils.NetworkType.NETWORK_TYPE_3G
                || networkType == NetworkUtils.NetworkType.NETWORK_TYPE_4G
                || networkType == NetworkUtils.NetworkType.NETWORK_TYPE_5G;
    }

    /**
     * Reset current network status - with broadcast
     */
    static void getNetworkType(Context context) {
        final Context appContext = context.getApplicationContext();
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                getNetworkTypeImpl(appContext, true);
            }
        });
    }

    /**
     * Reset current network status
     *
     * @param broadcast with broadcast
     */
    private static void getNetworkTypeImpl(Context context, boolean broadcast) {
        final NetworkUtils.NetworkType oldNetworkType = Singleton.INSTANCE.mNetworkType;
        final NetworkUtils.NetworkType networkType = NetworkUtils.getNetworkType(context);
        if (oldNetworkType == networkType) {
            return;
        }
        Singleton.INSTANCE.mNetworkType = networkType;
        if (broadcast) {
            NetworkBus.getInstance().onNetworkTypeChange(networkType);
        }
    }
}
