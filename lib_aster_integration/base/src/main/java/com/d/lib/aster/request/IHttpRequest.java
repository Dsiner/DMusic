package com.d.lib.aster.request;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.IRequest;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.scheduler.Observable;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by D on 2017/10/24.
 */
public abstract class IHttpRequest<HR extends IHttpRequest, C extends IClient>
        extends IRequest<HR, C> {

    private IHttpRequest() {
    }

    public IHttpRequest(String url) {
        this(url, null);
    }

    public IHttpRequest(String url, Params params) {
        this(url, params, null);
    }

    public IHttpRequest(String url, Params params, Config config) {
        this.mUrl = url;
        this.mParams = params;
        this.mConfig = config != null ? config : Config.getDefault();
    }

    /**
     * Initialize Observable, etc.
     */
    protected abstract void prepare();

    public <T> void request(final SimpleCallback<T> callback) {
    }

    public <T, R> void request(final AsyncCallback<T, R> callback) {
    }

    public <T> Observable.Observe<T> observable(Class<T> clazz) {
        return null;
    }

    @Override
    public HR baseUrl(String baseUrl) {
        mConfig.baseUrl(baseUrl);
        return (HR) this;
    }

    @Override
    public HR headers(Map<String, String> headers) {
        mConfig.headers(headers);
        return (HR) this;
    }

    @Override
    public HR headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        mConfig.headers(onHeadInterceptor);
        return (HR) this;
    }

    @Override
    public HR connectTimeout(long timeout) {
        mConfig.connectTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR readTimeout(long timeout) {
        mConfig.readTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR writeTimeout(long timeout) {
        mConfig.writeTimeout(timeout);
        return (HR) this;
    }

    @Override
    public HR sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        mConfig.sslSocketFactory(sslSocketFactory);
        return (HR) this;
    }

    @Override
    public HR addInterceptor(IInterceptor interceptor) {
        mConfig.addInterceptor(interceptor);
        return (HR) this;
    }

    @Override
    public HR addNetworkInterceptors(IInterceptor interceptor) {
        mConfig.addNetworkInterceptors(interceptor);
        return (HR) this;
    }

    @Override
    public HR retryCount(int retryCount) {
        mConfig.retryCount(retryCount);
        return (HR) this;
    }

    @Override
    public HR retryDelayMillis(long retryDelayMillis) {
        mConfig.retryDelayMillis(retryDelayMillis);
        return (HR) this;
    }

    /**
     * Singleton
     */
    public static abstract class Singleton<HRF extends Singleton, C extends IClient>
            extends IRequest<HRF, C> {

        private Singleton() {
        }

        public Singleton(String url) {
            this(url, null);
        }

        public Singleton(String url, Params params) {
            this(url, params, null);
        }

        public Singleton(String url, Params params, Config config) {
            this.mUrl = url;
            this.mParams = params;
            this.mConfig = config != null ? config : Config.getDefault();
        }

        /**
         * Initialize Observable, etc.
         */
        protected abstract void prepare();

        public <T> void request(final SimpleCallback<T> callback) {
        }

        public <T, R> void request(final AsyncCallback<T, R> callback) {
        }

        public <T> Observable.Observe<T> observable(Class<T> clazz) {
            return null;
        }
    }
}
