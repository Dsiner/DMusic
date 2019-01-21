package com.d.lib.aster.integration.okhttp3.request;

import android.support.annotation.NonNull;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.integration.okhttp3.OkHttpClient;
import com.d.lib.aster.integration.okhttp3.RequestManager;
import com.d.lib.aster.integration.okhttp3.func.ApiFunc;
import com.d.lib.aster.integration.okhttp3.func.ApiRetryFunc;
import com.d.lib.aster.integration.okhttp3.func.MapFunc;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.okhttp3.observer.ApiObserver;
import com.d.lib.aster.integration.okhttp3.observer.AsyncApiObserver;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.request.IHttpRequest;
import com.d.lib.aster.scheduler.Observable;
import com.d.lib.aster.scheduler.callback.DisposableObserver;
import com.d.lib.aster.scheduler.schedule.Schedulers;
import com.d.lib.aster.utils.Util;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.ResponseBody;

/**
 * Created by D on 2017/10/24.
 */
public abstract class HttpRequest<HR extends HttpRequest> extends IHttpRequest<HR, OkHttpClient> {
    protected Observable<ResponseBody> mObservable;

    public HttpRequest(String url) {
        super(url);
    }

    public HttpRequest(String url, Params params) {
        super(url, params);
    }

    public HttpRequest(String url, Params params, Config config) {
        super(url, params, config);
    }

    @Override
    protected OkHttpClient getClient() {
        return OkHttpClient.create(IClient.TYPE_NORMAL, mConfig.log(true));
    }

    @Override
    public <T> void request(final SimpleCallback<T> callback) {
        prepare();
        DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .observeOn(Schedulers.mainThread())
                .subscribe(new ApiRetryFunc<T>(disposableObserver,
                        mConfig.retryCount,
                        mConfig.retryDelayMillis,
                        new ApiRetryFunc.OnRetry<T>() {
                            @NonNull
                            @Override
                            public Observable.Observe<T> observe() {
                                return mObservable.subscribeOn(Schedulers.io())
                                        .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                        .observeOn(Schedulers.mainThread());
                            }
                        }));
    }

    @Override
    public <T, R> void request(final AsyncCallback<T, R> callback) {
        prepare();
        DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .map(new MapFunc<T, R>(callback))
                .observeOn(Schedulers.mainThread())
                .subscribe(new ApiRetryFunc<R>(disposableObserver,
                        mConfig.retryCount, mConfig.retryDelayMillis,
                        new ApiRetryFunc.OnRetry<R>() {
                            @NonNull
                            @Override
                            public Observable.Observe<R> observe() {
                                return mObservable.subscribeOn(Schedulers.io())
                                        .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                        .map(new MapFunc<T, R>(callback))
                                        .observeOn(Schedulers.mainThread());
                            }
                        }));
    }

    @Override
    public <T> Observable.Observe<T> observable(Class<T> clazz) {
        prepare();
        return mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(clazz));
    }

    @Override
    public HR baseUrl(String baseUrl) {
        mConfig.baseUrl(baseUrl);
        return (HR) this;
    }

    @Override
    public HR headers(Map<String, String> headers) {
        mConfig.headers(headers);
        return (HR) this;
    }

    @Override
    public HR headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        mConfig.headers(onHeadInterceptor);
        return (HR) this;
    }

    @Override
    public HR connectTimeout(long timeout) {
        mConfig.connectTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR readTimeout(long timeout) {
        mConfig.readTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR writeTimeout(long timeout) {
        mConfig.writeTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        mConfig.sslSocketFactory(sslSocketFactory);
        return (HR) this;
    }

    @Override
    public HR addInterceptor(IInterceptor interceptor) {
        mConfig.addInterceptor(interceptor);
        return (HR) this;
    }

    @Override
    public HR addNetworkInterceptors(IInterceptor interceptor) {
        mConfig.addNetworkInterceptors(interceptor);
        return (HR) this;
    }

    @Override
    public HR retryCount(int retryCount) {
        mConfig.retryCount(retryCount);
        return (HR) this;
    }

    @Override
    public HR retryDelayMillis(long retryDelayMillis) {
        mConfig.retryDelayMillis(retryDelayMillis);
        return (HR) this;
    }

    /**
     * Singleton
     */
    public static abstract class Singleton<HRF extends Singleton>
            extends IHttpRequest.Singleton<HRF, OkHttpClient> {
        protected Observable<ResponseBody> mObservable;

        public Singleton(String url) {
            super(url);
        }

        public Singleton(String url, Params params) {
            super(url, params);
        }

        public Singleton(String url, Params params, Config config) {
            super(url, params, config);
        }

        @Override
        protected OkHttpClient getClient() {
            return OkHttpClient.getDefault(IClient.TYPE_NORMAL);
        }

        @Override
        public <T> void request(final SimpleCallback<T> callback) {
            prepare();
            DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .observeOn(Schedulers.mainThread())
                    .subscribe(new ApiRetryFunc<T>(disposableObserver,
                            getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis,
                            new ApiRetryFunc.OnRetry<T>() {
                                @NonNull
                                @Override
                                public Observable.Observe<T> observe() {
                                    return mObservable.subscribeOn(Schedulers.io())
                                            .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                            .observeOn(Schedulers.mainThread());
                                }
                            }));
        }

        @Override
        public <T, R> void request(final AsyncCallback<T, R> callback) {
            prepare();
            DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .map(new MapFunc<T, R>(callback))
                    .observeOn(Schedulers.mainThread())
                    .subscribe(new ApiRetryFunc<R>(disposableObserver,
                            getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis,
                            new ApiRetryFunc.OnRetry<R>() {
                                @NonNull
                                @Override
                                public Observable.Observe<R> observe() {
                                    return mObservable.subscribeOn(Schedulers.io())
                                            .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                            .map(new MapFunc<T, R>(callback))
                                            .observeOn(Schedulers.mainThread());
                                }
                            }));
        }

        @Override
        public <T> Observable.Observe<T> observable(Class<T> clazz) {
            prepare();
            return mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(clazz));
        }
    }
}
