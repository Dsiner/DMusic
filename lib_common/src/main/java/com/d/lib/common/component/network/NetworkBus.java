package com.d.lib.common.component.network;

import android.support.annotation.UiThread;

import com.d.lib.common.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Network monitoring
 * Created by D on 2018/2/5.
 */
public class NetworkBus {
    private volatile static NetworkBus INSTANCE;
    private final List<OnNetworkTypeChangeListener> mOnNetworkTypeChangeListener = new ArrayList<>();

    public static NetworkBus getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkBus();
                }
            }
        }
        return INSTANCE;
    }

    @UiThread
    public void addListener(OnNetworkTypeChangeListener l) {
        if (l != null) {
            mOnNetworkTypeChangeListener.add(l);
        }
    }

    @UiThread
    public void removeListener(OnNetworkTypeChangeListener l) {
        if (l != null) {
            mOnNetworkTypeChangeListener.remove(l);
        }
    }

    @UiThread
    public void clearAllListener(OnNetworkTypeChangeListener l) {
        mOnNetworkTypeChangeListener.clear();
    }

    void onNetworkTypeChange(NetworkUtils.NetworkType networkType) {
        if (networkType == null) {
            return;
        }
        synchronized (NetworkBus.class) {
            for (int i = 0; i < mOnNetworkTypeChangeListener.size(); i++) {
                OnNetworkTypeChangeListener listener = mOnNetworkTypeChangeListener.get(i);
                if (listener != null) {
                    listener.onNetworkTypeChange(networkType);
                }
            }
        }
    }

    public interface OnNetworkTypeChangeListener {
        void onNetworkTypeChange(NetworkUtils.NetworkType networkType);
    }
}
