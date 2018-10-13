package com.d.lib.rxnet.request;

import android.text.TextUtils;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.HttpClient;
import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.IRequest;
import com.d.lib.rxnet.base.RequestManager;
import com.d.lib.rxnet.callback.ProgressCallback;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.observer.DownloadObserver;
import com.d.lib.rxnet.utils.Util;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.ResponseBody;

/**
 * Created by D on 2017/10/24.
 */
public class DownloadRequest extends IRequest<DownloadRequest> {
    protected Map<String, String> mParams;

    public DownloadRequest(String url) {
        this(url, null);
    }

    public DownloadRequest(String url, Map<String, String> params) {
        this(url, params, null);
    }

    public DownloadRequest(String url, Map<String, String> params, HttpConfig config) {
        this.mUrl = url;
        this.mParams = params;
        this.mConfig = config != null ? config : HttpConfig.getDefault();
    }

    @Override
    protected HttpClient getClient() {
        return HttpClient.create(HttpClient.TYPE_DOWNLOAD, mConfig.log(false));
    }

    private void prepare() {
        if (mParams == null || mParams.size() <= 0) {
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).download(mUrl);
        } else {
            mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).download(mUrl, mParams);
        }
    }

    public void request(final String path, final String name, final ProgressCallback callback) {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("This path can not be empty!");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("This name can not be empty!");
        }
        if (callback == null) {
            throw new NullPointerException("This callback must not be null!");
        }
        prepare();
        requestImpl(mObservable, getClient().getHttpConfig(), mTag, path, name, callback);
    }

    private static void requestImpl(final Observable<ResponseBody> observable,
                                    final HttpConfig config,
                                    final Object tag,
                                    final String path, final String name,
                                    final ProgressCallback callback) {
        Util.executeMain(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onStart();
                }
            }
        });
        DisposableObserver<ResponseBody> disposableObserver = new DownloadObserver(path, name, callback);
        if (tag != null) {
            RequestManager.getIns().add(tag, disposableObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    @Override
    public DownloadRequest baseUrl(String baseUrl) {
        return super.baseUrl(baseUrl);
    }

    @Override
    public DownloadRequest headers(Map<String, String> headers) {
        return super.headers(headers);
    }

    @Override
    public DownloadRequest headers(HeadersInterceptor.OnHeadInterceptor onHeadInterceptor) {
        return super.headers(onHeadInterceptor);
    }

    @Override
    public DownloadRequest connectTimeout(long timeout) {
        return super.connectTimeout(timeout);
    }

    @Override
    public DownloadRequest readTimeout(long timeout) {
        return super.readTimeout(timeout);
    }

    @Override
    public DownloadRequest writeTimeout(long timeout) {
        return super.writeTimeout(timeout);
    }

    @Override
    public DownloadRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return super.sslSocketFactory(sslSocketFactory);
    }

    @Override
    public DownloadRequest addInterceptor(Interceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public DownloadRequest addNetworkInterceptors(Interceptor interceptor) {
        return super.addNetworkInterceptors(interceptor);
    }

    @Override
    public DownloadRequest retryCount(int retryCount) {
        return super.retryCount(retryCount);
    }

    @Override
    public DownloadRequest retryDelayMillis(long retryDelayMillis) {
        return super.retryDelayMillis(retryDelayMillis);
    }

    /**
     * Singleton
     */
    public static class Singleton extends IRequest<Singleton> {

        public Singleton(String url) {
            this(url, null);
        }

        public Singleton(String url, Map<String, String> params) {
            this.mUrl = url;
            this.mParams = params;
        }

        @Override
        protected HttpClient getClient() {
            return HttpClient.getDefault(HttpClient.TYPE_DOWNLOAD);
        }

        private void prepare() {
            if (mParams == null || mParams.size() <= 0) {
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).download(mUrl);
            } else {
                mObservable = getClient().getRetrofitClient().create(RetrofitAPI.class).download(mUrl, mParams);
            }
        }

        public void request(final String path, final String name, final ProgressCallback callback) {
            if (TextUtils.isEmpty(path)) {
                throw new IllegalArgumentException("This path can not be empty!");
            }
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("This name can not be empty!");
            }
            if (callback == null) {
                throw new NullPointerException("This callback must not be null!");
            }
            prepare();
            requestImpl(mObservable, getClient().getHttpConfig(), mTag, path, name, callback);
        }
    }
}
