package com.d.lib.rxnet.request;

import android.text.TextUtils;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.ApiManager;
import com.d.lib.rxnet.base.HttpConfig;
import com.d.lib.rxnet.base.RetrofitClient;
import com.d.lib.rxnet.func.ApiRetryFunc;
import com.d.lib.rxnet.listener.DownloadCallBack;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.ResponseBody;

/**
 * New but Default Config
 * Created by D on 2017/10/24.
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {
    protected Map<String, String> params;

    public DownloadRequest(String url) {
        this.url = url;
        this.config = HttpConfig.getDefaultConfig();
    }

    public DownloadRequest(String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
        this.config = HttpConfig.getDefaultConfig();
    }

    protected void init() {
        if (params == null) {
            observable = RetrofitClient.getRetrofitDown(HttpConfig.getDefaultConfig())
                    .create(RetrofitAPI.class).download(url);
        } else {
            observable = RetrofitClient.getRetrofitDown(HttpConfig.getDefaultConfig())
                    .create(RetrofitAPI.class).download(url, params);
        }
    }

    public void request(final String path, final String name, final DownloadCallBack callback) {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("this path can not be empty!");
        }
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("this name can not be empty!");
        }
        if (callback == null) {
            throw new NullPointerException("this callback is null!");
        }
        init();
        DisposableObserver disposableObserver = new DownloadObserver(callback);
        if (super.tag != null) {
            ApiManager.get().add(super.tag, disposableObserver);
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

    private void saveFile(FlowableEmitter<? super DownloadModel> sub, File saveFile, ResponseBody resp) {
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
    protected DownloadRequest baseUrl(String baseUrl) {
        return this;
    }

    @Override
    protected DownloadRequest headers(Map<String, String> headers) {
        return this;
    }

    @Override
    protected DownloadRequest connectTimeout(long timeout) {
        return this;
    }

    @Override
    protected DownloadRequest readTimeout(long timeout) {
        return this;
    }

    @Override
    protected DownloadRequest writeTimeout(long timeout) {
        return this;
    }

    @Override
    protected DownloadRequest addInterceptor(Interceptor interceptor) {
        return this;
    }

    @Override
    protected DownloadRequest addNetworkInterceptors(Interceptor interceptor) {
        return this;
    }

    @Override
    protected DownloadRequest sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return this;
    }

    @Override
    protected DownloadRequest retryCount(int retryCount) {
        return this;
    }

    @Override
    protected DownloadRequest retryDelayMillis(long retryDelayMillis) {
        return this;
    }

    public static class DownloadModel {
        public long downloadSize;
        public long totalSize;
    }

    /**
     * New
     */
    public static class DownloadRequestF extends DownloadRequest {

        public DownloadRequestF(String url) {
            super(url);
            this.config = HttpConfig.getNewDefaultConfig();
        }

        public DownloadRequestF(String url, Map<String, String> params) {
            super(url, params);
            this.config = HttpConfig.getNewDefaultConfig();
        }

        @Override
        protected void init() {
            if (params == null) {
                observable = RetrofitClient.getRetrofitDown(config).create(RetrofitAPI.class).download(url);
            } else {
                observable = RetrofitClient.getRetrofitDown(config).create(RetrofitAPI.class).download(url, params);
            }
        }

        @Override
        public DownloadRequestF tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /******************* Config *******************/
        @Override
        public DownloadRequestF baseUrl(String baseUrl) {
            config.baseUrl(baseUrl);
            return this;
        }

        @Override
        public DownloadRequestF headers(Map<String, String> headers) {
            config.headers(headers);
            return this;
        }

        @Override
        public DownloadRequestF connectTimeout(long timeout) {
            config.connectTimeout(timeout);
            return this;
        }

        @Override
        public DownloadRequestF readTimeout(long timeout) {
            config.readTimeout(timeout);
            return this;
        }

        @Override
        public DownloadRequestF writeTimeout(long timeout) {
            config.writeTimeout(timeout);
            return this;
        }

        @Override
        public DownloadRequestF addInterceptor(Interceptor interceptor) {
            config.addInterceptor(interceptor);
            return this;
        }

        @Override
        public DownloadRequestF addNetworkInterceptors(Interceptor interceptor) {
            config.addNetworkInterceptors(interceptor);
            return this;
        }

        @Override
        public DownloadRequestF sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            config.sslSocketFactory(sslSocketFactory);
            return this;
        }

        @Override
        public DownloadRequestF retryCount(int retryCount) {
            config.retryCount(retryCount);
            return this;
        }

        @Override
        public DownloadRequestF retryDelayMillis(long retryDelayMillis) {
            config.retryDelayMillis(retryDelayMillis);
            return this;
        }
    }
}
