package com.d.lib.rxnet.base;

import android.support.annotation.Nullable;

import com.d.lib.rxnet.observer.DownloadObserver;

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
        if (tag == null || disposable == null) {
            return;
        }
        mHashMap.put(tag, disposable);
    }

    public synchronized void remove(Object tag) {
        if (mHashMap.isEmpty() || tag == null) {
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
        if (tag == null) {
            return false;
        }
        boolean canceled = mHashMap.containsKey(tag);
        cancel(tag);
        return canceled;
    }

    public synchronized void cancel(Object tag) {
        if (tag == null) {
            return;
        }
        Disposable value = mHashMap.remove(tag);
        cancelImp(value);
    }

    private void cancelImp(@Nullable Disposable value) {
        if (value != null && !value.isDisposed()) {
            if (value instanceof DownloadObserver) {
                ((DownloadObserver) value).cancel();
            } else {
                value.dispose();
            }
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
