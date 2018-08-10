package com.d.lib.common.module.taskscheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TaskManager
 */
public class TaskManager {
    private volatile static TaskManager ins;

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
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMain(Runnable r) {
        return mainHandler.post(r);
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMainDelayed(Runnable r, long delayMillis) {
        return mainHandler.postDelayed(r, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    void executeMain(Runnable r) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            if (r != null) {
                r.run();
            }
            return;
        }
        mainHandler.post(r);
    }

    /**
     * Execute async task in the cached thread pool
     */
    void executeTask(Runnable r) {
        cachedThreadPool.execute(r);
    }

    /**
     * Execute async task in the single thread pool
     */
    void executeSingle(Runnable r) {
        singleThreadExecutor.execute(r);
    }

    /**
     * Execute async task in a new thread
     */
    void executeNew(Runnable r) {
        new Thread(r).start();
    }
}
