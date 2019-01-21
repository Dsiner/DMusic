package com.d.lib.aster.integration.retrofit.request;

import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.retrofit.RetrofitAPI;
import com.d.lib.aster.interceptor.IInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;

/**
 * Created by D on 2017/10/24.
 */
public class HeadRequest extends HttpRequest<HeadRequest> {

    public HeadRequest(String url, Params params) {
        super(url, params);
    }

    @Override
    protected void prepare() {
        mObservable = getClient().getClient().create(RetrofitAPI.class).head(mUrl, mParams);
    }

    @Override
    public <T> void request(SimpleCallback<T> callback) {
        super.request(callback);
    }

    @Override
    public <T, R> void request(AsyncCallback<T, R> callback) {
        super.request(callback);
    }

    @Override
    public <T> com.d.lib.aster.scheduler.Observable.Observe<T> observable(Class<T> clazz) {
        return super.observable(clazz);
    }

    @Override
    public <T> Observable<T> observableRx(Class<T> clazz) {
        return super.observableRx(clazz);
    }

    @Override
    public HeadRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public HeadRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public HeadRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public HeadRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public HeadRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public HeadRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public HeadRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public HeadRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public HeadRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public HeadRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public HeadRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static class Singleton extends HttpRequest.Singleton<Singleton> {

        public Singleton(String url, Params params) {
            super(url, params);
        }

        @Override
        protected void prepare() {
            mObservable = getClient().getClient().create(RetrofitAPI.class).head(mUrl, mParams);
        }

        @Override
        public <T> void request(SimpleCallback<T> callback) {
            super.request(callback);
        }

        @Override
        public <T, R> void request(AsyncCallback<T, R> callback) {
            super.request(callback);
        }

        @Override
        public <T> com.d.lib.aster.scheduler.Observable.Observe<T> observable(Class<T> clazz) {
            return super.observable(clazz);
        }

        @Override
        public <T> Observable<T> observableRx(Class<T> clazz) {
            return super.observableRx(clazz);
        }
    }
}
