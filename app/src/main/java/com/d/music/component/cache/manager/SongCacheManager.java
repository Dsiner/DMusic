package com.d.music.component.cache.manager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.component.cache.base.AbstractCacheManager;
import com.d.lib.common.component.cache.listener.CacheListener;
import com.d.lib.common.component.cache.utils.threadpool.ThreadPool;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.utils.FileUtil;

/**
 * Created by D on 2017/10/18.
 */
public class SongCacheManager extends AbstractCacheManager<MusicModel, String> {
    private volatile static SongCacheManager mInstance;

    public static SongCacheManager getIns(Context context) {
        if (mInstance == null) {
            synchronized (SongCacheManager.class) {
                if (mInstance == null) {
                    mInstance = new SongCacheManager(context);
                }
            }
        }
        return mInstance;
    }

    private SongCacheManager(Context context) {
        super(context);
        mLruCache.setCount(0);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return "";
    }

    @Override
    public void load(final Context context, final MusicModel key, final CacheListener<String> listener) {
        if (isLoading(key, listener)) {
            return;
        }
        if (isLru(key, listener)) {
            return;
        }
        ThreadPool.getIns().executeDownload(new Runnable() {
            @Override
            public void run() {
                if (isDisk(key, listener)) {
                    return;
                }
                absLoad(context, key, listener);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final MusicModel key, final CacheListener<String> listener) {
        Transfer.downloadCache(key, false, new Transfer.OnTransferCallback<MusicModel>() {
            @Override
            public void onFirst(MusicModel model) {

            }

            @Override
            public void onSecond(MusicModel model) {
                final String path = HitTarget.hitSong(model);
                putDisk(key, path);
                success(key, path, listener);
            }

            @Override
            public void onError(MusicModel model, Throwable e) {
                Log.e("Cache", e.toString());
                e.printStackTrace();
                error(key, e, listener);
            }
        });
    }

    @Override
    protected boolean isLru(MusicModel key, CacheListener<String> listener) {
        final String path = HitTarget.hitSong(key);
        if (!TextUtils.isEmpty(path) && FileUtil.isFileExist(path)) {
            success(key, path, listener);
            return true;
        }
        return false;
    }

    @Override
    protected void putLru(MusicModel key, String value) {

    }

    @Override
    protected String getDisk(MusicModel key) {
        return null;
    }

    @Override
    protected void putDisk(MusicModel key, String value) {

    }
}
