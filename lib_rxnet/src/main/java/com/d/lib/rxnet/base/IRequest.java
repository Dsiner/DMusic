package com.d.lib.rxnet.base;

import com.d.lib.rxnet.func.ApiFunc;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * IRequest
 * Created by D on 2017/10/24.
 */
public abstract class IRequest<R extends IRequest> extends IConfig<R> {
    protected HttpConfig config;
    protected String url;
    protected Observable observable;
    protected Object tag; // Request tag

    /**
     * Get the Client
     *
     * @return Retrofit
     */
    protected abstract Retrofit getClient();

    /**
     * Set request tag
     */
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    /**
     * Get request tag
     */
    public Object getTag() {
        return tag;
    }

    @Override
    protected R baseUrl(String baseUrl) {
        config.baseUrl(baseUrl);
        return (R) this;
    }

    @Override
    protected R headers(Map<String, String> headers) {
        config.headers(headers);
        return (R) this;
    }

    @Override
    protected R headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        config.headers(onHeadInterceptor);
        return (R) this;
    }

    @Override
    protected R connectTimeout(long timeout) {
        config.connectTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R readTimeout(long timeout) {
        config.readTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R writeTimeout(long timeout) {
        config.writeTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        config.sslSocketFactory(sslSocketFactory);
        return (R) this;
    }

    @Override
    protected R addInterceptor(Interceptor interceptor) {
        config.addInterceptor(interceptor);
        return (R) this;
    }

    @Override
    protected R addNetworkInterceptors(Interceptor interceptor) {
        config.addNetworkInterceptors(interceptor);
        return (R) this;
    }

    @Override
    protected R retryCount(int retryCount) {
        config.retryCount(retryCount);
        return (R) this;
    }

    @Override
    protected R retryDelayMillis(long retryDelayMillis) {
        config.retryDelayMillis(retryDelayMillis);
        return (R) this;
    }

    /**
     * e.g observable.compose(this.<T>norTransformer(callback))
     */
    protected <OTF> ObservableTransformer<ResponseBody, OTF> norTransformer(final Class<OTF> clazz) {
        return new ObservableTransformer<ResponseBody, OTF>() {
            @Override
            public ObservableSource<OTF> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new ApiFunc<OTF>(clazz))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis));
            }
        };
    }
}
