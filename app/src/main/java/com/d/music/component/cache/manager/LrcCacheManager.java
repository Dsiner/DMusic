package com.d.music.component.cache.manager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.common.component.cache.base.AbstractCacheManager;
import com.d.lib.common.component.cache.listener.CacheListener;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.utils.FileUtil;

/**
 * Created by D on 2017/10/18.
 */
public class LrcCacheManager extends AbstractCacheManager<MusicModel, String> {
    private volatile static LrcCacheManager mInstance;

    public static LrcCacheManager getIns(Context context) {
        if (mInstance == null) {
            synchronized (LrcCacheManager.class) {
                if (mInstance == null) {
                    mInstance = new LrcCacheManager(context);
                }
            }
        }
        return mInstance;
    }

    private LrcCacheManager(Context context) {
        super(context);
        mLruCache.setCount(0);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final MusicModel key, final CacheListener<String> listener) {
        Transfer.downloadLrcCache(key, new SimpleCallback<MusicModel>() {
            @Override
            public void onSuccess(MusicModel response) {
                final String path = HitTarget.hitLrc(response);
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

    @Override
    protected boolean isLru(MusicModel key, CacheListener<String> listener) {
        final String path = HitTarget.hitLrc(key);
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
