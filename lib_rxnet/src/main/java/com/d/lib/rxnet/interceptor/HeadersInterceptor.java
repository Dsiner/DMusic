package com.d.lib.rxnet.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Request header interception
 */
public class HeadersInterceptor implements Interceptor {
    private Map<String, String> headers;
    private OnHeadInterceptor onHeadInterceptor;

    public HeadersInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }

        if (onHeadInterceptor != null) {
            onHeadInterceptor.intercept(builder);
        }

        return chain.proceed(builder.build());
    }

    public interface OnHeadInterceptor {

        /**
         * Some parameters may be dynamic, such as tokens, etc. You shoule override here
         * builder.addHeader("token", "")
         */
        void intercept(Request.Builder builder);
    }

    public HeadersInterceptor setOnHeadInterceptor(OnHeadInterceptor onHeadInterceptor) {
        this.onHeadInterceptor = onHeadInterceptor;
        return this;
    }
}
