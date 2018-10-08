package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.callback.UploadCallback;
import com.d.lib.rxnet.utils.Util;

/**
 * Observer with Upload Callback
 * Created by D on 2017/10/26.
 */
public class UploadObserver extends AbsObserver<Object> {
    private UploadCallback callback;

    public UploadObserver(UploadCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onNext(Object o) {
        Util.printThread("RxNet_theard uploadOnNext");
        callback.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        callback.onError(e);
    }

    @Override
    public void onComplete() {

    }
}
