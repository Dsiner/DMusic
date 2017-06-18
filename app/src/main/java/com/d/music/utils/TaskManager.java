package com.d.music.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局任务类，任务使用全局线程池，暂仅支持cachedThreadPool
 */
public class TaskManager {
    private static TaskManager ins;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private TaskManager() {
    }

    public static TaskManager getIns() {
        if (ins == null) {
            ins = new TaskManager();
        }
        return ins;
    }

    /**
     * 执行异步任务
     *
     * @param runnable:runnable
     */
    public void executeTask(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
