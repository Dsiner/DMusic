package com.d.lib.aster.base;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.d.lib.aster.interceptor.IHeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.utils.SSLUtil;
import com.d.lib.aster.utils.ULog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

/**
 * HttpConfig
 * Created by D on 2017/10/24.
 */
public class Config extends IConfig<Config> {

    public static class Default {
        public static String TAG_LOG = "RetrofitLog Back = ";
        public static Level LOG_LEVEL = Level.BODY;

        /****************** Default ******************/
        public static final String BASE_URL = "https://www.google.com/";
        public static final long CONNECT_TIMEOUT = 10 * 1000;
        public static final long READ_TIMEOUT = 10 * 1000;
        public static final long WRITE_TIMEOUT = 10 * 1000;
        public static final int RETRY_COUNT = 3; // Default retries
        public static final long RETRY_DELAY_MILLIS = 3 * 1000; // Default retry interval (ms)
    }

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    private static Application context;
    public String baseUrl;
    public Map<String, String> headers = new LinkedHashMap<>();
    public IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor;

    public long connectTimeout = -1;
    public long readTimeout = -1;
    public long writeTimeout = -1;

    public int retryCount = -1;
    public long retryDelayMillis = -1;

    public SSLSocketFactory sslSocketFactory;
    public ArrayList<IInterceptor> interceptors = new ArrayList<>();
    public ArrayList<IInterceptor> networkInterceptors = new ArrayList<>();
    public boolean log = true;

    private static class Singleton {
        private static Config DEFAULT_CONFIG;

        @NonNull
        private synchronized static Config getDefault() {
            if (DEFAULT_CONFIG == null) {
                DEFAULT_CONFIG = new Config()
                        .baseUrl(Default.BASE_URL)
                        .connectTimeout(Default.CONNECT_TIMEOUT)
                        .readTimeout(Default.READ_TIMEOUT)
                        .writeTimeout(Default.WRITE_TIMEOUT)
                        .retryCount(Default.RETRY_COUNT)
                        .retryDelayMillis(Default.RETRY_DELAY_MILLIS)
                        .sslSocketFactory(SSLUtil.getSslSocketFactory(null, null, null));
            }
            return DEFAULT_CONFIG;
        }

        private synchronized static void setDefault(@NonNull Builder builder) {
            Config config = new Config();

            context = builder.context;
            config.baseUrl = !TextUtils.isEmpty(builder.baseUrl) ? builder.baseUrl : Default.BASE_URL;
            config.headers = builder.headers;
            config.onHeadInterceptor = builder.onHeadInterceptor;

            config.connectTimeout = builder.connectTimeout != -1 ? builder.connectTimeout : Default.CONNECT_TIMEOUT;
            config.readTimeout = builder.readTimeout != -1 ? builder.readTimeout : Default.READ_TIMEOUT;
            config.writeTimeout = builder.writeTimeout != -1 ? builder.writeTimeout : Default.WRITE_TIMEOUT;

            config.retryCount = builder.retryCount != -1 ? builder.retryCount : Default.RETRY_COUNT;
            config.retryDelayMillis = builder.retryDelayMillis != -1 ? builder.retryDelayMillis : Default.RETRY_DELAY_MILLIS;

            config.sslSocketFactory = builder.sslSocketFactory;
            config.interceptors = builder.interceptors;
            config.networkInterceptors = builder.networkInterceptors;

            Singleton.DEFAULT_CONFIG = config;
        }
    }

    /**
     * Get the default configuration
     */
    @NonNull
    public static Config getDefault() {
        Config defaultConfig = Singleton.getDefault();
        return copy(defaultConfig);
    }

    @NonNull
    public static Config copy(@NonNull Config config) {
        return new Config()
                .baseUrl(config.baseUrl)
                .headers(config.headers)
                .headers(config.onHeadInterceptor)
                .connectTimeout(config.connectTimeout)
                .readTimeout(config.readTimeout)
                .writeTimeout(config.writeTimeout)
                .retryCount(config.retryCount)
                .retryDelayMillis(config.retryDelayMillis)
                .sslSocketFactory(config.sslSocketFactory);
    }

    @Nullable
    public Context getContext() {
        return context;
    }

    @Override
    public Config baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public Config headers(Map<String, String> headers) {
        if (this.headers != null && headers != null) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
        return this;
    }

    @Override
    public Config headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        this.onHeadInterceptor = onHeadInterceptor;
        return this;
    }

    @Override
    public Config connectTimeout(long timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    @Override
    public Config readTimeout(long timeout) {
        this.readTimeout = timeout;
        return this;
    }

    @Override
    public Config writeTimeout(long timeout) {
        this.writeTimeout = timeout;
        return this;
    }

    @Override
    public Config sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    @Override
    public Config addInterceptor(IInterceptor interceptor) {
        if (this.interceptors != null && interceptor != null) {
            this.interceptors.add(interceptor);
        }
        return this;
    }

    @Override
    public Config addNetworkInterceptors(IInterceptor interceptor) {
        if (this.networkInterceptors != null && interceptor != null) {
            this.networkInterceptors.add(interceptor);
        }
        return this;
    }

    @Override
    public Config retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public Config retryDelayMillis(long retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    /**
     * @param log Whether to add HttpLoggingInterceptor
     */
    public Config log(boolean log) {
        this.log = log;
        return this;
    }

    public static class Builder {
        private Application context;
        private String baseUrl;
        private Map<String, String> headers = new LinkedHashMap<>();
        private IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor;

        private long connectTimeout = -1;
        private long readTimeout = -1;
        private long writeTimeout = -1;

        private int retryCount = -1;
        private long retryDelayMillis = -1;

        private SSLSocketFactory sslSocketFactory;
        private ArrayList<IInterceptor> interceptors = new ArrayList<>();
        private ArrayList<IInterceptor> networkInterceptors = new ArrayList<>();

        public Builder() {
        }

        public Builder(Context context) {
            this.context = (Application) context.getApplicationContext();
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

        public Builder headers(IHeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
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

        public Builder addInterceptor(IInterceptor interceptor) {
            if (this.interceptors != null && interceptor != null) {
                this.interceptors.add(interceptor);
            }
            return this;
        }

        public Builder addNetworkInterceptors(IInterceptor interceptor) {
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

        public Builder log(String tag, Level level) {
            Default.TAG_LOG = tag;
            Default.LOG_LEVEL = level;
            return this;
        }

        public Builder debug(boolean debug) {
            ULog.setDebug(debug);
            return this;
        }

        public void build() {
            Singleton.setDefault(this);
        }
    }
}
