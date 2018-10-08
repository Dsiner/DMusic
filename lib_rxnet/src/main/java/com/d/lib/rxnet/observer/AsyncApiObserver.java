package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.callback.AsyncCallback;

/**
 * Observer with Async Callback
 */
public class AsyncApiObserver<T, R> extends AbsObserver<R> {
    private R data;
    private AsyncCallback<T, R> callback;

    public AsyncApiObserver(AsyncCallback<T, R> callback) {
        if (callback == null) {
            throw new NullPointerException("This callback must not be null!");
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
