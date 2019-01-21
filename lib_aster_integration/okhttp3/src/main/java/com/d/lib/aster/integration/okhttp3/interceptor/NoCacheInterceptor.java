package com.d.lib.aster.integration.okhttp3.interceptor;

import android.support.annotation.NonNull;

import com.d.lib.aster.interceptor.IInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * No cache interception
 */
public class NoCacheInterceptor implements Interceptor,
        IInterceptor<Interceptor.Chain, Response> {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder().header("Cache-Control", "no-cache").build();
        Response originalResponse = chain.proceed(request);
        originalResponse = originalResponse.newBuilder().header("Cache-Control", "no-cache").build();
        return originalResponse;
    }
}
