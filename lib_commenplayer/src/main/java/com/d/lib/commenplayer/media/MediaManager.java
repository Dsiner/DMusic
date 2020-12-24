package com.d.lib.commenplayer.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.MediaController;

import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.util.Factory;
import com.d.lib.commenplayer.util.FileMediaDataSource;
import com.d.lib.commenplayer.util.Settings;
import com.d.lib.commenplayer.util.ULog;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class MediaManager implements MediaController.MediaPlayerControl,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnInfoListener {

    // All possible internal states
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    private static MediaManager INSTANCE;
    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    public int mCurrentState = STATE_IDLE;
    public int mTargetState = STATE_IDLE;
    public int mSeekWhenPrepared;  // Recording the seek position while preparing
    private Settings mSettings;
    private Handler mHandler;
    private IMediaPlayer mMediaPlayer;
    private int mCurrentBufferPercentage;
    private IPlayerListener mPlayerListener;

    private MediaManager(Context context) {
        mSettings = new Settings(context.getApplicationContext());
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static MediaManager instance(Context context) {
        if (INSTANCE == null) {
            synchronized (MediaManager.class) {
                INSTANCE = new MediaManager(context.getApplicationContext());
            }
        }
        return INSTANCE;
    }

    public void setListener(IPlayerListener listener) {
        this.mPlayerListener = listener;
    }

    public IMediaPlayer prepare(final Context context,
                                final Uri uri,
                                final Map<String, String> heads,
                                final boolean looping) {
        if (context == null || uri == null) {
            return null;
        }
        mCurrentState = STATE_PREPARING;
        mCurrentBufferPercentage = 0;
        mSeekWhenPrepared = 0;
        // We shouldn't clear the target state, because somebody might have
        // called start() previously
        release(context, false);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // AudioManager.AUDIOFOCUS_GAIN / AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        try {
            mMediaPlayer = Factory.createPlayer(context, mSettings.getPlayer());
            if (mMediaPlayer == null) {
                return null;
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String scheme = uri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && mSettings.getUsingMediaDataSource()
                    && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(uri.toString()));
                mMediaPlayer.setDataSource(dataSource);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(context, uri, heads);

            } else {
                mMediaPlayer.setDataSource(uri.toString());
            }
            mMediaPlayer.setLooping(looping);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.prepareAsync();
            return mMediaPlayer;
        } catch (Exception e) {
            ULog.w("Unable to open content: " + uri + e);
            onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            e.printStackTrace();
            return null;
        }
    }

    public void release(Context context, boolean clearTargetState) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentState = STATE_IDLE;
        if (clearTargetState) {
            mTargetState = STATE_IDLE;
        }
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    @Override
    public void onPrepared(final IMediaPlayer mp) {
        mCurrentState = STATE_PREPARED;
        // mSeekWhenPrepared may be changed after seekTo() call
        if (mSeekWhenPrepared != 0) {
            seekTo(mSeekWhenPrepared);
        }
        if (mPlayerListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerListener != null)
                        mPlayerListener.onPrepared(mp);
                }
            });
        }
    }

    @Override
    public void onCompletion(final IMediaPlayer mp) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mPlayerListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerListener != null)
                        mPlayerListener.onCompletion(mp);
                }
            });
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, final int percent) {
        mCurrentBufferPercentage = percent;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
    }

    @Override
    public boolean onError(final IMediaPlayer mp, final int what, final int extra) {
        ULog.d("Error: " + what + "," + extra);
        if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
            ULog.d("onError:" + "Invalid progressive playback");
        } else {
            ULog.d("onError:" + "Unknown");
        }
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        /* If an error handler has been supplied, use it and finish. */
        if (mPlayerListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerListener == null) {
                        return;
                    }
                    mPlayerListener.onError(mp, what, extra);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onInfo(final IMediaPlayer mp, final int what, final int extra) {
        if (mPlayerListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerListener == null) {
                        return;
                    }
                    mPlayerListener.onInfo(mp, what, extra);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(final IMediaPlayer mp, final int width, final int height, final int sarNum, final int sarDen) {
        if (mPlayerListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mPlayerListener == null) {
                        return;
                    }
                    mPlayerListener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
                }
            });
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null
                && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING);
    }
}
