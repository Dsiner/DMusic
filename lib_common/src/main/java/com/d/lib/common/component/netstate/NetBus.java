package com.d.lib.common.component.netstate;

import android.support.annotation.UiThread;

import java.util.ArrayList;

/**
 * Network monitoring
 * Created by D on 2018/2/5.
 */
public class NetBus {
    private static NetBus instance;
    private ArrayList<OnNetListener> onNetListeners = new ArrayList<>();

    public static NetBus getInstance() {
        if (instance == null) {
            synchronized (NetBus.class) {
                if (instance == null) {
                    instance = new NetBus();
                }
            }
        }
        return instance;
    }

    @UiThread
    public void addListener(OnNetListener l) {
        if (l != null) {
            onNetListeners.add(l);
        }
    }

    @UiThread
    public void removeListener(OnNetListener l) {
        if (l != null) {
            onNetListeners.remove(l);
        }
    }

    void onNetChange(int state) {
        synchronized (NetBus.class) {
            for (int i = 0; i < onNetListeners.size(); i++) {
                OnNetListener listener = onNetListeners.get(i);
                if (listener != null) {
                    listener.onNetChange(state);
                }
            }
        }
    }

    public interface OnNetListener {
        void onNetChange(int state);
    }
}
