package com.d.lib.rxnet.func;

import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.utils.ULog;
import com.d.lib.rxnet.utils.Util;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Retry Func
 */
public class ApiRetryFunc implements Function<Observable<? extends Throwable>, Observable<?>> {
    private final int mMaxRetries;
    private final long mRetryDelayMillis;
    private int mRetryCount;

    public ApiRetryFunc(int maxRetries, long retryDelayMillis) {
        this.mMaxRetries = maxRetries != -1 ? maxRetries : HttpConfig.getDefault().retryCount;
        this.mRetryDelayMillis = retryDelayMillis != -1 ? retryDelayMillis : HttpConfig.getDefault().retryDelayMillis;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        Util.printThread("RxNet_theard retryInit");

        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                Util.printThread("RxNet_theard retryApply");
                if (++mRetryCount <= mMaxRetries && (throwable instanceof SocketTimeoutException
                        || throwable instanceof ConnectException)) {
                    ULog.d("Get response data error, it will try after " + mRetryDelayMillis
                            + " millisecond, retry count " + mRetryCount + "/" + mMaxRetries);
                    return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
                }
                return Observable.error(throwable);
            }
        });
    }
}
