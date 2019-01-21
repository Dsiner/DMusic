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
public class OptionRequest extends HttpRequest<OptionRequest> {

    public OptionRequest(String url, Params params) {
        super(url, params);
    }

    @Override
    protected void prepare() {
        mObservable = getClient().getClient().create(RetrofitAPI.class).options(mUrl, mParams);
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
    public OptionRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public OptionRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public OptionRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public OptionRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public OptionRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public OptionRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public OptionRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public OptionRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public OptionRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public OptionRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public OptionRequest retryDelayMillis(long retryDelayMillis) {
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
            mObservable = getClient().getClient().create(RetrofitAPI.class).options(mUrl, mParams);
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
