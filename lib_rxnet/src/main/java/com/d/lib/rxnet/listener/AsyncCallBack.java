package com.d.lib.rxnet.listener;

import io.reactivex.annotations.NonNull;

/**
 * AsyncCallBack
 * Created by D on 2017/10/24.
 */
public interface AsyncCallBack<T, R> extends SimpleCallBack<R> {
    R apply(@NonNull T t) throws Exception;
}
