package com.d.lib.rxnet.interceptor;

import android.support.annotation.NonNull;

import com.d.lib.rxnet.body.UploadProgressRequestBody;
import com.d.lib.rxnet.callback.ProgressCallback;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Upload progress interception
 */
public class UploadProgressInterceptor implements Interceptor {
    private ProgressCallback mCallback;

    public UploadProgressInterceptor(ProgressCallback callback) {
        if (callback == null) {
            throw new NullPointerException("This callback must not be null.");
        }
        this.mCallback = callback;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(), new UploadProgressRequestBody(originalRequest.body(), mCallback))
                .build();
        return chain.proceed(progressRequest);
    }
}
