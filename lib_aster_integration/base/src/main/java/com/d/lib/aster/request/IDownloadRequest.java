package com.d.lib.aster.request;

import android.support.annotation.NonNull;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.IRequest;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.ProgressCallback;
import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by D on 2017/10/24.
 */
public abstract class IDownloadRequest<HR extends IDownloadRequest, C extends IClient>
        extends IRequest<HR, C> {

    public IDownloadRequest(String url) {
        this(url, null);
    }

    public IDownloadRequest(String url, Params params) {
        this(url, params, null);
    }

    public IDownloadRequest(String url, Params params, Config config) {
        this.mUrl = url;
        this.mParams = params;
        this.mConfig = config != null ? config : Config.getDefault();
    }

    protected abstract void prepare();

    @SuppressWarnings("ConstantConditions")
    public void request(@NonNull final String path, @NonNull final String name,
                        @NonNull final ProgressCallback callback) {
    }

    @Override
    public HR tag(Object tag) {
        return super.tag(tag);
    }

    @Override
    public HR baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public HR headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public HR headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public HR connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public HR readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public HR writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public HR sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public HR addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public HR addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public HR retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public HR retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static abstract class Singleton<HRF extends Singleton, C extends IClient>
            extends IRequest<HRF, C> {

        public Singleton(String url) {
            this(url, null);
        }

        public Singleton(String url, Params params) {
            this.mUrl = url;
            this.mParams = params;
        }

        protected abstract void prepare();

        @SuppressWarnings("ConstantConditions")
        public void request(@NonNull final String path, @NonNull final String name,
                            @NonNull final ProgressCallback callback) {
        }

        @Override
        public HRF tag(Object tag) {
            return super.tag(tag);
        }
    }
}
