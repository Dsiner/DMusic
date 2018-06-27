package com.d.lib.common.module.taskscheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TaskManager
 */
public class TaskManager {
    private static TaskManager ins;

    private Handler mainHandler;
    private ExecutorService cachedThreadPool;
    private ExecutorService singleThreadExecutor;

    private TaskManager() {
        mainHandler = new Handler(Looper.getMainLooper());
        cachedThreadPool = Executors.newCachedThreadPool();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    static TaskManager getIns() {
        if (ins == null) {
            synchronized (TaskManager.class) {
                if (ins == null) {
                    ins = new TaskManager();
                }
            }
        }
        return ins;
    }

    /**
     * Execute sync task in main thread
     */
    void executeMain(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * Execute async task in cached thread pool
     */
    void executeTask(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }

    /**
     * Execute async task in single thread pool
     */
    void executeSingle(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }

    /**
     * Execute async task in a new thread
     */
    void executeNew(Runnable runnable) {
        new Thread(runnable).start();
    }
}
