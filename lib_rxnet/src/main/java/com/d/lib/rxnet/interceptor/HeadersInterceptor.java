package com.d.lib.rxnet.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头拦截
 */
public class HeadersInterceptor implements Interceptor {
    private Map<String, String> headers;

    public HeadersInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }

        // TODO: @developer 2017/10/24
//        addToken(builder);

        return chain.proceed(builder.build());
    }

    /**
     * Token maybe dynamical，you shoule override here
     */
    private void addToken(Request.Builder builder) {
        builder.addHeader("token", "").build();
    }
}
