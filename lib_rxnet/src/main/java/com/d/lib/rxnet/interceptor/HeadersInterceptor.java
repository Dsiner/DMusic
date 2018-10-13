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
    private Map<String, String> mHeaders;
    private OnHeadInterceptor mOnHeadInterceptor;

    public HeadersInterceptor(Map<String, String> headers) {
        this.mHeaders = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (mHeaders != null && mHeaders.size() > 0) {
            Set<String> keys = mHeaders.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, mHeaders.get(headerKey));
            }
        }

        if (mOnHeadInterceptor != null) {
            mOnHeadInterceptor.intercept(builder);
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
        this.mOnHeadInterceptor = onHeadInterceptor;
        return this;
    }
}
