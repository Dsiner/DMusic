package com.d.lib.aster.integration.retrofit;

import com.d.lib.aster.base.Config;
import com.d.lib.aster.integration.retrofit.func.ApiFunc;
import com.d.lib.aster.integration.retrofit.func.ApiRetryFunc;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Transformer
 */
public class ApiTransformer {

    public static <T> ObservableTransformer<T, T> norTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(Config.getDefault().retryCount,
                                Config.getDefault().retryDelayMillis));
            }
        };
    }

    /**
     * e.g observable.compose(this.<T>norTransformer(callback))
     */
    protected <OTF> ObservableTransformer<ResponseBody, OTF> norTransformer(final Class<OTF> clazz,
                                                                            final Config config) {
        return new ObservableTransformer<ResponseBody, OTF>() {
            @Override
            public ObservableSource<OTF> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new ApiFunc<OTF>(clazz))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(config.retryCount, config.retryDelayMillis));
            }
        };
    }

    public static <T> ObservableTransformer<T, T> norTransformer(final int retryCount, final int retryDelayMillis) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }
}
