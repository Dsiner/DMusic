package com.d.lib.aster.request;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.IRequest;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.ProgressCallback;
import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by D on 2017/10/24.
 */
public abstract class IUploadRequest<HR extends IUploadRequest, C extends IClient>
        extends IRequest<HR, C> {

    public IUploadRequest(String url) {
        this(url, null);
    }

    public IUploadRequest(String url, Config config) {
        this.mUrl = url;
        this.mParams = new Params();
        this.mConfig = config != null ? config : Config.getDefault();
    }

    protected abstract void prepare();

    public void request() {
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

    public HR addParam(String paramKey, String paramValue) {
        return (HR) this;
    }

    public HR addFile(String key, File file) {
        return (HR) this;
    }

    public HR addFile(String key, File file, ProgressCallback callback) {
        return (HR) this;
    }

    public HR addImageFile(String key, File file) {
        return (HR) this;
    }

    public HR addImageFile(String key, File file, ProgressCallback callback) {
        return (HR) this;
    }

    public HR addBytes(String key, byte[] bytes, String name) {
        return (HR) this;
    }

    public HR addBytes(String key, byte[] bytes, String name, ProgressCallback callback) {
        return (HR) this;
    }

    public HR addStream(String key, InputStream inputStream, String name) {
        return (HR) this;
    }

    public HR addStream(String key, InputStream inputStream, String name, ProgressCallback callback) {
        return (HR) this;
    }

    /**
     * Singleton
     */
    public abstract static class Singleton<HRF extends Singleton, C extends IClient>
            extends IRequest<HRF, C> {

        public Singleton(String url) {
            this.mUrl = url;
            this.mParams = new Params();
        }

        protected abstract void prepare();

        public void request() {
        }

        @Override
        public HRF tag(Object tag) {
            return super.tag(tag);
        }

        public HRF addParam(String paramKey, String paramValue) {
            return (HRF) this;
        }

        public HRF addFile(String key, File file) {
            return (HRF) this;
        }

        public HRF addFile(String key, File file, ProgressCallback callback) {
            return (HRF) this;
        }

        public HRF addImageFile(String key, File file) {
            return (HRF) this;
        }

        public HRF addImageFile(String key, File file, ProgressCallback callback) {
            return (HRF) this;
        }

        public HRF addBytes(String key, byte[] bytes, String name) {
            return (HRF) this;
        }

        public HRF addBytes(String key, byte[] bytes, String name, ProgressCallback callback) {
            return (HRF) this;
        }

        public HRF addStream(String key, InputStream inputStream, String name) {
            return (HRF) this;
        }

        public HRF addStream(String key, InputStream inputStream, String name, ProgressCallback callback) {
            return (HRF) this;
        }
    }
}
