package com.d.lib.common.event.bus.callback;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ProgressCallback
 * Created by D on 2018/1/23.
 */
public interface ProgressCallback<T> extends SimpleCallback<T> {
    int DONE = 0;
    int ERROR = 1;
    int RUNNING = 2;
    int PENDING = 3;

    @IntDef({DONE, ERROR, RUNNING, PENDING})
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {

    }

    void onStart();

    void onProgress(@NonNull T progress);

    void onPending();

    void onCancel();
}
