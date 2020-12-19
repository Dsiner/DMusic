package com.d.music.component.cache;

import com.d.music.component.cache.utils.threadpool.ThreadPool;

/**
 * Cache
 * Created by D on 2018/8/25.
 */
public class Cache {
    public static void setThreadPool(ThreadPool pool) {
        ThreadPool.setThreadPool(pool);
    }
}
