package com.d.lib.common.component.cache;

import com.d.lib.common.component.cache.utils.threadpool.ThreadPool;

/**
 * Cache
 * Created by D on 2018/8/25.
 */
public class Cache {
    public static void setThreadPool(ThreadPool pool) {
        ThreadPool.setThreadPool(pool);
    }
}
