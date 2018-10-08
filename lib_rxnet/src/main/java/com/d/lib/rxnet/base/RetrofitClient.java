package com.d.lib.rxnet.base;

import android.text.TextUtils;

import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.utils.ULog;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient
 * Created by D on 2017/7/14.
 */
public class RetrofitClient {

    private static class Default {
        private final static Retrofit INSTANCE = getRetrofit(HttpConfig.getDefault(), true);
    }

    private static class Transfer {
        private final static Retrofit INSTANCE = getRetrofit(HttpConfig.getDefault(), false);
    }

    /**
     * Singleton - Default configuration
     */
    public static Retrofit getDefault() {
        return Default.INSTANCE;
    }

    /**
     * Singleton - Default Transfer configuration
     */
    public static Retrofit getTransfer() {
        return Transfer.INSTANCE;
    }

    /**
     * New instance - Custom configuration
     *
     * @param config Configuration
     * @param log    Whether to add HttpLoggingInterceptor
     * @return Retrofit
     */
    public static Retrofit getRetrofit(HttpConfig config, boolean log) {
        Retrofit retrofit = new Retrofit.Builder()
                // Set OKHttpClient, if not set, a default will be provided
                .client(getOkHttpClient(config.headers,
                        config.onHeadInterceptor,
                        config.connectTimeout != -1 ? config.connectTimeout : HttpConfig.getDefault().connectTimeout,
                        config.readTimeout != -1 ? config.readTimeout : HttpConfig.getDefault().readTimeout,
                        config.writeTimeout != -1 ? config.writeTimeout : HttpConfig.getDefault().writeTimeout,
                        config.sslSocketFactory,
                        config.interceptors,
                        config.networkInterceptors,
                        log))
                // Set base url
                .baseUrl(!TextUtils.isEmpty(config.baseUrl) ? config.baseUrl : HttpConfig.getDefault().baseUrl)
                // Set RxJava2CallAdapterFactory
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // Add converter
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(Map<String, String> headers,
                                                HeadersInterceptor.OnHeadInterceptor onHeadInterceptor,
                                                long connectTimeout,
                                                long readTimeout,
                                                long writeTimeout,
                                                SSLSocketFactory sslSocketFactory,
                                                ArrayList<Interceptor> interceptors,
                                                ArrayList<Interceptor> networkInterceptors,
                                                boolean log) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);

        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory);
        }

        if (headers != null && headers.size() > 0 || onHeadInterceptor != null) {
            builder.addInterceptor(new HeadersInterceptor(headers).setOnHeadInterceptor(onHeadInterceptor));
        }
        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        if (log) {
            builder.addInterceptor(getOkhttpLog());
        }

        if (networkInterceptors != null && networkInterceptors.size() > 0) {
            for (Interceptor networkInterceptor : networkInterceptors) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }
        return builder.build();
    }

    private static HttpLoggingInterceptor getOkhttpLog() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String s) {
                // Print retrofit log
                ULog.d(Config.TAG_LOG + s);
            }
        });
        loggingInterceptor.setLevel(Config.LOG_LEVEL);
        return loggingInterceptor;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            // Get the X509 Key Manager instance of TrustManagerFactory
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            ULog.e("SslContextFactory: " + e.getMessage());
            return null;
        }
    }
}
