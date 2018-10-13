package com.d.lib.rxnet.base;

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

    public synchronized void cancel(Object tag) {
        cancelImp(tag);
        mHashMap.remove(tag);
    }

    private void cancelImp(Object tag) {
        Disposable value = mHashMap.get(tag);
        if (value != null && !value.isDisposed()) {
            value.dispose();
        }
    }

    public synchronized void cancelAll() {
        if (mHashMap.isEmpty()) {
            return;
        }
        Set<Object> keys = mHashMap.keySet();
        for (Object k : keys) {
            cancelImp(k);
        }
        mHashMap.clear();
    }
}
