package com.d.lib.aster.integration.retrofit.observer;

import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.integration.retrofit.RequestManager;

/**
 * Observer with Async Callback
 */
public class AsyncApiObserver<T, R> extends AbsObserver<R> {
    private R mData;
    private Object mTag; // Request tag
    private AsyncCallback<T, R> mCallback;

    public AsyncApiObserver(Object tag, AsyncCallback<T, R> callback) {
        if (callback == null) {
            throw new NullPointerException("This callback must not be null!");
        }
        this.mTag = tag;
        this.mCallback = callback;
    }

    @Override
    public void onNext(R r) {
        RequestManager.getIns().cancel(mTag);
        this.mData = r;
        mCallback.onSuccess(r);
    }

    @Override
    public void onError(Throwable e) {
        RequestManager.getIns().cancel(mTag);
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
