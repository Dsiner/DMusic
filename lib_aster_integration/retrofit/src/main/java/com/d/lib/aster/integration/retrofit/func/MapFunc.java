package com.d.lib.aster.integration.retrofit.func;

import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.utils.Util;

import io.reactivex.functions.Function;

/**
 * Map with AsyncCallback
 */
public class MapFunc<T, R> implements Function<T, R> {
    private AsyncCallback<T, R> mCallback;

    public MapFunc(AsyncCallback<T, R> callback) {
        this.mCallback = callback;
    }

    @Override
    public R apply(T responseBody) throws Exception {
        Util.printThread("Aster_thread callback apply");
        return mCallback.apply(responseBody);
    }
}
