package com.d.lib.rxnet.func;

import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.util.RxLog;
import com.d.lib.rxnet.util.RxUtil;

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
    private final int maxRetries;
    private final long retryDelayMillis;
    private int retryCount;

    public ApiRetryFunc(int maxRetries, long retryDelayMillis) {
        this.maxRetries = maxRetries != -1 ? maxRetries : HttpConfig.getDefaultConfig().retryCount;
        this.retryDelayMillis = retryDelayMillis != -1 ? retryDelayMillis : HttpConfig.getDefaultConfig().retryDelayMillis;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        RxUtil.printThread("RxNet_theard retryInit: ");

        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                RxUtil.printThread("RxNet_theard retryApply: ");
                if (++retryCount <= maxRetries && (throwable instanceof SocketTimeoutException
                        || throwable instanceof ConnectException)) {
                    RxLog.d("get response data error, it will try after " + retryDelayMillis
                            + " millisecond, retry count " + retryCount + "/" + maxRetries);
                    return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                }
                return Observable.error(throwable);
            }
        });
    }
}
