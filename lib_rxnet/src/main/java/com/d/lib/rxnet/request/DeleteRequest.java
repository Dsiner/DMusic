package com.d.lib.rxnet.request;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.callback.AsyncCallback;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import okhttp3.Interceptor;

/**
 * Created by D on 2017/10/24.
 */
public class DeleteRequest extends HttpRequest<DeleteRequest> {

    public DeleteRequest(String url, Map<String, String> params) {
        super(url, params);
    }

    @Override
    protected void prepare() {
        mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).delete(mUrl, mParams);
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
    public <T> Observable<T> observable(Class<T> clazz) {
        return super.observable(clazz);
    }

    @Override
    public DeleteRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public DeleteRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public DeleteRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public DeleteRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public DeleteRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public DeleteRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public DeleteRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public DeleteRequest addInterceptor(Interceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public DeleteRequest addNetworkInterceptors(Interceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public DeleteRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public DeleteRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static class Singleton extends HttpRequest.Singleton<Singleton> {

        public Singleton(String url, Map<String, String> params) {
            super(url, params);
        }

        @Override
        protected void prepare() {
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).delete(mUrl, mParams);
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
        public <T> Observable<T> observable(Class<T> clazz) {
            return super.observable(clazz);
        }
    }
}
