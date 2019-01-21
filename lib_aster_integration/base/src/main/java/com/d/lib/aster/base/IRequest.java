package com.d.lib.aster.base;

import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * IRequest
 * Created by D on 2017/10/24.
 */
public abstract class IRequest<R extends IRequest, C extends IClient> extends IConfig<R> {
    protected Config mConfig;
    protected String mUrl;
    protected Params mParams;
    protected Object mTag; // Request tag

    /**
     * Get the Client
     *
     * @return Client
     */
    protected abstract C getClient();

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
    protected R headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
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
    protected R addInterceptor(IInterceptor interceptor) {
        mConfig.addInterceptor(interceptor);
        return (R) this;
    }

    @Override
    protected R addNetworkInterceptors(IInterceptor interceptor) {
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
}
