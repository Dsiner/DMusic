package com.d.lib.aster.integration.okhttp3.request;

import android.support.annotation.NonNull;

import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.integration.okhttp3.MediaTypes;
import com.d.lib.aster.integration.okhttp3.OkHttpClient;
import com.d.lib.aster.integration.okhttp3.RequestManager;
import com.d.lib.aster.integration.okhttp3.func.ApiFunc;
import com.d.lib.aster.integration.okhttp3.func.ApiRetryFunc;
import com.d.lib.aster.integration.okhttp3.func.MapFunc;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.okhttp3.observer.ApiObserver;
import com.d.lib.aster.integration.okhttp3.observer.AsyncApiObserver;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.request.IPostRequest;
import com.d.lib.aster.scheduler.Observable;
import com.d.lib.aster.scheduler.callback.DisposableObserver;
import com.d.lib.aster.scheduler.schedule.Schedulers;
import com.d.lib.aster.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by D on 2017/10/24.
 */
public class PostRequest extends IPostRequest<PostRequest, OkHttpClient> {
    protected Observable<ResponseBody> mObservable;
    private Map<String, Object> mForms = new LinkedHashMap<>();
    private RequestBody mRequestBody;
    private MediaType mMediaType;
    private String mContent;

    public PostRequest(String url) {
        super(url);
    }

    public PostRequest(String url, Params params) {
        super(url, params);
    }

    @Override
    protected OkHttpClient getClient() {
        return OkHttpClient.create(IClient.TYPE_NORMAL, mConfig.log(true));
    }

    @Override
    protected void prepare() {
        if (mForms != null && mForms.size() > 0) {
            if (mParams != null && mParams.size() > 0) {
                Iterator<Map.Entry<String, String>> entryIterator = mParams.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry != null) {
                        mForms.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            mObservable = getClient().create().postForm(mUrl, mForms);
            return;
        }
        if (mRequestBody != null) {
            mObservable = getClient().create().postBody(mUrl, mRequestBody);
            return;
        }
        if (mContent != null && mMediaType != null) {
            mRequestBody = RequestBody.create(mMediaType, mContent);
            mObservable = getClient().create().postBody(mUrl, mRequestBody);
            return;
        }
        if (mParams != null && mParams.size() > 0) {
            mObservable = getClient().create().post(mUrl, mParams);
            return;
        }
        mObservable = getClient().create().post(mUrl);
    }

    @Override
    public <T> void request(final SimpleCallback<T> callback) {
        prepare();
        DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .observeOn(Schedulers.mainThread())
                .subscribe(new ApiRetryFunc<T>(disposableObserver,
                        mConfig.retryCount,
                        mConfig.retryDelayMillis,
                        new ApiRetryFunc.OnRetry<T>() {
                            @NonNull
                            @Override
                            public Observable.Observe<T> observe() {
                                return mObservable.subscribeOn(Schedulers.io())
                                        .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                        .observeOn(Schedulers.mainThread());
                            }
                        }));
    }

    @Override
    public <T, R> void request(final AsyncCallback<T, R> callback) {
        prepare();
        DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
        if (mTag != null) {
            RequestManager.getIns().add(mTag, disposableObserver);
        }
        mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                .map(new MapFunc<T, R>(callback))
                .observeOn(Schedulers.mainThread())
                .subscribe(new ApiRetryFunc<R>(disposableObserver,
                        mConfig.retryCount, mConfig.retryDelayMillis,
                        new ApiRetryFunc.OnRetry<R>() {
                            @NonNull
                            @Override
                            public Observable.Observe<R> observe() {
                                return mObservable.subscribeOn(Schedulers.io())
                                        .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                        .map(new MapFunc<T, R>(callback))
                                        .observeOn(Schedulers.mainThread());
                            }
                        }));
    }

    @Override
    public <T> Observable.Observe<T> observable(Class<T> clazz) {
        prepare();
        return mObservable.subscribeOn(Schedulers.io())
                .map(new ApiFunc<T>(clazz));
    }

    @Override
    public PostRequest addForm(String formKey, Object formValue) {
        if (formKey != null && formValue != null) {
            mForms.put(formKey, formValue);
        }
        return this;
    }

    public PostRequest setRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
        return this;
    }

    @Override
    public PostRequest setString(String string) {
        this.mContent = string;
        this.mMediaType = MediaTypes.TEXT_PLAIN_TYPE;
        return this;
    }

    public PostRequest setString(String string, MediaType mediaType) {
        this.mContent = string;
        this.mMediaType = mediaType;
        return this;
    }

    @Override
    public PostRequest setJson(String json) {
        this.mContent = json;
        this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    @Override
    public PostRequest setJson(JSONObject jsonObject) {
        this.mContent = jsonObject.toString();
        this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    @Override
    public PostRequest setJson(JSONArray jsonArray) {
        this.mContent = jsonArray.toString();
        this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    @Override
    public PostRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public PostRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public PostRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public PostRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public PostRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public PostRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public PostRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public PostRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public PostRequest addNetworkInterceptors(IInterceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public PostRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public PostRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static class Singleton extends IPostRequest.Singleton<Singleton, OkHttpClient> {
        protected Observable<ResponseBody> mObservable;
        private Map<String, Object> mForms = new LinkedHashMap<>();
        private RequestBody mRequestBody;
        private MediaType mMediaType;
        private String mContent;

        public Singleton(String url) {
            super(url);
        }

        public Singleton(String url, Params params) {
            super(url, params);
        }

        @Override
        protected OkHttpClient getClient() {
            return OkHttpClient.getDefault(IClient.TYPE_NORMAL);
        }

        @Override
        protected void prepare() {
            if (mForms != null && mForms.size() > 0) {
                if (mParams != null && mParams.size() > 0) {
                    Iterator<Map.Entry<String, String>> entryIterator = mParams.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (entryIterator.hasNext()) {
                        entry = entryIterator.next();
                        if (entry != null) {
                            mForms.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                mObservable = getClient().create().postForm(mUrl, mForms);
                return;
            }
            if (mRequestBody != null) {
                mObservable = getClient().create().postBody(mUrl, mRequestBody);
                return;
            }
            if (mContent != null && mMediaType != null) {
                mRequestBody = RequestBody.create(mMediaType, mContent);
                mObservable = getClient().create().postBody(mUrl, mRequestBody);
                return;
            }
            if (mParams != null && mParams.size() > 0) {
                mObservable = getClient().create().post(mUrl, mParams);
                return;
            }
            mObservable = getClient().create().post(mUrl);
        }

        @Override
        public <T> void request(final SimpleCallback<T> callback) {
            prepare();
            DisposableObserver<T> disposableObserver = new ApiObserver<T>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .observeOn(Schedulers.mainThread())
                    .subscribe(new ApiRetryFunc<T>(disposableObserver,
                            getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis,
                            new ApiRetryFunc.OnRetry<T>() {
                                @NonNull
                                @Override
                                public Observable.Observe<T> observe() {
                                    return mObservable.subscribeOn(Schedulers.io())
                                            .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                            .observeOn(Schedulers.mainThread());
                                }
                            }));
        }

        @Override
        public <T, R> void request(final AsyncCallback<T, R> callback) {
            prepare();
            DisposableObserver<R> disposableObserver = new AsyncApiObserver<T, R>(mTag, callback);
            if (mTag != null) {
                RequestManager.getIns().add(mTag, disposableObserver);
            }
            mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                    .map(new MapFunc<T, R>(callback))
                    .observeOn(Schedulers.mainThread())
                    .subscribe(new ApiRetryFunc<R>(disposableObserver,
                            getClient().getHttpConfig().retryCount,
                            getClient().getHttpConfig().retryDelayMillis,
                            new ApiRetryFunc.OnRetry<R>() {
                                @NonNull
                                @Override
                                public Observable.Observe<R> observe() {
                                    return mObservable.subscribeOn(Schedulers.io())
                                            .map(new ApiFunc<T>(Util.getFirstCls(callback)))
                                            .map(new MapFunc<T, R>(callback))
                                            .observeOn(Schedulers.mainThread());
                                }
                            }));
        }

        @Override
        public <T> Observable.Observe<T> observable(Class<T> clazz) {
            prepare();
            return mObservable.subscribeOn(Schedulers.io())
                    .map(new ApiFunc<T>(clazz));
        }

        @Override
        public Singleton addForm(String formKey, Object formValue) {
            if (formKey != null && formValue != null) {
                mForms.put(formKey, formValue);
            }
            return this;
        }

        public Singleton setRequestBody(RequestBody requestBody) {
            this.mRequestBody = requestBody;
            return this;
        }

        @Override
        public Singleton setString(String string) {
            this.mContent = string;
            this.mMediaType = MediaTypes.TEXT_PLAIN_TYPE;
            return this;
        }

        public Singleton setString(String string, MediaType mediaType) {
            this.mContent = string;
            this.mMediaType = mediaType;
            return this;
        }

        @Override
        public Singleton setJson(String json) {
            this.mContent = json;
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        @Override
        public Singleton setJson(JSONObject jsonObject) {
            this.mContent = jsonObject.toString();
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        @Override
        public Singleton setJson(JSONArray jsonArray) {
            this.mContent = jsonArray.toString();
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }
    }
}
