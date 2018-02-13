package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.listener.AsyncCallBack;

/**
 * Observer with Async AsyncCallBack
 */
public class AsyncApiObserver<T, R> extends AbsObserver<R> {
    private R data;
    private AsyncCallBack<T, R> callback;

    public AsyncApiObserver(AsyncCallBack<T, R> callback) {
        if (callback == null) {
            throw new NullPointerException("this callback is null!");
        }
        this.callback = callback;
    }

    @Override
    public void onNext(R r) {
        this.data = r;
        callback.onSuccess(r);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        callback.onError(e);
    }

    @Override
    public void onComplete() {
    }

    public R getData() {
        return data;
    }
}
