package com.d.lib.common.event.bus;

import android.support.annotation.UiThread;

import com.d.lib.common.event.bus.callback.SimpleCallback;

import java.util.ArrayList;

/**
 * AbstractBus
 * Created by D on 2018/1/26.
 */
public abstract class AbstractBus<T, Callback extends SimpleCallback<T>> implements SimpleCallback<T> {
    protected ArrayList<Callback> mCallbacks = new ArrayList<>();

    /**
     * Registers the given subscriber to receive events.
     */
    @UiThread
    public void register(Callback subscriber) {
        if (subscriber != null) {
            mCallbacks.add(subscriber);
        }
    }

    /**
     * Unregisters the given subscriber from all event classes.
     */
    @UiThread
    public synchronized void unregister(Callback subscriber) {
        if (subscriber != null) {
            mCallbacks.remove(subscriber);
        }
    }

    @UiThread
    @Override
    public void onSuccess(T response) {
        for (int i = 0; i < mCallbacks.size(); i++) {
            Callback l = mCallbacks.get(i);
            if (l != null) {
                l.onSuccess(response);
            }
        }
    }

    @UiThread
    @Override
    public void onError(Throwable e) {
        for (int i = 0; i < mCallbacks.size(); i++) {
            Callback l = mCallbacks.get(i);
            if (l != null) {
                l.onError(e);
            }
        }
    }
}
