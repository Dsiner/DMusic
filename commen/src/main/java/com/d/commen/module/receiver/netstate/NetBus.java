package com.d.commen.module.receiver.netstate;

import java.util.ArrayList;

/**
 * 网络监听
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

    public void addListener(OnNetListener l) {
        if (l != null) {
            onNetListeners.add(l);
        }
    }

    public void removeListener(OnNetListener l) {
        if (l != null) {
            onNetListeners.remove(l);
        }
    }

    public void onNetChange(int state) {
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
