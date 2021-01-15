package com.d.music.component.media.controler;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.d.lib.common.util.ToastUtils;
import com.d.music.R;
import com.d.music.component.cache.LinkCache;
import com.d.music.component.cache.SongCache;
import com.d.music.component.cache.listener.CacheListener;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Player
 * Created by D on 2018/8/22.
 */
public class Player {
    public static final int STATE_PLAY = 0;
    public static final int STATE_START = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_SEEKTO = 3;
    public static final int STATE_PREV = 4;
    public static final int STATE_NEXT = 5;
    public static final int STATE_STOP = 6;

    /**
     * Map used to store Player' tags.
     */
    private SparseArray<Object> mKeyedTags;
    private Context mContext;
    private List<Action> mActions = new ArrayList<>();
    private MediaPlayerManager mMediaPlayerManager;
    private Presenter mPresenter;
    private MediaPlayerManager.OnMediaPlayerListener mOnMediaPlayerListener;

    public Player(Context context) {
        this.mContext = context.getApplicationContext();
        this.mMediaPlayerManager = MediaPlayerManager.getInstance();
        this.mPresenter = new Presenter(context.getApplicationContext(), this);
    }

    public Object getTag(int key) {
        if (mKeyedTags != null) return mKeyedTags.get(key);
        return null;
    }

    public void setTag(int key, final Object tag) {
        // If the package id is 0x00 or 0x01, it's either an undefined package
        // or a framework id
        if ((key >>> 24) < 2) {
            throw new IllegalArgumentException("The key must be an application-specific "
                    + "resource id.");
        }

        setKeyedTag(key, tag);
    }

    private void setKeyedTag(int key, Object tag) {
        if (mKeyedTags == null) {
            mKeyedTags = new SparseArray<Object>(2);
        }

        mKeyedTags.put(key, tag);
    }

    public boolean isPlaying() {
        return mMediaPlayerManager.isPlaying();
    }

    public MediaPlayerManager getMediaManager() {
        return mMediaPlayerManager;
    }

    @UiThread
    public void excute(final Action action) {
        push(action);
        if (mActions.size() > 1) {
            return;
        }
        switch (action.action) {
            case STATE_PLAY:
                play(action.model, true);
                break;
            case STATE_START:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.start();
                break;
            case STATE_PAUSE:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.pause();
                break;
            case STATE_SEEKTO:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.seekTo(action.msec);
                break;
            case STATE_PREV:
                play(action.model, false);
                break;
            case STATE_NEXT:
                play(action.model, true);
                break;
            case STATE_STOP:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.stop();
                break;
        }
    }

    @UiThread
    private void play(final MusicModel model, final boolean next) {
        mMediaPlayerManager.reset();
        if (model.type == MusicModel.TYPE_LOCAL) {
            playLocal(model.songUrl, next);
        } else {
            mPresenter.playLink(model, next);
        }
    }

    @UiThread
    public void playLocal(final String url, final boolean next) {
        mMediaPlayerManager.play(url, new MediaPlayerManager.OnMediaPlayerListener() {
            @Override
            public void onLoading(MediaPlayer mp, String url) {
                if (mActions.size() > 1) {
                    return;
                }
                if (mOnMediaPlayerListener != null) {
                    mOnMediaPlayerListener.onLoading(mMediaPlayerManager.getMediaPlayer(), url);
                }
            }

            @Override
            public void onPrepared(MediaPlayer mp, String url) {
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.start();
                if (mOnMediaPlayerListener != null) {
                    mOnMediaPlayerListener.onPrepared(mp, url);
                }
            }

            @Override
            public void onError(MediaPlayer mp, String url) {
                if (pop()) {
                    return;
                }
                if (mOnMediaPlayerListener != null) {
                    mOnMediaPlayerListener.onError(mp, url);
                }
            }

            @Override
            public void onCompletion(MediaPlayer mp, String url) {
                if (mOnMediaPlayerListener != null) {
                    mOnMediaPlayerListener.onCompletion(mp, url);
                }
            }

            @Override
            public void onCancel(MediaPlayer mp) {
                if (mOnMediaPlayerListener != null) {
                    mOnMediaPlayerListener.onCancel(mp);
                }
            }
        });
    }

    @UiThread
    private void push(Action action) {
        Action ac = mActions.size() > 1 ? mActions.get(0) : null;
        mActions.clear();
        if (ac != null) {
            mActions.add(ac);
        }
        mActions.add(action);
    }

    @UiThread
    public boolean pop() {
        Action ac = mActions.size() > 1 ? mActions.get(mActions.size() - 1) : null;
        mActions.clear();
        if (ac != null) {
            mActions.add(ac);
            excute(ac);
            return true;
        }
        return false;
    }

    public void setOnMediaPlayerListener(MediaPlayerManager.OnMediaPlayerListener l) {
        mOnMediaPlayerListener = l;
    }

    public static class Action {
        public int action;
        public MusicModel model;
        public int msec;

        public Action(int action) {
            this.action = action;
        }

        public Action(int action, MusicModel model) {
            this.action = action;
            this.model = model;
        }

        public Action(int action, int msec) {
            this.action = action;
            this.msec = msec;
        }
    }

    public static class Presenter {
        private Context mContext;
        private WeakReference<Player> mViewRef;

        Presenter(Context context, Player player) {
            this.mContext = context.getApplicationContext();
            this.mViewRef = new WeakReference<>(player);
        }

        @UiThread
        @Nullable
        public Player getView() {
            return mViewRef == null ? null : mViewRef.get();
        }

        private void playLink(@NonNull final MusicModel model, final boolean next) {
            if (model.type == MusicModel.TYPE_BAIDU) {
                playLinkBaiduImp(model, next);
            }
        }

        private void playLinkBaiduImp(@NonNull final MusicModel model, final boolean next) {
            if (getView() == null) {
                return;
            }
            SongCache.with(mContext).load(model).into(getView());
            LinkCache.with(mContext).load(model).listener(getView(), new CacheListener<String>() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onSuccess(String result) {
                    if (getView() == null) {
                        return;
                    }
                    getView().playLocal(result, next);
                }

                @Override
                public void onError(Throwable e) {
                    if (getView() == null) {
                        return;
                    }
                    if (getView().pop()) {
                        return;
                    }
                    if (getView().mOnMediaPlayerListener != null) {
                        getView().mOnMediaPlayerListener.onError(getView().mMediaPlayerManager.getMediaPlayer(), "");
                    }
                    ToastUtils.toast(mContext, mContext.getResources().getString(R.string.lib_pub_net_error));
                }
            });
        }
    }
}
