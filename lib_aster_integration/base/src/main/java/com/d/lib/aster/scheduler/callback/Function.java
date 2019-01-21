package com.d.lib.aster.scheduler.callback;

import android.support.annotation.NonNull;

/**
 * Function
 * Created by D on 2018/5/15.
 */
public interface Function<T, R> {
    /**
     * Apply some calculation to the input value and return some other value.
     *
     * @param t the input value
     * @return the output value
     * @throws Exception on error
     */
    R apply(@NonNull T t) throws Exception;
}
