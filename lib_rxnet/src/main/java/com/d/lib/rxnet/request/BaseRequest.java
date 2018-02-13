package com.d.lib.rxnet.request;

import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.func.ApiFunc;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.listener.ConfigListener;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * BaseRequest
 * Created by D on 2017/10/24.
 */
public abstract class BaseRequest<R extends BaseRequest> extends ConfigListener<R> {
    protected HttpConfig config;
    protected String url;
    protected Observable observable;
    protected Object tag;//请求标签

    /**
     * 设置请求标签
     */
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    /**
     * e.g observable.compose(this.<T>norTransformer(callback))
     */
    protected <T> ObservableTransformer<ResponseBody, T> norTransformer(final Class<T> clazz) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new ApiFunc<T>(clazz))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis));
            }
        };
    }
}
