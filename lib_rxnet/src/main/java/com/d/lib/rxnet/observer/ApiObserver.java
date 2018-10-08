package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.callback.SimpleCallback;

/**
 * Observer with Sync Callback
 */
public class ApiObserver<R> extends AbsObserver<R> {
    private R data;
    private SimpleCallback<R> callback;

    public ApiObserver(SimpleCallback<R> callback) {
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
