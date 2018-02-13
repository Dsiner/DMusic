package com.d.lib.rxnet.base;

import android.text.TextUtils;

import com.d.lib.rxnet.listener.ConfigListener;
import com.d.lib.rxnet.util.SSLUtil;

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
public class HttpConfig extends ConfigListener<HttpConfig> {
    private static HttpConfig defaultConfig;

    public String baseUrl;
    public Map<String, String> headers = new LinkedHashMap<>();
    public long connectTimeout = -1;
    public long readTimeout = -1;
    public long writeTimeout = -1;
    public int retryCount = -1;
    public long retryDelayMillis = -1;
    public ArrayList<Interceptor> interceptors = new ArrayList<>();
    public ArrayList<Interceptor> networkInterceptors = new ArrayList<>();
    public SSLSocketFactory sslSocketFactory;

    /**
     * 获取默认配置
     */
    public synchronized static HttpConfig getDefaultConfig() {
        if (defaultConfig == null) {
            synchronized (HttpConfig.class) {
                if (defaultConfig == null) {
                    /*** default ***/
                    defaultConfig = new HttpConfig()
                            .baseUrl(Config.BASE_URL)
                            .connectTimeout(Config.CONNECT_TIMEOUT)
                            .readTimeout(Config.READ_TIMEOUT)
                            .writeTimeout(Config.WRITE_TIMEOUT)
                            .retryCount(Config.RETRY_COUNT)
                            .retryDelayMillis(Config.RETRY_DELAY_MILLIS)
                            .sslSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
                }
            }
        }
        return defaultConfig;
    }

    /**
     * 获取默认配置-拷贝
     */
    public static HttpConfig getNewDefaultConfig() {
        getDefaultConfig();
        return new HttpConfig()
                .baseUrl(defaultConfig.baseUrl)
                .connectTimeout(defaultConfig.connectTimeout)
                .readTimeout(defaultConfig.readTimeout)
                .writeTimeout(defaultConfig.writeTimeout)
                .retryCount(defaultConfig.retryCount)
                .retryDelayMillis(defaultConfig.retryDelayMillis)
                .sslSocketFactory(defaultConfig.sslSocketFactory);
    }

    private synchronized static void setDefaultConfig(HttpConfig config) {
        if (config == null) {
            return;
        }
        config.baseUrl = !TextUtils.isEmpty(config.baseUrl) ? config.baseUrl : Config.BASE_URL;

        config.connectTimeout = config.connectTimeout != -1 ? config.connectTimeout : Config.CONNECT_TIMEOUT;
        config.readTimeout = config.readTimeout != -1 ? config.readTimeout : Config.READ_TIMEOUT;
        config.writeTimeout = config.writeTimeout != -1 ? config.writeTimeout : Config.WRITE_TIMEOUT;

        config.retryCount = config.retryCount != -1 ? config.retryCount : Config.RETRY_COUNT;
        config.retryDelayMillis = config.retryDelayMillis != -1 ? config.retryDelayMillis : Config.RETRY_DELAY_MILLIS;

        HttpConfig.defaultConfig = config;
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
    public HttpConfig sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
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

    public static class Build extends HttpConfig {

        public Build() {
        }

        @Override
        public Build baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        @Override
        public Build headers(Map<String, String> headers) {
            if (this.headers != null && headers != null) {
                this.headers.clear();
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public Build connectTimeout(long timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        @Override
        public Build readTimeout(long timeout) {
            this.readTimeout = timeout;
            return this;
        }

        @Override
        public Build writeTimeout(long timeout) {
            this.writeTimeout = timeout;
            return this;
        }

        @Override
        public Build addInterceptor(Interceptor interceptor) {
            if (this.interceptors != null && interceptor != null) {
                this.interceptors.add(interceptor);
            }
            return this;
        }

        @Override
        public Build addNetworkInterceptors(Interceptor interceptor) {
            if (this.networkInterceptors != null && interceptor != null) {
                this.networkInterceptors.add(interceptor);
            }
            return this;
        }

        @Override
        public Build sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        @Override
        public Build retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        @Override
        public Build retryDelayMillis(long retryDelayMillis) {
            this.retryDelayMillis = retryDelayMillis;
            return this;
        }

        public Build setLog(String tag, HttpLoggingInterceptor.Level level) {
            Config.TAG_LOG = tag;
            Config.LOG_LEVEL = level;
            return this;
        }

        public void build() {
            HttpConfig.setDefaultConfig(this);
        }
    }
}
