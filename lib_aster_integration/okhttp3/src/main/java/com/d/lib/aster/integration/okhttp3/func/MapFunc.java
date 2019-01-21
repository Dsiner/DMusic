package com.d.lib.aster.integration.okhttp3.func;

import com.d.lib.aster.callback.AsyncCallback;
import com.d.lib.aster.scheduler.callback.Function;
import com.d.lib.aster.utils.Util;

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
