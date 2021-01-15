package com.d.music.component.cache.utils.threadpool;

import androidx.annotation.NonNull;

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

    public static ThreadPool getInstance() {
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
                TaskManager.getInstance().executeMain(r);
            }

            @Override
            public void executeTask(Runnable r) {
                TaskManager.getInstance().executeTask(r);
            }

            @Override
            public void executeDownload(Runnable r) {
                TaskManager.getInstance().executeDownload(r);
            }

            @Override
            public void executeNew(Runnable r) {
                TaskManager.getInstance().executeNew(r);
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

    public abstract void executeDownload(Runnable r);

    /**
     * Execute async task in a new thread
     */
    public abstract void executeNew(Runnable r);
}
