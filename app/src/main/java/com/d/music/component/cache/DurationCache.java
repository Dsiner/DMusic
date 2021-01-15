package com.d.music.component.cache;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;

import com.d.music.R;
import com.d.music.component.cache.base.AbstractCache;
import com.d.music.component.cache.exception.CacheException;
import com.d.music.component.cache.listener.CacheListener;
import com.d.music.component.cache.listener.IDuration;
import com.d.music.component.cache.manager.DurationCacheManager;
import com.d.music.component.cache.utils.Util;

/**
 * Cache - Get media duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class DurationCache extends AbstractCache<DurationCache, View, String, Long, Long> {

    private DurationCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_pub_cache_tag_duration;
    }

    @UiThread
    public static DurationCache with(Context context) {
        return new DurationCache(context);
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void clear(View view) {
        if (view == null) {
            return;
        }
        view.setTag(getTag(), "");
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void release(Context context) {
        if (context == null) {
            return;
        }
        DurationCacheManager.getInstance(context).release();
    }

    @Override
    public DurationCache load(String url) {
        return super.load(url);
    }

    @Override
    public DurationCache placeholder(Long placeHolder) {
        return super.placeholder(placeHolder);
    }

    @Override
    public DurationCache error(Long error) {
        return super.error(error);
    }

    @Override
    public void into(final View view) {
        if (isFinishing() || view == null) {
            return;
        }
        if (TextUtils.isEmpty(mKey)) {
            // Just error
            if (view instanceof IDuration) {
                ((IDuration) view).setDuration(mError != null ? mError : mPlaceHolder);
            } else if (view instanceof TextView) {
                long time = mError != null ? mError : mPlaceHolder != null ? mPlaceHolder : 0;
                ((TextView) view).setText(Util.formatTime(time));
            }
            return;
        }
        setTarget(view);
        Object tag = view.getTag(getTag());
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, mKey)) {
            // Not refresh
            return;
        }
        view.setTag(getTag(), mKey);
        DurationCacheManager.getInstance(getContext()).load(getContext(), mKey,
                new CacheListener<Long>() {
                    @Override
                    public void onLoading() {
                        if (isFinished()) {
                            return;
                        }
                        if (mPlaceHolder == null) {
                            return;
                        }
                        setTarget(mPlaceHolder);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        if (isFinished()) {
                            return;
                        }
                        setTarget(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isFinished()) {
                            return;
                        }
                        if (mError == null) {
                            return;
                        }
                        setTarget(mError);
                    }

                    private void setTarget(Long value) {
                        if (getTarget() instanceof IDuration) {
                            ((IDuration) getTarget()).setDuration(value);
                        } else if (getTarget() instanceof TextView) {
                            long time = value != null ? value : 0;
                            ((TextView) getTarget()).setText(Util.formatTime(time));
                        }
                    }

                    private boolean isFinished() {
                        if (isFinishing() || getTarget() == null) {
                            return true;
                        }
                        Object tag = getTarget().getTag(getTag());
                        return tag == null || !(tag instanceof String) || !TextUtils.equals((String) tag, mKey);
                    }
                });
    }

    @Override
    public void listener(CacheListener<Long> l) {
        if (isFinishing()) {
            return;
        }
        if (TextUtils.isEmpty(mKey)) {
            // Just error
            if (l != null) {
                l.onError(new CacheException("Url must not be empty!"));
            }
            return;
        }
        DurationCacheManager.getInstance(getContext()).load(getContext(), mKey, l);
    }
}
