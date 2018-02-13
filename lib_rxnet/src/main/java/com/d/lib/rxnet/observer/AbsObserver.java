package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.exception.ApiException;
import com.d.lib.rxnet.util.RxLog;

import io.reactivex.observers.DisposableObserver;

/**
 * Abs Observer
 */
abstract class AbsObserver<T> extends DisposableObserver<T> {

    AbsObserver() {
    }

    @Override
    public void onError(Throwable e) {
        //print error log
        if (e instanceof ApiException) {
            e.printStackTrace();
        }
        RxLog.e(e.getMessage());
    }
}
