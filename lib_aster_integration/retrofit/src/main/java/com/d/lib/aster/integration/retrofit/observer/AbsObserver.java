package com.d.lib.aster.integration.retrofit.observer;

import com.d.lib.aster.integration.retrofit.exception.ApiException;
import com.d.lib.aster.utils.ULog;

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
