package com.d.music.component.cache.base;

import android.app.Activity;
import android.content.Context;

import com.d.music.component.cache.listener.CacheListener;

import java.lang.ref.WeakReference;

/**
 * Created by D on 2018/6/8.
 */
public abstract class AbstractCache<R extends AbstractCache, Target, Key, Placeholder, Result> {
    protected WeakReference<Context> mContext;
    protected WeakReference<Target> mTarget;
    protected Key mKey;
    protected Placeholder mPlaceHolder;
    protected Placeholder mError;

    protected AbstractCache(Context context) {
        this.mContext = new WeakReference<>(context instanceof Activity ? context : context.getApplicationContext());
    }

    public R load(Key key) {
        this.mKey = key;
        return (R) this;
    }

    public R placeholder(Placeholder placeHolder) {
        this.mPlaceHolder = placeHolder;
        return (R) this;
    }

    public R error(Placeholder error) {
        this.mError = error;
        return (R) this;
    }

    protected Target getTarget() {
        return mTarget != null ? mTarget.get() : null;
    }

    protected void setTarget(Target target) {
        mTarget = new WeakReference<>(target);
    }

    protected Context getContext() {
        return mContext != null ? mContext.get() : null;
    }

    protected boolean isFinishing() {
        return mContext == null || mContext.get() == null
                || mContext.get() instanceof Activity && ((Activity) mContext.get()).isFinishing();
    }

    public abstract void into(final Target target);

    public abstract void listener(CacheListener<Result> l);
}
