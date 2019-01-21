package com.d.lib.aster.base;

import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * IConfig
 * Created by D on 2017/10/25.
 */
public abstract class IConfig<R> {
    protected abstract R baseUrl(String baseUrl);

    protected abstract R headers(Map<String, String> headers);

    protected abstract R headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor);

    protected abstract R connectTimeout(long timeout);

    protected abstract R readTimeout(long timeout);

    protected abstract R writeTimeout(long timeout);

    protected abstract R sslSocketFactory(SSLSocketFactory sslSocketFactory);

    protected abstract R addInterceptor(IInterceptor interceptor);

    protected abstract R addNetworkInterceptors(IInterceptor interceptor);

    /************************** Retry **************************/
    protected abstract R retryCount(int retryCount);

    protected abstract R retryDelayMillis(long retryDelayMillis);
}
