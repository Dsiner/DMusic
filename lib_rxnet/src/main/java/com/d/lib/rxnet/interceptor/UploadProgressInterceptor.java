package com.d.lib.rxnet.interceptor;

import android.support.annotation.NonNull;

import com.d.lib.rxnet.body.UploadProgressRequestBody;
import com.d.lib.rxnet.listener.UploadCallBack;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上传进度拦截
 */
public class UploadProgressInterceptor implements Interceptor {
    private UploadCallBack callback;

    public UploadProgressInterceptor(UploadCallBack callback) {
        if (callback == null) {
            throw new NullPointerException("this callback must not null.");
        }
        this.callback = callback;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }
        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(), new UploadProgressRequestBody(originalRequest.body(), callback))
                .build();
        return chain.proceed(progressRequest);
    }
}
