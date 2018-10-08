package com.d.lib.rxnet.base;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.utils.SSLUtil;
import com.d.lib.rxnet.utils.ULog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * HttpConfig
 * Created by D on 2017/10/24.
 */
public class HttpConfig extends IConfig<HttpConfig> {
    public String baseUrl;
    public Map<String, String> headers = new LinkedHashMap<>();
    public HeadersInterceptor.OnHeadInterceptor onHeadInterceptor;

    public long connectTimeout = -1;
    public long readTimeout = -1;
    public long writeTimeout = -1;

    public int retryCount = -1;
    public long retryDelayMillis = -1;

    public SSLSocketFactory sslSocketFactory;
    public ArrayList<Interceptor> interceptors = new ArrayList<>();
    public ArrayList<Interceptor> networkInterceptors = new ArrayList<>();


    private static class Singleton {
        private static HttpConfig DEFAULT_CONFIG = new HttpConfig()
                .baseUrl(Config.BASE_URL)
                .connectTimeout(Config.CONNECT_TIMEOUT)
                .readTimeout(Config.READ_TIMEOUT)
                .writeTimeout(Config.WRITE_TIMEOUT)
                .retryCount(Config.RETRY_COUNT)
                .retryDelayMillis(Config.RETRY_DELAY_MILLIS)
                .sslSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
    }

    /**
     * Get the default configuration
     */
    public synchronized static HttpConfig getDefault() {
        return Singleton.DEFAULT_CONFIG;
    }

    /**
     * Get the default configuration - copy
     */
    public static HttpConfig getNewDefault() {
        HttpConfig defaultConfig = getDefault();
        return new HttpConfig()
                .baseUrl(defaultConfig.baseUrl)
                .connectTimeout(defaultConfig.connectTimeout)
                .readTimeout(defaultConfig.readTimeout)
                .writeTimeout(defaultConfig.writeTimeout)
                .retryCount(defaultConfig.retryCount)
                .retryDelayMillis(defaultConfig.retryDelayMillis)
                .sslSocketFactory(defaultConfig.sslSocketFactory);
    }

    private synchronized static void setDefault(@NonNull Builder builder) {
        HttpConfig config = new HttpConfig();

        config.baseUrl = !TextUtils.isEmpty(builder.baseUrl) ? builder.baseUrl : Config.BASE_URL;
        config.headers = builder.headers;
        config.onHeadInterceptor = builder.onHeadInterceptor;

        config.connectTimeout = builder.connectTimeout != -1 ? builder.connectTimeout : Config.CONNECT_TIMEOUT;
        config.readTimeout = builder.readTimeout != -1 ? builder.readTimeout : Config.READ_TIMEOUT;
        config.writeTimeout = builder.writeTimeout != -1 ? builder.writeTimeout : Config.WRITE_TIMEOUT;

        config.retryCount = builder.retryCount != -1 ? builder.retryCount : Config.RETRY_COUNT;
        config.retryDelayMillis = builder.retryDelayMillis != -1 ? builder.retryDelayMillis : Config.RETRY_DELAY_MILLIS;

        config.sslSocketFactory = builder.sslSocketFactory;
        config.interceptors = builder.interceptors;
        config.networkInterceptors = builder.networkInterceptors;

        Singleton.DEFAULT_CONFIG = config;
    }

    @Override
    public HttpConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public HttpConfig headers(Map<String, String> headers) {
        if (this.headers != null && headers != null) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
        return this;
    }

    @Override
    public HttpConfig headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        this.onHeadInterceptor = onHeadInterceptor;
        return this;
    }

    @Override
    public HttpConfig connectTimeout(long timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    @Override
    public HttpConfig readTimeout(long timeout) {
        this.readTimeout = timeout;
        return this;
    }

    @Override
    public HttpConfig writeTimeout(long timeout) {
        this.writeTimeout = timeout;
        return this;
    }

    @Override
    public HttpConfig sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    @Override
    public HttpConfig addInterceptor(Interceptor interceptor) {
        if (this.interceptors != null && interceptor != null) {
            this.interceptors.add(interceptor);
        }
        return this;
    }

    @Override
    public HttpConfig addNetworkInterceptors(Interceptor interceptor) {
        if (this.networkInterceptors != null && interceptor != null) {
            this.networkInterceptors.add(interceptor);
        }
        return this;
    }

    @Override
    public HttpConfig retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public HttpConfig retryDelayMillis(long retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    public static class Builder {
        private String baseUrl;
        private Map<String, String> headers = new LinkedHashMap<>();
        private HeadersInterceptor.OnHeadInterceptor onHeadInterceptor;

        private long connectTimeout = -1;
        private long readTimeout = -1;
        private long writeTimeout = -1;

        private int retryCount = -1;
        private long retryDelayMillis = -1;

        private SSLSocketFactory sslSocketFactory;
        private ArrayList<Interceptor> interceptors = new ArrayList<>();
        private ArrayList<Interceptor> networkInterceptors = new ArrayList<>();

        public Builder() {
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            if (this.headers != null && headers != null) {
                this.headers.clear();
                this.headers.putAll(headers);
            }
            return this;
        }

        public Builder headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
            this.onHeadInterceptor = onHeadInterceptor;
            return this;
        }

        public Builder connectTimeout(long timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(long timeout) {
            this.readTimeout = timeout;
            return this;
        }

        public Builder writeTimeout(long timeout) {
            this.writeTimeout = timeout;
            return this;
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (this.interceptors != null && interceptor != null) {
                this.interceptors.add(interceptor);
            }
            return this;
        }

        public Builder addNetworkInterceptors(Interceptor interceptor) {
            if (this.networkInterceptors != null && interceptor != null) {
                this.networkInterceptors.add(interceptor);
            }
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder retryDelayMillis(long retryDelayMillis) {
            this.retryDelayMillis = retryDelayMillis;
            return this;
        }

        public Builder setLog(String tag, HttpLoggingInterceptor.Level level) {
            Config.TAG_LOG = tag;
            Config.LOG_LEVEL = level;
            return this;
        }

        public Builder setDebug(boolean debug) {
            ULog.setDebug(debug);
            return this;
        }

        public void build() {
            HttpConfig.setDefault(this);
        }
    }
}
