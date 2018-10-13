package com.d.lib.rxnet.observer;

import android.support.annotation.Nullable;

import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.lib.rxnet.utils.Util;

import okhttp3.ResponseBody;

/**
 * Observer with Upload Callback
 * Created by D on 2017/10/26.
 */
public class UploadObserver extends AbsObserver<ResponseBody> {
    private final SimpleCallback<ResponseBody> mCallback;

    public UploadObserver(@Nullable SimpleCallback<ResponseBody> callback) {
        this.mCallback = callback;
    }

    @Override
    public void onNext(ResponseBody o) {
        Util.printThread("RxNet_theard uploadOnNext");
        if (mCallback == null) {
            return;
        }
        mCallback.onSuccess(o);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (mCallback == null) {
            return;
        }
        mCallback.onError(e);
    }

    @Override
    public void onComplete() {

    }
}
