package com.d.lib.rxnet.request;

import com.d.lib.rxnet.base.HttpClient;
import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.IRequest;
import com.d.lib.rxnet.base.RequestManager;
import com.d.lib.rxnet.callback.AsyncCallback;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.lib.rxnet.func.ApiFunc;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.func.MapFunc;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.observer.ApiObserver;
import com.d.lib.rxnet.observer.AsyncApiObserver;
import com.d.lib.rxnet.utils.Util;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;

/**
 * Created by D on 2017/10/24.
 */
public abstract class HttpRequest<HR extends HttpRequest> extends IRequest<HR> {

    private HttpRequest() {
    }

    public HttpRequest(String url) {
        this(url, null);
    }

    public HttpRequest(String url, Map<String, String> params) {
        this(url, params, null);
    }

    public HttpRequest(String url, Map<String, String> params, HttpConfig config) {
        this.mUrl = url;
        this.mParams = params;
        this.mConfig = config != null ? config : HttpConfig.getDefault();
    }

    @Override
    protected HttpClient getClient() {
        return HttpClient.create(HttpClient.TYPE_NORMAL, mConfig.log(true));
    }

    /**
     * Initialize Observable, etc.
     */
    protected abstract void prepare();

    public <T> void request(SimpleCallback<T> callback) {
        prepare();
        DisposableObserver<T> disposableObserver = new ApiObserver<T>(callback);
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

    public <T, R> void request(AsyncCallback<T, R> callback) {
        prepare();
        DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(callback);
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

    public <T> Observable<T> observable(Class<T> clazz) {
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
    public HR addInterceptor(Interceptor interceptor) {
        mConfig.addInterceptor(interceptor);
        return (HR) this;
    }

    @Override
    public HR addNetworkInterceptors(Interceptor interceptor) {
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
    public static abstract class Singleton<HRF extends IRequest> extends IRequest<HRF> {

        private Singleton() {
        }

        public Singleton(String url) {
            this(url, null);
        }

        public Singleton(String url, Map<String, String> params) {
            this(url, params, null);
        }

        public Singleton(String url, Map<String, String> params, HttpConfig config) {
            this.mUrl = url;
            this.mParams = params;
            this.mConfig = config != null ? config : HttpConfig.getDefault();
        }

        @Override
        protected HttpClient getClient() {
            return HttpClient.getDefault(HttpClient.TYPE_NORMAL);
        }

        /**
         * Initialize Observable, etc.
         */
        protected abstract void prepare();

        public <T> void request(SimpleCallback<T> callback) {
            prepare();
            DisposableObserver<T> disposableObserver = new ApiObserver<T>(callback);
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

        public <T, R> void request(AsyncCallback<T, R> callback) {
            prepare();
            DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(callback);
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

        public <T> Observable<T> observable(Class<T> clazz) {
            prepare();
            return mObservable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(clazz))
                    .retryWhen(new ApiRetryFunc(getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis));
        }
    }
}
