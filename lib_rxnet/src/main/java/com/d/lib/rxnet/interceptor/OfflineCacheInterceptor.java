package com.d.lib.rxnet.interceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.d.lib.rxnet.util.Network;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 离线缓存拦截
 */
public class OfflineCacheInterceptor implements Interceptor {
    private Context context;
    private String cacheControlValue;

    public OfflineCacheInterceptor(Context context) {
        this(context, 24 * 60 * 60);//默认最大离线缓存时间（秒）
    }

    public OfflineCacheInterceptor(Context context, int cacheControlValue) {
        this.context = context;
        this.cacheControlValue = String.format("max-stale=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!Network.isConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    }
}
