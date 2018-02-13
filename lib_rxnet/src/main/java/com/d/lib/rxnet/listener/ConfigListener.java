package com.d.lib.rxnet.listener;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Interceptor;

/**
 * ConfigListener
 * Created by D on 2017/10/25.
 */
public abstract class ConfigListener<R> {
    protected abstract R baseUrl(String baseUrl);

    protected abstract R headers(Map<String, String> headers);

    /*************************** OkHttpClient ***************************/
    protected abstract R connectTimeout(long timeout);

    protected abstract R readTimeout(long timeout);

    protected abstract R writeTimeout(long timeout);

    protected abstract R addInterceptor(Interceptor interceptor);

    protected abstract R addNetworkInterceptors(Interceptor interceptor);

    protected abstract R sslSocketFactory(SSLSocketFactory sslSocketFactory);

    /*************************** Retry ***************************/
    protected abstract R retryCount(int retryCount);

    protected abstract R retryDelayMillis(long retryDelayMillis);
}
