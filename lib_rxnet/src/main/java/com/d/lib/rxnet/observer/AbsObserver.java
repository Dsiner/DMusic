package com.d.lib.rxnet.observer;

import com.d.lib.rxnet.exception.ApiException;
import com.d.lib.rxnet.utils.ULog;

import io.reactivex.observers.DisposableObserver;

/**
 * Abstract Observer
 */
abstract class AbsObserver<T> extends DisposableObserver<T> {

    AbsObserver() {
    }

    @Override
    public void onError(Throwable e) {
        // Print error log
        if (e instanceof ApiException) {
            e.printStackTrace();
        }
        ULog.e(e.getMessage());
    }
}
