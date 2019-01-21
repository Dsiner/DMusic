package com.d.lib.aster.scheduler.schedule;

import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.d.lib.aster.scheduler.Observable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Schedulers
 * Created by D on 2018/5/15.
 */
public class Schedulers {
    final static int DEFAULT_THREAD = 0;
    final static int NEW_THREAD = 1;
    final static int IO = 2;
    final static int MAIN_THREAD = 3;

    @IntDef({DEFAULT_THREAD, NEW_THREAD, IO, MAIN_THREAD})
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scheduler {

    }

    @Scheduler
    public static int defaultThread() {
        return DEFAULT_THREAD;
    }

    @Scheduler
    public static int newThread() {
        return NEW_THREAD;
    }

    @Scheduler
    public static int io() {
        return IO;
    }

    @Scheduler
    public static int mainThread() {
        return MAIN_THREAD;
    }

    /**
     * Executes the given runnable at some time in the future.
     * The runnable may execute in a new thread, in a pooled thread, or in the calling thread
     */
    public static void switchThread(@Scheduler final int scheduler, @NonNull final Runnable runnable) {
        if (scheduler == NEW_THREAD) {
            Observable.executeNew(runnable);
            return;
        } else if (scheduler == IO) {
            Observable.executeTask(runnable);
            return;
        } else if (scheduler == MAIN_THREAD) {
            Observable.executeMain(runnable);
            return;
        }
        runnable.run();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
