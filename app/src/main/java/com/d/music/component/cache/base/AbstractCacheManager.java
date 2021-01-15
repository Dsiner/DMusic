package com.d.music.component.cache.base;

import android.content.Context;

import androidx.annotation.NonNull;

import com.d.music.component.cache.listener.CacheListener;
import com.d.music.component.cache.utils.threadpool.ThreadPool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public abstract class AbstractCacheManager<K, T> extends CacheManager {
    protected LruCache<K, T> mLruCache;
    protected HashMap<K, ArrayList<CacheListener<T>>> mHashMap;

    protected AbstractCacheManager(Context context) {
        super(context);
        mLruCache = new LruCache<>();
        mHashMap = new HashMap<>();
    }

    public void load(final Context context, final K key, final CacheListener<T> listener) {
        if (isLoading(key, listener)) {
            return;
        }
        if (isLru(key, listener)) {
            return;
        }
        ThreadPool.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if (isDisk(key, listener)) {
                    return;
                }
                absLoad(context, key, listener);
            }
        });
    }

    protected void success(final K key, final T value, final CacheListener<T> l) {
        ThreadPool.getInstance().executeMain(new Runnable() {
            @Override
            public void run() {
                successImplementation(key, value);
            }
        });
    }

    private void successImplementation(final K key, final T value) {
        // Save to cache
        putLru(key, value);
        ArrayList<CacheListener<T>> listeners = mHashMap.get(key);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                CacheListener<T> listener = listeners.get(i);
                listener.onSuccess(value);
            }
            mHashMap.remove(key);
        }
    }

    protected void error(final K key, final Throwable e, final CacheListener<T> l) {
        ThreadPool.getInstance().executeMain(new Runnable() {
            @Override
            public void run() {
                errorImplementation(key, e);
            }
        });
    }

    private void errorImplementation(final K key, final Throwable e) {
        ArrayList<CacheListener<T>> listeners = mHashMap.get(key);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onError(e);
            }
            mHashMap.remove(key);
        }
    }

    protected boolean isLoading(final K key, final CacheListener<T> l) {
        if (mHashMap.containsKey(key)) {
            if (l != null) {
                ArrayList<CacheListener<T>> listeners = mHashMap.get(key);
                listeners.add(l);
                l.onLoading();
            }
            return true;
        }
        if (l != null) {
            l.onLoading();
            ArrayList<CacheListener<T>> listeners = new ArrayList<>();
            listeners.add(l);
            mHashMap.put(key, listeners);
        }
        return false;
    }

    protected boolean isLru(final K key, final CacheListener<T> listener) {
        final T valueLru = mLruCache.get(key);
        if (valueLru != null) {
            success(key, valueLru, listener);
            return true;
        }
        return false;
    }

    protected void putLru(K key, T value) {
        mLruCache.put(key, value);
    }

    protected boolean isDisk(final K key, final CacheListener<T> listener) {
        final T valueDisk = getDisk(key);
        if (valueDisk != null) {
            success(key, valueDisk, listener);
            return true;
        }
        return false;
    }

    public void release() {
        mLruCache.clear();
    }

    @NonNull
    protected abstract String getPreFix();

    protected abstract void absLoad(final Context context, final K key, final CacheListener<T> listener);

    protected abstract T getDisk(final K key);

    protected abstract void putDisk(K key, T value);
}
