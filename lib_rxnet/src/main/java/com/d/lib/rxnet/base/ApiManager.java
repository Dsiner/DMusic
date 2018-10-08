package com.d.lib.rxnet.base;

import java.util.HashMap;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * Request management to facilitate mid-way cancellation of requests
 */
public class ApiManager {
    private HashMap<Object, Disposable> map;

    private static class Singleton {
        private final static ApiManager INSTANCE = new ApiManager();
    }

    public static ApiManager get() {
        return Singleton.INSTANCE;
    }

    private ApiManager() {
        map = new HashMap<>();
    }

    public synchronized void add(Object tag, Disposable disposable) {
        map.put(tag, disposable);
    }

    public synchronized void remove(Object tag) {
        if (map.isEmpty()) {
            return;
        }
        map.remove(tag);
    }

    public synchronized void removeAll() {
        if (map.isEmpty()) {
            return;
        }
        map.clear();
    }

    public synchronized void cancel(Object tag) {
        if (map.isEmpty()) {
            return;
        }
        Disposable value = map.get(tag);
        if (value == null) {
            return;
        }
        if (!value.isDisposed()) {
            value.dispose();
            map.remove(tag);
        }
    }

    public synchronized void cancelAll() {
        if (map.isEmpty()) {
            return;
        }
        Set<Object> keys = map.keySet();
        for (Object apiKey : keys) {
            cancel(apiKey);
        }
    }
}
