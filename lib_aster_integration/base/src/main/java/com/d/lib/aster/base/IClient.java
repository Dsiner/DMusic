package com.d.lib.aster.base;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.d.lib.aster.utils.ULog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * HttpClient
 * Created by D on 2017/7/14.
 */
public class IClient {
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_DOWNLOAD = 1;
    public final static int TYPE_UPLOAD = 2;

    @IntDef({TYPE_NORMAL, TYPE_DOWNLOAD, TYPE_UPLOAD})
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {

    }

    protected int mType;
    protected Config mConfig;

    protected IClient(@State int type, @NonNull Config config) {
        this.mType = type;
        this.mConfig = config;
    }

    public int getType() {
        return mType;
    }

    @NonNull
    public Config getHttpConfig() {
        return mConfig;
    }

    protected static SSLSocketFactory getSSLSocketFactory() {
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
