package com.d.lib.rxnet.request;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.callback.AsyncCallback;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.mode.MediaTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by D on 2017/10/24.
 */
public class PostRequest extends HttpRequest<PostRequest> {
    private Map<String, Object> mForms = new LinkedHashMap<>();
    private RequestBody mRequestBody;
    private MediaType mMediaType;
    private String mContent;

    public PostRequest(String url) {
        super(url);
    }

    public PostRequest(String url, Map<String, String> params) {
        super(url, params);
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
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postForm(mUrl, mForms);
            return;
        }
        if (mRequestBody != null) {
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postBody(mUrl, mRequestBody);
            return;
        }
        if (mContent != null && mMediaType != null) {
            mRequestBody = RequestBody.create(mMediaType, mContent);
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postBody(mUrl, mRequestBody);
            return;
        }
        if (mParams != null && mParams.size() > 0) {
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).post(mUrl, mParams);
            return;
        }
        mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).post(mUrl);
    }

    @Override
    public <T> void request(SimpleCallback<T> callback) {
        super.request(callback);
    }

    @Override
    public <T, R> void request(AsyncCallback<T, R> callback) {
        super.request(callback);
    }

    @Override
    public <T> Observable<T> observable(Class<T> clazz) {
        return super.observable(clazz);
    }

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

    public PostRequest setJson(String json) {
        this.mContent = json;
        this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostRequest setJson(JSONObject jsonObject) {
        this.mContent = jsonObject.toString();
        this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

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
    public PostRequest addInterceptor(Interceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public PostRequest addNetworkInterceptors(Interceptor interceptor) {
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
    public static class Singleton extends HttpRequest.Singleton<Singleton> {
        private Map<String, Object> mForms = new LinkedHashMap<>();
        private RequestBody mRequestBody;
        private MediaType mMediaType;
        private String mContent;

        public Singleton(String url) {
            super(url);
        }

        public Singleton(String url, Map<String, String> params) {
            super(url, params);
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
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postForm(mUrl, mForms);
                return;
            }
            if (mRequestBody != null) {
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postBody(mUrl, mRequestBody);
                return;
            }
            if (mContent != null && mMediaType != null) {
                mRequestBody = RequestBody.create(mMediaType, mContent);
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).postBody(mUrl, mRequestBody);
                return;
            }
            if (mParams != null && mParams.size() > 0) {
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).post(mUrl, mParams);
                return;
            }
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).post(mUrl);
        }

        @Override
        public <T> void request(SimpleCallback<T> callback) {
            super.request(callback);
        }

        @Override
        public <T, R> void request(AsyncCallback<T, R> callback) {
            super.request(callback);
        }

        @Override
        public <T> Observable<T> observable(Class<T> clazz) {
            return super.observable(clazz);
        }

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

        public Singleton setJson(String json) {
            this.mContent = json;
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        public Singleton setJson(JSONObject jsonObject) {
            this.mContent = jsonObject.toString();
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        public Singleton setJson(JSONArray jsonArray) {
            this.mContent = jsonArray.toString();
            this.mMediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }
    }
}
