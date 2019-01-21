package com.d.lib.aster.callback;

/**
 * SimpleCallback
 * Created by D on 2017/10/24.
 */
public interface SimpleCallback<R> {
    void onSuccess(R response);

    void onError(Throwable e);
}
