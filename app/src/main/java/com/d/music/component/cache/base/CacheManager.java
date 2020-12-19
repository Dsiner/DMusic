package com.d.music.component.cache.base;

import android.content.Context;

/**
 * Created by D on 2017/10/18.
 */
public class CacheManager {
    protected volatile static ACache aCache;

    protected CacheManager(Context context) {
        init(context.getApplicationContext());
    }

    private void init(Context context) {
        if (aCache == null) {
            synchronized (CacheManager.class) {
                if (aCache == null) {
                    aCache = ACache.get(context);
                }
            }
        }
    }
}
