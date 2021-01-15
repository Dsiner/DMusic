package com.d.music.component.cache.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.d.lib.aster.callback.SimpleCallback;
import com.d.music.component.cache.base.CacheManager;
import com.d.music.component.cache.base.ExpireLruCache;
import com.d.music.component.cache.listener.CacheListener;
import com.d.music.component.cache.utils.threadpool.ThreadPool;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class LinkCacheManager extends CacheManager {
    private volatile static LinkCacheManager mInstance;

    private ExpireLruCache<String, String> mLruCache;
    private HashMap<String, ArrayList<CacheListener<String>>> mHashMap;

    private LinkCacheManager(Context context) {
        super(context);
        mLruCache = new ExpireLruCache<>(60, 2 * 60 * 60 * 1000);
        mHashMap = new HashMap<>();
    }

    public static LinkCacheManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LinkCacheManager.class) {
                if (mInstance == null) {
                    mInstance = new LinkCacheManager(context);
                }
            }
        }
        return mInstance;
    }

    public void load(final Context context, final MusicModel key, final CacheListener<String> listener) {
        if (isLoading(key, listener)) {
            return;
        }
        if (isLru(key, listener)) {
            return;
        }
        ThreadPool.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                if (isDisk(key, listener)) {
                    return;
                }
                absLoad(context, key, listener);
            }
        });
    }

    private void success(final MusicModel key, final String value, final CacheListener<String> l) {
        ThreadPool.getInstance().executeMain(new Runnable() {
            @Override
            public void run() {
                successImplementation(key, value);
            }
        });
    }

    private void successImplementation(final MusicModel key, final String value) {
        // Save to cache
        putLru(key, value);
        ArrayList<CacheListener<String>> listeners = mHashMap.get(key.id);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                CacheListener<String> listener = listeners.get(i);
                listener.onSuccess(value);
            }
            mHashMap.remove(key.id);
        }
    }

    private void error(final MusicModel key, final Throwable e, final CacheListener<String> l) {
        ThreadPool.getInstance().executeMain(new Runnable() {
            @Override
            public void run() {
                errorImplementation(key, e);
            }
        });
    }

    private void errorImplementation(final MusicModel key, final Throwable e) {
        ArrayList<CacheListener<String>> listeners = mHashMap.get(key.id);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onError(e);
            }
            mHashMap.remove(key.id);
        }
    }

    private boolean isLoading(final MusicModel key, final CacheListener<String> l) {
        if (mHashMap.containsKey(key.id)) {
            if (l != null) {
                ArrayList<CacheListener<String>> listeners = mHashMap.get(key.id);
                listeners.add(l);
                l.onLoading();
            }
            return true;
        }
        if (l != null) {
            l.onLoading();
            ArrayList<CacheListener<String>> listeners = new ArrayList<>();
            listeners.add(l);
            mHashMap.put(key.id, listeners);
        }
        return false;
    }

    @NonNull
    private String getPreFix() {
        return "";
    }

    private void absLoad(Context context, final MusicModel key, final CacheListener<String> listener) {
        Transfer.getInfo(key, new SimpleCallback<MusicModel>() {
            @Override
            public void onSuccess(MusicModel response) {
                final String path = response.songUrl;
                putDisk(key, path);
                success(key, path, listener);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Cache", e.toString());
                e.printStackTrace();
                error(key, e, listener);
            }
        });
    }

    private boolean isLru(MusicModel key, CacheListener<String> listener) {
        final String path = HitTarget.hitSong(key);
        if (!TextUtils.isEmpty(path) && FileUtils.isFileExist(path)) {
            success(key, path, listener);
            return true;
        }
        final String valueLru = mLruCache.get(key.id);
        if (valueLru != null) {
            success(key, valueLru, listener);
            return true;
        }
        return false;
    }

    private void putLru(MusicModel key, String value) {
        mLruCache.put(key.id, value);
    }

    private boolean isDisk(final MusicModel key, final CacheListener<String> listener) {
        final String valueDisk = getDisk(key);
        if (valueDisk != null) {
            success(key, valueDisk, listener);
            return true;
        }
        return false;
    }

    public void release() {
        mLruCache.clear();
    }

    private String getDisk(MusicModel key) {
        return null;
    }

    private void putDisk(MusicModel key, String value) {

    }
}
