package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.callback.SimpleCallback;

/**
 * Observer with Sync Callback
 */
public class ApiObserver<R> extends AbsObserver<R> {
    private R mData;
    private SimpleCallback<R> mCallback;

    public ApiObserver(SimpleCallback<R> callback) {
        if (callback == null) {
            throw new NullPointerException("This callback must not be null!");
        }
        this.mCallback = callback;
    }

    @Override
    public void onNext(R r) {
        this.mData = r;
        mCallback.onSuccess(r);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        mCallback.onError(e);
    }

    @Override
    public void onComplete() {
    }

    public R getData() {
        return mData;
    }
}
