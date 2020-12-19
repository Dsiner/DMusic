package com.d.lib.common.component.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.d.lib.common.util.log.ULog;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Glide global configuration
 * Created by D on 2017/8/10.
 */
public abstract class AbstractGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565))
                .setDiskCache(new ExternalCacheDiskCacheFactory(context, getCachePath(), 1024 * 1024 * 1024))
                .setMemoryCache(new LruResourceCache(6 * 1024 * 1024))
                .setBitmapPool(new LruBitmapPool(6 * 1024 * 1024));
    }

    @NonNull
    protected abstract String getCachePath();

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory);
        }
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
    }

    private SSLSocketFactory getSSLSocketFactory() {
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
            ULog.e("SslContextFactory: " + e.getMessage());
            return null;
        }
    }
}
