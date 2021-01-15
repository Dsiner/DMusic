package com.d.music.component.cache.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.d.music.component.cache.base.AbstractCacheManager;
import com.d.music.component.cache.base.PreFix;
import com.d.music.component.cache.bean.FrameBean;
import com.d.music.component.cache.listener.CacheListener;
import com.d.music.component.cache.utils.Util;

import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class FrameCacheManager extends AbstractCacheManager<String, FrameBean> {
    private volatile static FrameCacheManager instance;

    private FrameCacheManager(Context context) {
        super(context);
        mLruCache.setCount(12);
    }

    public static FrameCacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FrameCacheManager.class) {
                if (instance == null) {
                    instance = new FrameCacheManager(context);
                }
            }
        }
        return instance;
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.FRAME;
    }

    @NonNull
    protected String getPreFixDuration() {
        return PreFix.DURATION;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, String url, CacheListener<FrameBean> listener) {
        // Also can use ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && url.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                mmr.setDataSource(url, headers);
            } else {
                mmr.setDataSource(context, Uri.parse(url));
            }
            // Get the first frame picture
            Bitmap bitmap = mmr.getFrameAtTime();
            // Get duration(milliseconds)
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            FrameBean frameBean = new FrameBean();
            frameBean.drawable = Util.bitmapToDrawableByBD(bitmap);
            frameBean.duration = duration;
            // Save to disk
            putDisk(url, frameBean);
            success(url, frameBean, listener);
        } catch (Throwable e) {
            Log.e("Cache", e.toString());
            e.printStackTrace();
            error(url, e, listener);
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    @Override
    protected FrameBean getDisk(String url) {
        Drawable drawable = aCache.getAsDrawable(getPreFix() + url);
        Long duration = (Long) aCache.getAsObject(getPreFixDuration() + url);
        if (drawable == null || duration == null) {
            return null;
        }
        FrameBean value = new FrameBean();
        value.drawable = drawable;
        value.duration = duration;
        return value;
    }

    @Override
    protected void putDisk(String url, FrameBean value) {
        aCache.put(getPreFix() + url, value.drawable);
        aCache.put(getPreFixDuration() + url, value.duration);
    }
}
