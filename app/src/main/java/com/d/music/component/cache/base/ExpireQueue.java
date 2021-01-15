package com.d.music.component.cache.base;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * ExpireQueue
 * Created by D on 2018/10/16.
 */
public class ExpireQueue<K> {
    private int maxCount, maxQueue;
    private List<K> list = new ArrayList<>();
    private List<K> queue = new ArrayList<>();
    private OnExpireListener<K> onExpireListener;

    public ExpireQueue(@IntRange(from = 1) int maxCount, @IntRange(from = 1) int maxQueue) {
        this.maxCount = maxCount;
        this.maxQueue = maxQueue;
    }

    public void add(K value) {
        queue.add(0, value);
        if (expireImp()) {
            expireImp();
        }
    }

    private boolean expireImp() {
        int size = queue.size();
        if (size > maxQueue) {
            K last = queue.get(size - 1);
            queue.remove(size - 1);
            notifyDataExpire(last);
            return true;
        }
        return false;
    }

    @Nullable
    public K take() {
        if (queue.size() <= 0) {
            return null;
        }
        K first = queue.get(0);
        queue.remove(0);
        list.add(first);
        return first;
    }

    public void remove(K value) {
        list.remove(value);
    }

    public boolean isFullLoad() {
        return list.size() >= maxCount;
    }

    private void notifyDataExpire(K value) {
        if (onExpireListener != null) {
            onExpireListener.onExpire(value);
        }
    }

    public void setOnExpireListener(OnExpireListener<K> l) {
        this.onExpireListener = l;
    }

    public interface OnExpireListener<T> {
        void onExpire(T value);
    }
}
