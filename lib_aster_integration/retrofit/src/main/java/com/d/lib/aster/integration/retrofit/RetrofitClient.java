package com.d.lib.aster.integration.retrofit;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.utils.ULog;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HttpClient
 * Created by D on 2017/7/14.
 */
public class RetrofitClient extends IClient {

    private static class Default {
        private final static RetrofitClient INSTANCE = create(TYPE_NORMAL, Config.getDefault().log(true));
    }

    private static class Transfer {
        private final static RetrofitClient DOWNLOAD = create(TYPE_DOWNLOAD, Config.getDefault().log(false));
        private final static RetrofitClient UPLOAD = create(TYPE_UPLOAD, Config.getDefault().log(false));
    }

    private Retrofit mClient;

    private RetrofitClient(@State int type, @NonNull Config config) {
        super(type, config);
        this.mClient = getClient(config);
    }

    @NonNull
    public Retrofit getClient() {
        return mClient;
    }

    public static RetrofitClient create(@State int type, @NonNull Config config) {
        return new RetrofitClient(type, config);
    }

    /**
     * Singleton - Default configuration
     */
    @NonNull
    public static RetrofitClient getDefault(@State int type) {
        if (type == TYPE_DOWNLOAD) {
            return Transfer.DOWNLOAD;
        } else if (type == TYPE_UPLOAD) {
            return Transfer.UPLOAD;
        } else {
            return RetrofitClient.Default.INSTANCE;
        }
    }

    /**
     * New instance - Custom configuration
     *
     * @param config Configuration
     * @return Retrofit
     */
    @NonNull
    public static Retrofit getClient(@NonNull Config config) {
        return new Retrofit.Builder()
                // Set OKHttpClient, if not set, a default will be provided
                .client(getOkHttpClient(config.headers,
                        config.onHeadInterceptor,
                        config.connectTimeout != -1 ? config.connectTimeout : Config.getDefault().connectTimeout,
                        config.readTimeout != -1 ? config.readTimeout : Config.getDefault().readTimeout,
                        config.writeTimeout != -1 ? config.writeTimeout : Config.getDefault().writeTimeout,
                        config.sslSocketFactory,
                        config.interceptors,
                        config.networkInterceptors,
                        config.log))
                // Set base url
                .baseUrl(!TextUtils.isEmpty(config.baseUrl) ? config.baseUrl : Config.getDefault().baseUrl)
                // Set RxJava2CallAdapterFactory
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // Add converter
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient getOkHttpClient(Map<String, String> headers,
                                                HeadersInterceptor.OnHeadInterceptor onHeadInterceptor,
                                                long connectTimeout,
                                                long readTimeout,
                                                long writeTimeout,
                                                SSLSocketFactory sslSocketFactory,
                                                ArrayList<IInterceptor> interceptors,
                                                ArrayList<IInterceptor> networkInterceptors,
                                                boolean log) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory);
        }

        if (headers != null && headers.size() > 0 || onHeadInterceptor != null) {
            builder.addInterceptor((okhttp3.Interceptor) new HeadersInterceptor(headers)
                    .setOnHeadInterceptor(onHeadInterceptor));
        }
        if (interceptors != null && interceptors.size() > 0) {
            for (IInterceptor interceptor : interceptors) {
                builder.addInterceptor((okhttp3.Interceptor) interceptor);
            }
        }
        if (log) {
            builder.addInterceptor(getOkHttpLog());
        }

        if (networkInterceptors != null && networkInterceptors.size() > 0) {
            for (IInterceptor networkInterceptor : networkInterceptors) {
                builder.addNetworkInterceptor((okhttp3.Interceptor) networkInterceptor);
            }
        }
        return builder.build();
    }

    private static HttpLoggingInterceptor getOkHttpLog() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String s) {
                // Print retrofit log
                ULog.d(Config.Default.TAG_LOG + s);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }
}
