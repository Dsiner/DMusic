package com.d.lib.common.component.netstate;

import android.support.annotation.UiThread;

import java.util.ArrayList;

/**
 * Network monitoring
 * Created by D on 2018/2/5.
 */
public class NetBus {
    private volatile static NetBus INSTANCE;
    private ArrayList<OnNetListener> mOnNetListeners = new ArrayList<>();

    public static NetBus getIns() {
        if (INSTANCE == null) {
            synchronized (NetBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetBus();
                }
            }
        }
        return INSTANCE;
    }

    @UiThread
    public void addListener(OnNetListener l) {
        if (l != null) {
            mOnNetListeners.add(l);
        }
    }

    @UiThread
    public void removeListener(OnNetListener l) {
        if (l != null) {
            mOnNetListeners.remove(l);
        }
    }

    void onNetChange(int state) {
        synchronized (NetBus.class) {
            for (int i = 0; i < mOnNetListeners.size(); i++) {
                OnNetListener listener = mOnNetListeners.get(i);
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
