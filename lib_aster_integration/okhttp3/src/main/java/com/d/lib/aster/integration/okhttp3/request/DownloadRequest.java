package com.d.lib.aster.integration.okhttp3.request;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.ProgressCallback;
import com.d.lib.aster.integration.okhttp3.OkHttpClient;
import com.d.lib.aster.integration.okhttp3.RequestManager;
import com.d.lib.aster.integration.okhttp3.func.ApiRetryFunc;
import com.d.lib.aster.integration.okhttp3.interceptor.HeadersInterceptor;
import com.d.lib.aster.integration.okhttp3.observer.DownloadObserver;
import com.d.lib.aster.interceptor.IInterceptor;
import com.d.lib.aster.request.IDownloadRequest;
import com.d.lib.aster.scheduler.Observable;
import com.d.lib.aster.scheduler.callback.DisposableObserver;
import com.d.lib.aster.scheduler.schedule.Schedulers;
import com.d.lib.aster.utils.Util;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.ResponseBody;

/**
 * Created by D on 2017/10/24.
 */
public class DownloadRequest extends IDownloadRequest<DownloadRequest, OkHttpClient> {
    protected Observable<ResponseBody> mObservable;

    public DownloadRequest(String url) {
        super(url, null);
    }

    public DownloadRequest(String url, Params params) {
        super(url, params);
    }

    public DownloadRequest(String url, Params params, Config config) {
        super(url, params, config);
    }

    @Override
    protected OkHttpClient getClient() {
        return OkHttpClient.create(IClient.TYPE_DOWNLOAD, mConfig.log(false));
    }

    @Override
    protected void prepare() {
        if (mParams == null || mParams.size() <= 0) {
            mObservable = getClient().create().download(mUrl);
        } else {
            mObservable = getClient().create().download(mUrl, mParams);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void request(@NonNull final String path, @NonNull final String name,
                        @NonNull final ProgressCallback callback) {
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
                                    final Config config,
                                    final Object tag,
                                    final String path, final String name,
                                    final ProgressCallback callback) {
        if (callback != null) {
            Util.executeMain(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();

                }
            });
        }
        DisposableObserver<ResponseBody> disposableObserver = new DownloadObserver(path, name, tag, callback);
        if (tag != null) {
            RequestManager.getIns().add(tag, disposableObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new ApiRetryFunc<ResponseBody>(disposableObserver,
                        config.retryCount, config.retryDelayMillis,
                        new ApiRetryFunc.OnRetry<ResponseBody>() {
                            @NonNull
                            @Override
                            public Observable.Observe<ResponseBody> observe() {
                                return observable.subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io());
                            }
                        }));
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
    public DownloadRequest addInterceptor(IInterceptor interceptor) {
        return super.addInterceptor(interceptor);
    }

    @Override
    public DownloadRequest addNetworkInterceptors(IInterceptor interceptor) {
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
    public static class Singleton extends IDownloadRequest.Singleton<Singleton, OkHttpClient> {
        protected Observable<ResponseBody> mObservable;

        public Singleton(String url) {
            super(url);
        }

        public Singleton(String url, Params params) {
            super(url, params);
        }

        @Override
        protected OkHttpClient getClient() {
            return OkHttpClient.getDefault(IClient.TYPE_DOWNLOAD);
        }

        @Override
        protected void prepare() {
            if (mParams == null || mParams.size() <= 0) {
                mObservable = getClient().create().download(mUrl);
            } else {
                mObservable = getClient().create().download(mUrl, mParams);
            }
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void request(@NonNull final String path, @NonNull final String name,
                            @NonNull final ProgressCallback callback) {
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
