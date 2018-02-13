package com.d.lib.rxnet.listener;

/**
 * SimpleCallBack
 * Created by D on 2017/10/24.
 */
public interface SimpleCallBack<R> {
    void onSuccess(R response);

    void onError(Throwable e);
}
