package com.d.lib.aster.scheduler.callback;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DisposableObserver
 * Created by D on 2018/12/6.
 **/
public abstract class DisposableObserver<T> implements Observer<T> {
    private AtomicBoolean disposed = new AtomicBoolean(false);

    public final boolean isDisposed() {
        return disposed.get();
    }

    public final void dispose() {
        disposed.set(true);
    }
}
