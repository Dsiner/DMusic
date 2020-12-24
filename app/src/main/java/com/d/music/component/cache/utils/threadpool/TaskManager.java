package com.d.music.component.cache.utils.threadpool;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * TaskManager
 */
public class TaskManager {
    private Handler mainHandler;
    private ExecutorService cachedThreadPool;
    private ExecutorService singleThreadExecutor;
    private ExecutorService downloadThreadPool;

    private TaskManager() {
        mainHandler = new Handler(Looper.getMainLooper());
        cachedThreadPool = Executors.newCachedThreadPool();
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        downloadThreadPool = Executors.newFixedThreadPool(3);
    }

    static TaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * Causes the Runnable command to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMain(Runnable command) {
        return mainHandler.post(command);
    }

    /**
     * Causes the Runnable command to be added to the message queue.
     * The runnable will be run in the main thread
     */
    boolean postMainDelayed(Runnable command, long delayMillis) {
        return mainHandler.postDelayed(command, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    void executeMain(Runnable command) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            if (command != null) {
                command.run();
            }
            return;
        }
        mainHandler.post(command);
    }

    /**
     * Execute async task in the cached thread pool
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be accepted for execution
     * @throws NullPointerException       if command is null
     */
    void executeTask(Runnable command) {
        cachedThreadPool.execute(command);
    }

    /**
     * Execute async task in the single thread pool
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be accepted for execution
     * @throws NullPointerException       if command is null
     */
    void executeSingle(Runnable command) {
        singleThreadExecutor.execute(command);
    }

    void executeDownload(Runnable command) {
        downloadThreadPool.execute(command);
    }

    /**
     * Execute async task in a new thread
     *
     * @param command the object whose {@code run} method is invoked when this thread
     *                is started. If {@code null}, this classes {@code run} method does
     *                nothing.
     */
    void executeNew(Runnable command) {
        new Thread(command).start();
    }

    private static class Singleton {
        private static final TaskManager INSTANCE = new TaskManager();
    }
}
