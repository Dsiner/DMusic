package com.d.lib.rxnet.request;

import android.text.TextUtils;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.ApiManager;
import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.IRequest;
import com.d.lib.rxnet.base.RetrofitClient;
import com.d.lib.rxnet.callback.DownloadCallback;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.interceptor.HeadersInterceptor;
import com.d.lib.rxnet.observer.DownloadObserver;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by D on 2017/10/24.
 */
public class DownloadRequest extends IRequest<DownloadRequest> {
    protected Map<String, String> params;

    public DownloadRequest(String url) {
        this(null, url, null);
    }

    public DownloadRequest(String url, Map<String, String> params) {
        this(null, url, params);
    }

    public DownloadRequest(HttpConfig config, String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
        this.config = config != null ? config : HttpConfig.getNewDefault();
    }

    @Override
    protected Retrofit getClient() {
        return RetrofitClient.getRetrofit(config, false);
    }

    private void prepare() {
        if (params == null) {
            observable = getClient().create(RetrofitAPI.class).download(url);
        } else {
            observable = getClient().create(RetrofitAPI.class).download(url, params);
        }
    }

    public void request(final String path, final String name, final DownloadCallback callback) {
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
        requestImpl(observable, config, path, name, callback, tag);
    }


    private static void requestImpl(final Observable observable,
                                    final HttpConfig config,
                                    final String path, final String name,
                                    final DownloadCallback callback,
                                    final Object tag) {
        DisposableObserver disposableObserver = new DownloadObserver(callback);
        if (tag != null) {
            ApiManager.get().add(tag, disposableObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new Function<ResponseBody, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(final ResponseBody responseBody) throws Exception {
                        return Flowable.create(new FlowableOnSubscribe<DownloadModel>() {
                            @Override
                            public void subscribe(FlowableEmitter<DownloadModel> subscriber) throws Exception {
                                File dir = new File(path);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                File file = new File(dir.getPath() + File.separator + name);
                                saveFile(subscriber, file, responseBody);
                            }
                        }, BackpressureStrategy.LATEST);
                    }
                })
                .sample(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis))
                .subscribe(disposableObserver);
    }

    private static void saveFile(FlowableEmitter<? super DownloadModel> sub, File saveFile, ResponseBody resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[8192];

                DownloadModel downModel = new DownloadModel();
                inputStream = resp.byteStream();
                outputStream = new FileOutputStream(saveFile);

                long contentLength = resp.contentLength();
                downModel.totalSize = contentLength;
                sub.onNext(downModel);

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    downModel.downloadSize = downloadSize;
                    sub.onNext(downModel);
                }
                outputStream.flush();
                sub.onComplete();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (IOException e) {
            sub.onError(e);
        }
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

    public static class DownloadModel {
        public long downloadSize;
        public long totalSize;
    }

    /**
     * Singleton
     */
    public static class Singleton extends IRequest<Singleton> {
        protected Map<String, String> params;

        public Singleton(String url) {
            this(url, null);
        }

        public Singleton(String url, Map<String, String> params) {
            this.url = url;
            this.params = params;
            this.config = config != null ? config : HttpConfig.getNewDefault();
        }

        @Override
        protected Retrofit getClient() {
            return RetrofitClient.getTransfer();
        }

        private void prepare() {
            if (params == null) {
                observable = getClient().create(RetrofitAPI.class).download(url);
            } else {
                observable = getClient().create(RetrofitAPI.class).download(url, params);
            }
        }

        public void request(final String path, final String name, final DownloadCallback callback) {
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
            requestImpl(observable, config, path, name, callback, tag);
        }
    }
}
