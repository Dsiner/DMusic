package com.d.lib.rxnet.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.rxnet.utils.ULog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Online cache interception
 */
public class OnlineCacheInterceptor implements Interceptor {
    private String mCacheControlValue;

    public OnlineCacheInterceptor() {
        this(60); // Default maximum online cache time (seconds)
    }

    public OnlineCacheInterceptor(int cacheControlValue) {
        this.mCacheControlValue = String.format("max-age=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") || cacheControl
                .contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            ULog.d(originalResponse.headers().toString());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + mCacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse;
        }
    }
}
