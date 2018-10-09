package com.d.lib.common.component.cache.listener;

/**
 * Created by D on 2017/10/19.
 */
public interface CacheListener<T> {
    void onLoading();

    void onSuccess(T result);

    void onError(Throwable e);
}
