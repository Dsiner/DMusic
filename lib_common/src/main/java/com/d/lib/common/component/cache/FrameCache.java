package com.d.lib.common.component.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.common.R;
import com.d.lib.common.component.cache.base.AbstractCache;
import com.d.lib.common.component.cache.bean.FrameBean;
import com.d.lib.common.component.cache.exception.CacheException;
import com.d.lib.common.component.cache.listener.CacheListener;
import com.d.lib.common.component.cache.listener.IFrame;
import com.d.lib.common.component.cache.manager.FrameCacheManager;

/**
 * Cache - Get video first frame & duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class FrameCache extends AbstractCache<FrameCache, View, String, Drawable, FrameBean> {

    private FrameCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_pub_cache_tag_frame;
    }

    @UiThread
    public static FrameCache with(Context context) {
        return new FrameCache(context);
    }

    @Override
    public FrameCache load(String url) {
        return super.load(url);
    }

    public FrameCache placeholder(@DrawableRes int resId) {
        if (isFinishing()) {
            return this;
        }
        return placeholder(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override
    public FrameCache placeholder(Drawable placeHolder) {
        return super.placeholder(placeHolder);
    }

    public FrameCache error(@DrawableRes int resId) {
        if (isFinishing()) {
            return this;
        }
        return error(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override
    public FrameCache error(Drawable error) {
        return super.error(error);
    }

    @Override
    public void into(View view) {
        if (isFinishing() || view == null) {
            return;
        }
        if (TextUtils.isEmpty(mKey)) {
            // Just error
            if (view instanceof IFrame) {
                ((IFrame) view).setFrame(mError != null ? mError : mPlaceHolder, 0L);
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(mError != null ? mError : mPlaceHolder);
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
        FrameCacheManager.getIns(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), mKey, new CacheListener<FrameBean>() {
                    @Override
                    public void onLoading() {
                        if (isFinished()) {
                            return;
                        }
                        if (mPlaceHolder == null) {
                            return;
                        }
                        setTarget(mPlaceHolder, 0L);
                    }

                    @Override
                    public void onSuccess(FrameBean result) {
                        if (isFinished()) {
                            return;
                        }
                        setTarget(result.drawable, result.duration);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isFinished()) {
                            return;
                        }
                        if (mError == null) {
                            return;
                        }
                        setTarget(mError, 0L);
                    }

                    private void setTarget(Drawable drawable, Long duration) {
                        if (getTarget() instanceof IFrame) {
                            ((IFrame) getTarget()).setFrame(drawable, duration);
                        } else if (getTarget() instanceof ImageView) {
                            ((ImageView) getTarget()).setImageDrawable(drawable);
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
    public void listener(CacheListener<FrameBean> l) {
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
        FrameCacheManager.getIns(getContext()).load(getContext(), mKey, l);
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
        FrameCacheManager.getIns(context.getApplicationContext()).release();
    }
}
