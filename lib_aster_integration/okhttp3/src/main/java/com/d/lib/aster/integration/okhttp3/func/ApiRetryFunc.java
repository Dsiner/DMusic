package com.d.lib.aster.integration.okhttp3.func;

import android.support.annotation.NonNull;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.scheduler.Observable;
import com.d.lib.aster.scheduler.callback.DisposableObserver;
import com.d.lib.aster.utils.ULog;
import com.d.lib.aster.utils.Util;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Retry Func
 */
public class ApiRetryFunc<T> extends DisposableObserver<T> {
    private final OnRetry<T> mObserve;
    private final DisposableObserver<T> mDisposableObserver;
    private final int mMaxRetries;
    private final long mRetryDelayMillis;
    private int mRetryCount;

    public ApiRetryFunc(@NonNull DisposableObserver<T> disposableObserver,
                        int maxRetries, long retryDelayMillis,
                        @NonNull OnRetry<T> observe) {
        this.mObserve = observe;
        this.mDisposableObserver = disposableObserver;
        this.mMaxRetries = maxRetries != -1 ? maxRetries : Config.getDefault().retryCount;
        this.mRetryDelayMillis = retryDelayMillis != -1 ? retryDelayMillis : Config.getDefault().retryDelayMillis;
    }

    @Override
    public void onNext(@NonNull T result) {
        mDisposableObserver.onNext(result);
    }

    @Override
    public void onError(Throwable e) {
        Util.printThread("Aster_thread retryInit");
        Util.printThread("Aster_thread retryApply");
        if (++mRetryCount <= mMaxRetries && (e instanceof SocketTimeoutException
                || e instanceof ConnectException)) {
            ULog.d("Get response data error, it will try after " + mRetryDelayMillis
                    + " millisecond, retry count " + mRetryCount + "/" + mMaxRetries);
            mRetryCount++;
            mObserve.observe().subscribe(this);
            return;
        }
        mDisposableObserver.onError(e);
    }

    public interface OnRetry<T> {
        @NonNull
        Observable.Observe<T> observe();
    }
}
