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
public class PatchRequest extends HttpRequest<PatchRequest> {

    public PatchRequest(String url, Params params) {
        super(url, params);
    }

    @Override
    protected void prepare() {
        mObservable = getClient().getClient().create(RetrofitAPI.class).patch(mUrl, mParams);
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
    public PatchRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public PatchRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public PatchRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public PatchRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public PatchRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public PatchRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public PatchRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public PatchRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public PatchRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public PatchRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public PatchRequest retryDelayMillis(long retryDelayMillis) {
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
            mObservable = getClient().getClient().create(RetrofitAPI.class).patch(mUrl, mParams);
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
