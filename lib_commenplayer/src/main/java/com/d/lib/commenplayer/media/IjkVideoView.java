package com.d.lib.commenplayer.media;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.listener.IRenderView;
import com.d.lib.commenplayer.services.MediaPlayerService;
import com.d.lib.commenplayer.util.Factory;
import com.d.lib.commenplayer.util.Settings;
import com.d.lib.commenplayer.util.ULog;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoView extends FrameLayout
        implements MediaController.MediaPlayerControl, IPlayerListener {

    private static final int[] ALL_ASPECT_RATIO = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};

    private Activity mActivity;
    private Uri mUri;
    private Map<String, String> mHeaders;
    private IMediaPlayer mMediaPlayer = null;

    private IRenderView mRenderView;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;

    //-------------------------
    // Extend: Aspect Ratio
    //-------------------------
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = ALL_ASPECT_RATIO[0];
    private boolean mIsLive = false;// is Live mode
    private boolean mIsPause = false;
    private int mPausePosition;
    private IPlayerListener mPlayerListener;
    private boolean mIsPlayerSupport;

    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                ULog.e("onSurfaceChanged: unmatched render callback");
                return;
            }
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (getManager().mTargetState == MediaManager.STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (getManager().mSeekWhenPrepared != 0) {
                    seekTo(getManager().mSeekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                ULog.e("onSurfaceCreated: unmatched render callback");
                return;
            }
            if (mMediaPlayer != null) {
                bindSurfaceHolder(mMediaPlayer, holder);
            } else {
                prepare();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                ULog.e("onSurfaceDestroyed: unmatched render callback");
                return;
            }
            // after we return from this we can't use the surface any more
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
        }
    };


    public IjkVideoView(Context context) {
        super(context);
        init(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mActivity = (Activity) context;
        // init player
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            mIsPlayerSupport = true;
        } catch (Throwable e) {
            ULog.e("GiraffePlayer loadLibraries error" + e);
        }

        initBackground();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    private void initBackground() {
        boolean enable = new Settings(mActivity.getApplicationContext()).getEnableBackgroundPlay();
        if (enable) {
            MediaPlayerService.intentToStart(getContext());
            MediaPlayerService.getMediaManager(mActivity);
        } else {
            getManager();
        }
    }

    private MediaManager getManager() {
        return MediaManager.instance(mActivity);
    }

    public IRenderView getRenderView() {
        return mRenderView;
    }

    private void addRenderView() {
        IRenderView renderView = Factory.initRenders(mActivity);
        if (this.mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }
            removeAllViews();
            this.mRenderView.removeRenderCallback(mSHCallback);
            this.mRenderView = null;
        }
        if (renderView == null) {
            return;
        }

        this.mRenderView = renderView;
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            this.mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
        }
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            this.mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
        }
        this.mRenderView.setAspectRatio(mCurrentAspectRatio);
        this.mRenderView.setVideoRotation(mVideoRotationDegree);
        this.mRenderView.addRenderCallback(mSHCallback);
        View view = this.mRenderView.getView();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        view.setLayoutParams(lp);
        addView(view);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        this.mUri = uri;
        this.mHeaders = headers;
        prepare();
    }

    private void prepare() {
        if (mUri == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        mMediaPlayer = getManager().prepare(mActivity.getApplicationContext(), mUri, mHeaders, false);
        getManager().setListener(this);
        addRenderView();
    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null) {
            return;
        }
        if (holder == null) {
            mp.setDisplay(null);
            return;
        }
        holder.bindToMediaPlayer(mp);
    }

    /**
     * release the media player in any state
     */
    public void release(boolean clearTargetState) {
        getManager().release(mActivity.getApplicationContext(), clearTargetState);
        mMediaPlayer = null;
    }

    @Override
    public void start() {
        getManager().start();
    }

    @Override
    public void pause() {
        getManager().pause();
    }

    @Override
    public int getDuration() {
        return getManager().getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return getManager().getCurrentPosition();
    }

    @Override
    public void seekTo(int msec) {
        getManager().seekTo(msec);
    }

    @Override
    public boolean isPlaying() {
        return getManager().isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return getManager().getBufferPercentage();
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

    @Override
    public void onLoading() {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (mPlayerListener != null) {
            mPlayerListener.onLoading();
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (mPlayerListener != null) {
            mPlayerListener.onCompletion(mp);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        setArgs(mp);
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (getManager().mTargetState == MediaManager.STATE_PLAYING) {
                        start();
                    } else if (!isPlaying() && (getManager().mSeekWhenPrepared != 0
                            || getCurrentPosition() > 0)) {
                        // Show the media controls when we're paused into a video and make 'em stick.
                    }
                }
            }
        } else {
            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (getManager().mTargetState == MediaManager.STATE_PLAYING) {
                start();
            }
        }

        if (mPlayerListener != null) {
            mPlayerListener.onPrepared(mp);
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }
        return mPlayerListener != null && mPlayerListener.onError(mp, what, extra);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        // 当what为MEDIA_INFO_VIDEO_RENDERING_START时播放第一帧画面了
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                mVideoRotationDegree = extra;
                ULog.d("MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + extra);
                if (mRenderView != null) {
                    mRenderView.setVideoRotation(extra);
                }
                break;
            // case IMediaPlayer.MEDIA_INFO_...
        }
        return mPlayerListener == null || mPlayerListener.onInfo(mp, what, extra);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        setArgs(mp);
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
            }
            requestLayout();
        }
        if (mPlayerListener != null) {
            mPlayerListener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
        }
    }

    private void setArgs(IMediaPlayer mp) {
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        mVideoSarNum = mp.getVideoSarNum();
        mVideoSarDen = mp.getVideoSarDen();
    }

    public void setLive(boolean live) {
        mIsLive = live;
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void play(String url, int pos) {
        setVideoPath(url);
        seekTo(pos);
        start();
        onLoading();
    }

    public void onResume() {
        if (mIsPause) {
            mIsPause = false;
            prepare();
            seekTo(mIsLive ? 0 : mPausePosition);
            start();
            onLoading();
        }
    }

    public void onPause() {
        mIsPause = true;
        if (mIsLive) {
            release(false);
        } else {
            mPausePosition = getCurrentPosition();
            getManager().pause();
        }
    }

    public void onDestroy() {
        release(true);
    }

    public void setScaleType(int scaleType) {
        if (mRenderView != null) {
            for (int index : ALL_ASPECT_RATIO) {
                if (index == scaleType) {
                    mCurrentAspectRatioIndex = index;
                }
            }
            mRenderView.setAspectRatio(scaleType);
        }
    }

    public int toggleAspectRatio() {
        mCurrentAspectRatioIndex++;
        mCurrentAspectRatioIndex %= ALL_ASPECT_RATIO.length;
        mCurrentAspectRatio = ALL_ASPECT_RATIO[mCurrentAspectRatioIndex];
        if (mRenderView != null) {
            mRenderView.setAspectRatio(mCurrentAspectRatio);
        }
        return mCurrentAspectRatio;
    }

    public void setOnPlayerListener(IPlayerListener iPlayerListener) {
        mPlayerListener = iPlayerListener;
    }
}
