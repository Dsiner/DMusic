package com.d.lib.aster.integration.retrofit.request;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.retrofit.RequestManager;
import com.d.lib.aster.integration.retrofit.RetrofitClient;
import com.d.lib.aster.integration.retrofit.func.ApiFunc;
import com.d.lib.aster.integration.retrofit.func.ApiRetryFunc;
import com.d.lib.aster.integration.retrofit.func.MapFunc;
import com.d.lib.aster.integration.retrofit.observer.ApiObserver;
import com.d.lib.aster.integration.retrofit.observer.AsyncApiObserver;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.request.IHttpRequest;
import com.d.lib.aster.utils.Util;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by D on 2017/10/24.
 */
public abstract class HttpRequest<HR extends HttpRequest>
        extends IHttpRequest<HR, RetrofitClient> {
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
    protected RetrofitClient getClient() {
        return RetrofitClient.create(IClient.TYPE_NORMAL, mConfig.log(true));
    }

    /**
     * Initialize Observable, etc.
     */
    protected abstract void prepare();

    @Override
    public <T> void request(SimpleCallback<T> callback) {
        prepare();
        DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(mConfig.retryCount, mConfig.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    @Override
    public <T, R> void request(AsyncCallback<T, R> callback) {
        prepare();
        DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .map(new MapFunc<T, R>(callback))
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(mConfig.retryCount, mConfig.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    @Override
    public <T> com.d.lib.aster.scheduler.Observable.Observe<T> observable(Class<T> clazz) {
        return super.observable(clazz);
    }

    public <T> Observable<T> observableRx(Class<T> clazz) {
        prepare();
        return mObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(clazz))
                .retryWhen(new ApiRetryFunc(mConfig.retryCount, mConfig.retryDelayMillis));
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
            extends IHttpRequest.Singleton<HRF, RetrofitClient> {
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
        protected RetrofitClient getClient() {
            return RetrofitClient.getDefault(IClient.TYPE_NORMAL);
        }

        /**
         * Initialize Observable, etc.
         */
        protected abstract void prepare();

        @Override
        public <T> void request(SimpleCallback<T> callback) {
            prepare();
            DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new ApiRetryFunc(getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis))
                    .subscribe(disposableObserver);
        }

        @Override
        public <T, R> void request(AsyncCallback<T, R> callback) {
            prepare();
            DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .map(new MapFunc<T, R>(callback))
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new ApiRetryFunc(getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis))
                    .subscribe(disposableObserver);
        }

        @Override
        public <T> com.d.lib.aster.scheduler.Observable.Observe<T> observable(Class<T> clazz) {
            return super.observable(clazz);
        }

        public <T> Observable<T> observableRx(Class<T> clazz) {
            prepare();
            return mObservable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(clazz))
                    .retryWhen(new ApiRetryFunc(getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis));
        }
    }
}
