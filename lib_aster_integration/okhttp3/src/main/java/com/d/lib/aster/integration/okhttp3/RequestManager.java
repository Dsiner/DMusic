package com.d.lib.aster.integration.okhttp3;

import android.support.annotation.Nullable;

import com.d.lib.aster.integration.okhttp3.observer.DownloadObserver;
import com.d.lib.aster.integration.okhttp3.observer.UploadObserver;
import com.d.lib.aster.scheduler.callback.Observer;

import java.util.HashMap;
import java.util.Set;

/**
 * Request management to facilitate mid-way cancellation of requests
 */
public class RequestManager {
    private HashMap<Object, Observer> mHashMap;

    private static class Singleton {
        private final static RequestManager INSTANCE = new RequestManager();
    }

    public static RequestManager getIns() {
        return Singleton.INSTANCE;
    }

    private RequestManager() {
        mHashMap = new HashMap<>();
    }

    public synchronized void add(Object tag, Observer disposable) {
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
        Observer value = mHashMap.remove(tag);
        cancelImp(value);
    }

    private void cancelImp(@Nullable Observer value) {
        // TODO: @dsiner imp... 2018/12/6
//        if (value != null && !value.isDisposed()) {
//            if (value instanceof DownloadObserver) {
//                ((DownloadObserver) value).cancel();
//            } else if (value instanceof UploadObserver) {
//                ((UploadObserver) value).cancel();
//            } else {
//                value.dispose();
//            }
//        }
    }

    public synchronized void cancelAll() {
        if (mHashMap.isEmpty()) {
            return;
        }
        HashMap<Object, Observer> temp = new HashMap<>(mHashMap);
        mHashMap.clear();
        Set<Object> keys = temp.keySet();
        for (Object k : keys) {
            cancelImp(temp.get(k));
        }
    }
}
