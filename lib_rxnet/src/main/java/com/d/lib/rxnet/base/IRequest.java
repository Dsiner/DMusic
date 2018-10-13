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

/**
 * IRequest
 * Created by D on 2017/10/24.
 */
public abstract class IRequest<R extends IRequest> extends IConfig<R> {
    protected HttpConfig mConfig;
    protected String mUrl;
    protected Map<String, String> mParams;
    protected Observable<ResponseBody> mObservable;
    protected Object mTag; // Request tag

    /**
     * Get the Client
     *
     * @return Retrofit
     */
    protected abstract HttpClient getClient();

    /**
     * Set request tag
     */
    public R tag(Object tag) {
        this.mTag = tag;
        return (R) this;
    }

    /**
     * Get request tag
     */
    public Object getTag() {
        return mTag;
    }

    @Override
    protected R baseUrl(String baseUrl) {
        mConfig.baseUrl(baseUrl);
        return (R) this;
    }

    @Override
    protected R headers(Map<String, String> headers) {
        mConfig.headers(headers);
        return (R) this;
    }

    @Override
    protected R headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        mConfig.headers(onHeadInterceptor);
        return (R) this;
    }

    @Override
    protected R connectTimeout(long timeout) {
        mConfig.connectTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R readTimeout(long timeout) {
        mConfig.readTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R writeTimeout(long timeout) {
        mConfig.writeTimeout(timeout);
        return (R) this;
    }

    @Override
    protected R sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        mConfig.sslSocketFactory(sslSocketFactory);
        return (R) this;
    }

    @Override
    protected R addInterceptor(Interceptor interceptor) {
        mConfig.addInterceptor(interceptor);
        return (R) this;
    }

    @Override
    protected R addNetworkInterceptors(Interceptor interceptor) {
        mConfig.addNetworkInterceptors(interceptor);
        return (R) this;
    }

    @Override
    protected R retryCount(int retryCount) {
        mConfig.retryCount(retryCount);
        return (R) this;
    }

    @Override
    protected R retryDelayMillis(long retryDelayMillis) {
        mConfig.retryDelayMillis(retryDelayMillis);
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
                        .retryWhen(new ApiRetryFunc(mConfig.retryCount, mConfig.retryDelayMillis));
            }
        };
    }
}
