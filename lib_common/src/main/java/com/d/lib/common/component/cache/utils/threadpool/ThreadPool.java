package com.d.lib.common.component.cache.utils.threadpool;

import android.support.annotation.NonNull;

/**
 * Abstract thread pool, you can also implement it yourself,
 * the default implementation here is TaskManager
 * Created by D on 2018/8/25.
 */
public abstract class ThreadPool {
    private volatile static ThreadPool pool;

    protected ThreadPool() {
    }

    public static void setThreadPool(ThreadPool pool) {
        synchronized (ThreadPool.class) {
            if (ThreadPool.pool == null) {
                // Initialize only once
                ThreadPool.pool = pool;
            }
        }
    }

    public static ThreadPool getIns() {
        if (pool == null) {
            // Not implemented, then use the default
            synchronized (ThreadPool.class) {
                if (pool == null) {
                    pool = getDefaultPool();
                }
            }
        }
        return pool;
    }

    @NonNull
    private static ThreadPool getDefaultPool() {
        return new ThreadPool() {
            @Override
            public void executeMain(Runnable r) {
                TaskManager.getIns().executeMain(r);
            }

            @Override
            public void executeTask(Runnable r) {
                TaskManager.getIns().executeTask(r);
            }

            @Override
            public void executeNew(Runnable r) {
                TaskManager.getIns().executeNew(r);
            }
        };
    }

    /**
     * Execute sync task in the main thread
     */
    public abstract void executeMain(Runnable r);

    /**
     * Execute async task in the cached thread pool
     */
    public abstract void executeTask(Runnable r);

    /**
     * Execute async task in a new thread
     */
    public abstract void executeNew(Runnable r);
}
