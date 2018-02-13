package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.listener.UploadCallBack;
import com.d.lib.rxnet.util.RxUtil;

/**
 * Observer with Upload CallBack
 * Created by D on 2017/10/26.
 */
public class UploadObserver extends AbsObserver<Object> {
    private UploadCallBack callback;

    public UploadObserver(UploadCallBack callback) {
        this.callback = callback;
    }

    @Override
    public void onNext(Object o) {
        RxUtil.printThread("RxNet_theard uploadOnNext: ");
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
