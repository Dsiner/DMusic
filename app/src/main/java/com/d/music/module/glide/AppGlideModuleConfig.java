package com.d.music.module.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.d.lib.common.utils.log.ULog;
import com.d.music.common.Constants;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Glide全局配置
 * Created by D on 2017/8/10.
 */
@GlideModule
public class AppGlideModuleConfig extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565)
                .setDiskCache(new ExternalCacheDiskCacheFactory(context, Constants.Path.glide_cache, 1024 * 1024 * 1024))
                .setMemoryCache(new LruResourceCache(3 * 1024 * 1024))
                .setBitmapPool(new LruBitmapPool(3 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS)
                .sslSocketFactory(getSSLSocketFactory())
                .build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }

    private SSLSocketFactory getSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            // 取得TrustManagerFactory的X509密钥管理器实例
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
            ULog.e("SslContextFactory:" + e.getMessage());
            return null;
        }
    }
}
