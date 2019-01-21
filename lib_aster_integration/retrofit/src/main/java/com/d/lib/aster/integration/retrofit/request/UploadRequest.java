package com.d.lib.aster.integration.retrofit.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.callback.ProgressCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.integration.okhttp3.MediaTypes;
import com.d.lib.aster.integration.okhttp3.body.UploadProgressRequestBody;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.retrofit.RequestManager;
import com.d.lib.aster.integration.retrofit.RetrofitAPI;
import com.d.lib.aster.integration.retrofit.RetrofitClient;
import com.d.lib.aster.integration.retrofit.func.ApiRetryFunc;
import com.d.lib.aster.integration.retrofit.observer.UploadObserver;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.request.IUploadRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by D on 2017/10/24.
 */
public class UploadRequest extends IUploadRequest<UploadRequest, RetrofitClient> {
    protected List<MultipartBody.Part> mMultipartBodyParts = new ArrayList<>();
    protected Observable<ResponseBody> mObservable;

    public UploadRequest(String url) {
        super(url);
    }

    public UploadRequest(String url, Config config) {
        super(url, config);
    }

    @Override
    protected RetrofitClient getClient() {
        return RetrofitClient.create(IClient.TYPE_UPLOAD, mConfig.log(false));
    }

    @Override
    protected void prepare() {
        if (mParams != null && mParams.size() > 0) {
            Iterator<Map.Entry<String, String>> entryIterator = mParams.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (entryIterator.hasNext()) {
                entry = entryIterator.next();
                if (entry != null) {
                    mMultipartBodyParts.add(MultipartBody.Part.createFormData(entry.getKey(), entry.getValue()));
                }
            }
        }
        mObservable = getClient().getClient().create(RetrofitAPI.class).upload(mUrl, mMultipartBodyParts);
    }

    @Override
    public void request() {
        request(null);
    }

    public void request(@Nullable SimpleCallback<ResponseBody> callback) {
        prepare();
        requestImpl(mObservable, getClient().getHttpConfig(), mTag, callback);
    }

    @Override
    public UploadRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public UploadRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public UploadRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public UploadRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public UploadRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public UploadRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public UploadRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public UploadRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public UploadRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public UploadRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public UploadRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    private static void requestImpl(final Observable<ResponseBody> observable,
                                    final Config config,
                                    final Object tag,
                                    final SimpleCallback<ResponseBody> callback) {
        DisposableObserver<ResponseBody> disposableObserver = new UploadObserver(tag, callback);
        if (tag != null) {
            RequestManager.getIns().add(tag, disposableObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    @Override
    public UploadRequest addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.mParams.put(paramKey, paramValue);
        }
        return this;
    }

    @Override
    public UploadRequest addFile(String key, File file) {
        return addFile(key, file, null);
    }

    @Override
    public UploadRequest addFile(String key, File file, ProgressCallback callback) {
        if (key == null || file == null) {
            return this;
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            this.mMultipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            this.mMultipartBodyParts.add(part);
        }
        return this;
    }

    @Override
    public UploadRequest addImageFile(String key, File file) {
        return addImageFile(key, file, null);
    }

    @Override
    public UploadRequest addImageFile(String key, File file, ProgressCallback callback) {
        if (key == null || file == null) {
            return this;
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.IMAGE_TYPE, file);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            this.mMultipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            this.mMultipartBodyParts.add(part);
        }
        return this;
    }

    @Override
    public UploadRequest addBytes(String key, byte[] bytes, String name) {
        return addBytes(key, bytes, name, null);
    }

    @Override
    public UploadRequest addBytes(String key, byte[] bytes, String name, ProgressCallback callback) {
        if (key == null || bytes == null || name == null) {
            return this;
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, bytes);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, uploadProgressRequestBody);
            this.mMultipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, requestBody);
            this.mMultipartBodyParts.add(part);
        }
        return this;
    }

    @Override
    public UploadRequest addStream(String key, InputStream inputStream, String name) {
        return addStream(key, inputStream, name, null);
    }

    @Override
    public UploadRequest addStream(String key, InputStream inputStream, String name, ProgressCallback callback) {
        if (key == null || inputStream == null || name == null) {
            return this;
        }
        RequestBody requestBody = create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, inputStream);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, uploadProgressRequestBody);
            this.mMultipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, requestBody);
            this.mMultipartBodyParts.add(part);
        }
        return this;
    }

    private static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(inputStream);
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };
    }

    /**
     * Singleton
     */
    public static class Singleton extends IUploadRequest.Singleton<Singleton, RetrofitClient> {
        protected List<MultipartBody.Part> multipartBodyParts = new ArrayList<>();
        protected Observable<ResponseBody> mObservable;

        public Singleton(String url) {
            super(url);
        }

        @Override
        protected RetrofitClient getClient() {
            return RetrofitClient.getDefault(IClient.TYPE_UPLOAD);
        }

        @Override
        protected void prepare() {
            if (mParams != null && mParams.size() > 0) {
                Iterator<Map.Entry<String, String>> entryIterator = mParams.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry != null) {
                        multipartBodyParts.add(MultipartBody.Part.createFormData(entry.getKey(), entry.getValue()));
                    }
                }
            }
            mObservable = getClient().getClient().create(RetrofitAPI.class).upload(mUrl, multipartBodyParts);
        }

        @Override
        public void request() {
            request(null);
        }

        public void request(@Nullable SimpleCallback<ResponseBody> callback) {
            prepare();
            requestImpl(mObservable, getClient().getHttpConfig(), mTag, callback);
        }

        @Override
        public Singleton addParam(String paramKey, String paramValue) {
            if (paramKey != null && paramValue != null) {
                this.mParams.put(paramKey, paramValue);
            }
            return this;
        }

        @Override
        public Singleton addFile(String key, File file) {
            return addFile(key, file, null);
        }

        @Override
        public Singleton addFile(String key, File file, ProgressCallback callback) {
            if (key == null || file == null) {
                return this;
            }
            RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
            if (callback != null) {
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
                this.multipartBodyParts.add(part);
            } else {
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
                this.multipartBodyParts.add(part);
            }
            return this;
        }

        @Override
        public Singleton addImageFile(String key, File file) {
            return addImageFile(key, file, null);
        }

        public Singleton addImageFile(String key, File file, ProgressCallback callback) {
            if (key == null || file == null) {
                return this;
            }
            RequestBody requestBody = RequestBody.create(MediaTypes.IMAGE_TYPE, file);
            if (callback != null) {
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
                this.multipartBodyParts.add(part);
            } else {
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
                this.multipartBodyParts.add(part);
            }
            return this;
        }

        public Singleton addBytes(String key, byte[] bytes, String name) {
            return addBytes(key, bytes, name, null);
        }

        public Singleton addBytes(String key, byte[] bytes, String name, ProgressCallback callback) {
            if (key == null || bytes == null || name == null) {
                return this;
            }
            RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, bytes);
            if (callback != null) {
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, uploadProgressRequestBody);
                this.multipartBodyParts.add(part);
            } else {
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, requestBody);
                this.multipartBodyParts.add(part);
            }
            return this;
        }

        public Singleton addStream(String key, InputStream inputStream, String name) {
            return addStream(key, inputStream, name, null);
        }

        public Singleton addStream(String key, InputStream inputStream, String name, ProgressCallback callback) {
            if (key == null || inputStream == null || name == null) {
                return this;
            }
            RequestBody requestBody = create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, inputStream);
            if (callback != null) {
                UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, uploadProgressRequestBody);
                this.multipartBodyParts.add(part);
            } else {
                MultipartBody.Part part = MultipartBody.Part.createFormData(key, name, requestBody);
                this.multipartBodyParts.add(part);
            }
            return this;
        }
    }
}
