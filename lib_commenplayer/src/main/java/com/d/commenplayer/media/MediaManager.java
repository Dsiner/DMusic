package com.d.commenplayer.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.MediaController;

import com.d.commenplayer.listener.IPlayerListener;
import com.d.commenplayer.util.Factory;
import com.d.commenplayer.util.FileMediaDataSource;
import com.d.commenplayer.util.MLog;
import com.d.commenplayer.util.Settings;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class MediaManager implements MediaController.MediaPlayerControl, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnInfoListener {
    // all possible internal states
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;

    private static MediaManager mManager;
    private Settings settings;
    private Handler handler;
    private IMediaPlayer mediaPlayer;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    public int currentState = STATE_IDLE;
    public int targetState = STATE_IDLE;
    public int seekWhenPrepared;  // recording the seek position while preparing
    private int currentBufferPercentage;
    private IPlayerListener listener;

    public void setListener(IPlayerListener listener) {
        this.listener = listener;
    }

    public static MediaManager instance(Context context) {
        if (mManager == null) {
            synchronized (MediaManager.class) {
                mManager = new MediaManager(context.getApplicationContext());
            }
        }
        return mManager;
    }

    private MediaManager(Context context) {
        settings = new Settings(context.getApplicationContext());
        handler = new Handler(Looper.getMainLooper());
    }

    public IMediaPlayer prepare(Context context, final Uri uri, final Map<String, String> heads, boolean looping) {
        if (context == null || uri == null) {
            return null;
        }
        currentState = STATE_PREPARING;
        currentBufferPercentage = 0;
        seekWhenPrepared = 0;
        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(context, false);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.AUDIOFOCUS_GAIN / AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        try {
            mediaPlayer = Factory.createPlayer(context, settings.getPlayer());
            if (mediaPlayer == null) {
                return null;
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String scheme = uri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && settings.getUsingMediaDataSource()
                    && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(uri.toString()));
                mediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mediaPlayer.setDataSource(context, uri, heads);
            } else {
                mediaPlayer.setDataSource(uri.toString());
            }
            mediaPlayer.setLooping(looping);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.prepareAsync();
            return mediaPlayer;
        } catch (Exception e) {
            MLog.w("Unable to open content: " + uri + e);
            onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            e.printStackTrace();
            return null;
        }
    }

    public void release(Context context, boolean clearTargetState) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentState = STATE_IDLE;
        if (clearTargetState) {
            targetState = STATE_IDLE;
        }
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    @Override
    public void onPrepared(final IMediaPlayer mp) {
        currentState = STATE_PREPARED;
        // mSeekWhenPrepared may be changed after seekTo() call
        if (seekWhenPrepared != 0) {
            seekTo(seekWhenPrepared);
        }
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onPrepared(mp);
                }
            });
        }
    }

    @Override
    public void onCompletion(final IMediaPlayer mp) {
        currentState = STATE_PLAYBACK_COMPLETED;
        targetState = STATE_PLAYBACK_COMPLETED;
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onCompletion(mp);
                }
            });
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, final int percent) {
        currentBufferPercentage = percent;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
    }

    @Override
    public boolean onError(final IMediaPlayer mp, final int what, final int extra) {
        MLog.d("Error: " + what + "," + extra);
        if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
            MLog.d("onError:" + "Invalid progressive playback");
        } else {
            MLog.d("onError:" + "Unknown");
        }
        currentState = STATE_ERROR;
        targetState = STATE_ERROR;
        /* If an error handler has been supplied, use it and finish. */
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener == null) {
                        return;
                    }
                    listener.onError(mp, what, extra);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onInfo(final IMediaPlayer mp, final int what, final int extra) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener == null) {
                        return;
                    }
                    listener.onInfo(mp, what, extra);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onVideoSizeChanged(final IMediaPlayer mp, final int width, final int height, final int sarNum, final int sarDen) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener == null) {
                        return;
                    }
                    listener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
                }
            });
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mediaPlayer.start();
            currentState = STATE_PLAYING;
        }
        targetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            mediaPlayer.pause();
            currentState = STATE_PAUSED;
        }
        targetState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mediaPlayer.seekTo(msec);
            seekWhenPrepared = 0;
        } else {
            seekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return currentBufferPercentage;
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
        return (mediaPlayer != null
                && currentState != STATE_ERROR
                && currentState != STATE_IDLE
                && currentState != STATE_PREPARING);
    }
}
