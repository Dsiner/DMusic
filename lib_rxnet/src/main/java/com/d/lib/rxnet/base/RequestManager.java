package com.d.lib.rxnet.base;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * Request management to facilitate mid-way cancellation of requests
 */
public class RequestManager {
    private HashMap<Object, Disposable> mHashMap;

    private static class Singleton {
        private final static RequestManager INSTANCE = new RequestManager();
    }

    public static RequestManager getIns() {
        return Singleton.INSTANCE;
    }

    private RequestManager() {
        mHashMap = new HashMap<>();
    }

    public synchronized void add(Object tag, Disposable disposable) {
        mHashMap.put(tag, disposable);
    }

    public synchronized void remove(Object tag) {
        if (mHashMap.isEmpty()) {
            return;
        }
        mHashMap.remove(tag);
    }

    public synchronized void removeAll() {
        if (mHashMap.isEmpty()) {
            return;
        }
        mHashMap.clear();
    }

    public synchronized boolean canceled(Object tag) {
        return !mHashMap.containsKey(tag);
    }

    public synchronized void cancel(Object tag) {
        Disposable value = mHashMap.remove(tag);
        cancelImp(value);
    }

    private void cancelImp(@Nullable Disposable value) {
        if (value != null && !value.isDisposed()) {
            value.dispose();
        }
    }

    public synchronized void cancelAll() {
        if (mHashMap.isEmpty()) {
            return;
        }
        HashMap<Object, Disposable> temp = new HashMap<>(mHashMap);
        mHashMap.clear();
        Set<Object> keys = temp.keySet();
        for (Object k : keys) {
            cancelImp(temp.get(k));
        }
    }
}
