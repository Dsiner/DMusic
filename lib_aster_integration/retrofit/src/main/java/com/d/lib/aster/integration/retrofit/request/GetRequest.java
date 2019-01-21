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
public class GetRequest extends HttpRequest<GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    public GetRequest(String url, Params params) {
        super(url, params);
    }

    @Override
    protected void prepare() {
        if (mParams == null || mParams.size() <= 0) {
            mObservable = getClient().getClient().create(RetrofitAPI.class).get(mUrl);
        } else {
            mObservable = getClient().getClient().create(RetrofitAPI.class).get(mUrl, mParams);
        }
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
    public GetRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public GetRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public GetRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public GetRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public GetRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public GetRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public GetRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public GetRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public GetRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public GetRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public GetRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static class Singleton extends HttpRequest.Singleton<Singleton> {

        public Singleton(String url) {
            super(url);
        }

        public Singleton(String url, Params params) {
            super(url, params);
        }

        @Override
        protected void prepare() {
            if (mParams == null || mParams.size() <= 0) {
                mObservable = getClient().getClient().create(RetrofitAPI.class).get(mUrl);
            } else {
                mObservable = getClient().getClient().create(RetrofitAPI.class).get(mUrl, mParams);
            }
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
