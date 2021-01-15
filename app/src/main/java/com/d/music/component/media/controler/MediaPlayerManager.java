package com.d.music.component.media.controler;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.lang.ref.WeakReference;

/**
 * MediaPlayerManager
 * Created by D on 2017/9/11.
 */
public class MediaPlayerManager {
    private volatile static MediaPlayerManager INSTANCE;

    private MediaPlayer mMediaPlayer;
    private String mSource;
    private boolean mIsPrepared;

    private MediaPlayerManager() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);
    }

    public static MediaPlayerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (MediaPlayerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MediaPlayerManager();
                }
            }
        }
        return INSTANCE;
    }

    @UiThread
    public void play(final String url, final OnMediaPlayerListener l) {
        play(this, url, l);
    }

    @UiThread
    public void play(final Activity activity, final String url, final OnMediaPlayerListener l) {
        play(activity, url, l);
    }

    @UiThread
    public void play(final Object obj, final String url, final OnMediaPlayerListener listener) {
        if (TextUtils.isEmpty(url)) {
            if (listener != null) {
                listener.onError(mMediaPlayer, url);
            }
            return;
        }
        mSource = url;
        if (listener != null) {
            listener.onLoading(mMediaPlayer, url);
        }
        final WeakReference<Object> weakRef = new WeakReference<>(obj);
        try {
            mIsPrepared = false;
            mMediaPlayer.reset(); // 重置
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mIsPrepared = true;
                    if (listener != null) {
                        listener.onPrepared(mMediaPlayer, url);
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mIsPrepared = false;
                    if (listener != null) {
                        listener.onError(mMediaPlayer, url);
                    }
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (listener != null) {
                        listener.onCompletion(mMediaPlayer, url);
                    }
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
            mIsPrepared = false;
            mMediaPlayer.reset(); // 重置
            if (listener != null) {
                listener.onError(mMediaPlayer, url);
            }
        }
    }

    private boolean isDestroy(String url, WeakReference<Object> weakRef) {
        return !TextUtils.equals(url, mSource) || mMediaPlayer == null
                || weakRef == null || weakRef.get() == null
                || weakRef.get() instanceof Activity && ((Activity) weakRef.get()).isFinishing();
    }

    public String getUrl() {
        return mSource;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo(int msec) {
        try {
            if (!isPrepared()) {
                return;
            }
            int duration = mMediaPlayer.getDuration(); // 毫秒
            if (msec >= 0 && msec <= duration) {
                mMediaPlayer.seekTo(msec);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean isPrepared() {
        return mIsPrepared;
    }

    public void start() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mMediaPlayer.stop();
    }


    @NonNull
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public int getCurrentPosition() {
        try {
            if (!isPrepared()) {
                return 0;
            }
            int position = mMediaPlayer.getCurrentPosition();
            position = Math.max(position, 0);
            return position;
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getDuration() {
        try {
            if (!isPrepared()) {
                return 0;
            }
            int duration = mMediaPlayer.getDuration();
            duration = Math.max(duration, 0);
            return duration;
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reset() {
        mIsPrepared = false;
        mSource = "";
        mMediaPlayer.reset(); // 重置
    }

    private void release() {
        mIsPrepared = false;
        mSource = "";
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public interface OnMediaPlayerListener {
        void onLoading(MediaPlayer mp, String url);

        void onPrepared(MediaPlayer mp, String url);

        void onError(MediaPlayer mp, String url);

        void onCompletion(MediaPlayer mp, String url);

        void onCancel(MediaPlayer mp);
    }
}
