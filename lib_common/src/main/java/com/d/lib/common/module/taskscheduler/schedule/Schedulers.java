package com.d.lib.common.module.taskscheduler.schedule;

import android.os.Looper;
import android.support.annotation.IntDef;

import com.d.lib.common.module.taskscheduler.TaskScheduler;

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
     * Switch thread
     */
    public static void switchThread(@Scheduler final int scheduler, final Runnable r) {
        if (scheduler == NEW_THREAD) {
            TaskScheduler.executeNew(new Runnable() {
                @Override
                public void run() {
                    if (r != null) {
                        r.run();
                    }
                }
            });
            return;
        } else if (scheduler == IO) {
            TaskScheduler.executeTask(new Runnable() {
                @Override
                public void run() {
                    if (r != null) {
                        r.run();
                    }
                }
            });
            return;
        } else if (scheduler == MAIN_THREAD) {
            TaskScheduler.executeMain(new Runnable() {
                @Override
                public void run() {
                    if (r != null) {
                        r.run();
                    }
                }
            });
            return;
        }
        if (r != null) {
            r.run();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
