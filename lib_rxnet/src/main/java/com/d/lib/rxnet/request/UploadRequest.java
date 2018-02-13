package com.d.lib.rxnet.request;

import android.support.annotation.NonNull;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.ApiManager;
import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.RetrofitClient;
import com.d.lib.rxnet.body.UploadProgressRequestBody;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.interceptor.UploadProgressInterceptor;
import com.d.lib.rxnet.listener.UploadCallBack;
import com.d.lib.rxnet.mode.MediaTypes;
import com.d.lib.rxnet.observer.UploadObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * New but Default Config
 * Created by D on 2017/10/24.
 */
public class UploadRequest extends BaseRequest<UploadRequest> {
    protected Map<String, String> params = new LinkedHashMap<>();
    protected List<MultipartBody.Part> multipartBodyParts = new ArrayList<>();

    public UploadRequest(String url) {
        this.url = url;
        this.config = HttpConfig.getNewDefaultConfig();
    }

    protected void init(UploadCallBack callback) {
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (entryIterator.hasNext()) {
                entry = entryIterator.next();
                if (entry != null) {
                    multipartBodyParts.add(MultipartBody.Part.createFormData(entry.getKey(), entry.getValue()));
                }
            }
        }
        config.addNetworkInterceptors(new UploadProgressInterceptor(callback));
        observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).upload(url, multipartBodyParts);
    }

    public void request(final UploadCallBack callback) {
        if (callback == null) {
            throw new NullPointerException("this callback is null!");
        }
        init(callback);
        DisposableObserver disposableObserver = new UploadObserver(callback);
        if (super.tag != null) {
            ApiManager.get().add(super.tag, disposableObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    public UploadRequest addParam(String paramKey, String paramValue) {
        if (paramKey != null && paramValue != null) {
            this.params.put(paramKey, paramValue);
        }
        return this;
    }

    public UploadRequest addFile(String key, File file) {
        return addFile(key, file, null);
    }

    public UploadRequest addFile(String key, File file, UploadCallBack callback) {
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

    public UploadRequest addImageFile(String key, File file) {
        return addImageFile(key, file, null);
    }

    public UploadRequest addImageFile(String key, File file, UploadCallBack callback) {
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

    public UploadRequest addBytes(String key, byte[] bytes, String name) {
        return addBytes(key, bytes, name, null);
    }

    public UploadRequest addBytes(String key, byte[] bytes, String name, UploadCallBack callback) {
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

    public UploadRequest addStream(String key, InputStream inputStream, String name) {
        return addStream(key, inputStream, name, null);
    }

    public UploadRequest addStream(String key, InputStream inputStream, String name, UploadCallBack callback) {
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

    protected RequestBody create(final MediaType mediaType, final InputStream inputStream) {
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

    @Override
    protected UploadRequest baseUrl(String baseUrl) {
        return this;
    }

    @Override
    protected UploadRequest headers(Map<String, String> headers) {
        return this;
    }

    @Override
    protected UploadRequest connectTimeout(long timeout) {
        return this;
    }

    @Override
    protected UploadRequest readTimeout(long timeout) {
        return this;
    }

    @Override
    protected UploadRequest writeTimeout(long timeout) {
        return this;
    }

    @Override
    protected UploadRequest addInterceptor(Interceptor interceptor) {
        return this;
    }

    @Override
    protected UploadRequest addNetworkInterceptors(Interceptor interceptor) {
        return this;
    }

    @Override
    protected UploadRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return this;
    }

    @Override
    protected UploadRequest retryCount(int retryCount) {
        return this;
    }

    @Override
    protected UploadRequest retryDelayMillis(long retryDelayMillis) {
        return this;
    }

    /**
     * New
     */
    public static class UploadRequestF extends UploadRequest {

        public UploadRequestF(String url) {
            super(url);
            this.config = HttpConfig.getNewDefaultConfig();
        }

        @Override
        public UploadRequestF addParam(String paramKey, String paramValue) {
            if (paramKey != null && paramValue != null) {
                this.params.put(paramKey, paramValue);
            }
            return this;
        }

        @Override
        public UploadRequestF addFile(String key, File file) {
            return addFile(key, file, null);
        }

        @Override
        public UploadRequestF addFile(String key, File file, UploadCallBack callback) {
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
        public UploadRequestF addImageFile(String key, File file) {
            return addImageFile(key, file, null);
        }

        @Override
        public UploadRequestF addImageFile(String key, File file, UploadCallBack callback) {
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

        @Override
        public UploadRequestF addBytes(String key, byte[] bytes, String name) {
            return addBytes(key, bytes, name, null);
        }

        @Override
        public UploadRequestF addBytes(String key, byte[] bytes, String name, UploadCallBack callback) {
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

        @Override
        public UploadRequestF addStream(String key, InputStream inputStream, String name) {
            return addStream(key, inputStream, name, null);
        }

        @Override
        public UploadRequestF addStream(String key, InputStream inputStream, String name, UploadCallBack callback) {
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

        @Override
        public UploadRequestF tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /******************* Config *******************/
        @Override
        public UploadRequestF baseUrl(String baseUrl) {
            config.baseUrl(baseUrl);
            return this;
        }

        @Override
        public UploadRequestF headers(Map<String, String> headers) {
            config.headers(headers);
            return this;
        }

        @Override
        public UploadRequestF connectTimeout(long timeout) {
            config.connectTimeout(timeout);
            return this;
        }

        @Override
        public UploadRequestF readTimeout(long timeout) {
            config.readTimeout(timeout);
            return this;
        }

        @Override
        public UploadRequestF writeTimeout(long timeout) {
            config.writeTimeout(timeout);
            return this;
        }

        @Override
        public UploadRequestF addInterceptor(Interceptor interceptor) {
            config.addInterceptor(interceptor);
            return this;
        }

        @Override
        public UploadRequestF addNetworkInterceptors(Interceptor interceptor) {
            config.addNetworkInterceptors(interceptor);
            return this;
        }

        @Override
        public UploadRequestF sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            config.sslSocketFactory(sslSocketFactory);
            return this;
        }

        @Override
        public UploadRequestF retryCount(int retryCount) {
            config.retryCount(retryCount);
            return this;
        }

        @Override
        public UploadRequestF retryDelayMillis(long retryDelayMillis) {
            config.retryDelayMillis(retryDelayMillis);
            return this;
        }
    }
}
