package com.d.music.component.aster;

import android.content.Context;

import androidx.annotation.NonNull;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.integration.okhttp3.OkHttpModule;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.util.SSLUtils;

import java.util.Map;

/**
 * AppAsterModule
 * Created by D on 2019/3/11.
 */
public class AppAsterModule extends OkHttpModule {

    public static HeadersInterceptor.OnHeadInterceptor getOnHeadInterceptor(final Params params) {
        return new HeadersInterceptor.OnHeadInterceptor() {
            @Override
            public void intercept(Map<String, String> heads) {
            }
        };
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull Config.Builder builder) {
        builder.baseUrl(API.API_BASE)
                .connectTimeout(10 * 1000)
                .readTimeout(10 * 1000)
                .writeTimeout(10 * 1000)
                .retryCount(0)
                .retryDelayMillis(3 * 1000)
                .sslSocketFactory(SSLUtils.getSslSocketFactory(null, null, null))
                .log("AsterLog Back = ", Config.Level.BODY)
                .debug(true)
                .build();
    }
}
