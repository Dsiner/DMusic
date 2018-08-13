/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.d.commenplayer.media;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.d.commenplayer.listener.IPlayerListener;
import com.d.commenplayer.listener.IRenderView;
import com.d.commenplayer.services.MediaPlayerService;
import com.d.commenplayer.util.Factory;
import com.d.commenplayer.util.MLog;
import com.d.commenplayer.util.Settings;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl, IPlayerListener {
    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};

    private Activity mActivity;
    private Uri uri;
    private Map<String, String> headers;
    private IMediaPlayer mediaPlayer = null;

    private IRenderView renderView;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private int videoSarNum;
    private int videoSarDen;
    private int surfaceWidth;
    private int surfaceHeight;
    private int videoRotationDegree;

    //-------------------------
    // Extend: Aspect Ratio
    //-------------------------
    private int currentAspectRatioIndex = 0;
    private int currentAspectRatio = s_allAspectRatio[0];

    private boolean isLive = false;// is Live mode
    private boolean isPause = false;
    private int pausePos;
    private IPlayerListener listener;
    private boolean playerSupport;

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
            playerSupport = true;
        } catch (Throwable e) {
            MLog.e("GiraffePlayer loadLibraries error" + e);
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
        return renderView;
    }

    private void addRenderView() {
        IRenderView renderView = Factory.initRenders(mActivity);
        if (this.renderView != null) {
            if (mediaPlayer != null) {
                mediaPlayer.setDisplay(null);
            }
            removeAllViews();
            this.renderView.removeRenderCallback(mSHCallback);
            this.renderView = null;
        }
        if (renderView == null) {
            return;
        }

        this.renderView = renderView;
        if (videoWidth > 0 && videoHeight > 0) {
            this.renderView.setVideoSize(videoWidth, videoHeight);
        }
        if (videoSarNum > 0 && videoSarDen > 0) {
            this.renderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
        }
        this.renderView.setAspectRatio(currentAspectRatio);
        this.renderView.setVideoRotation(videoRotationDegree);
        this.renderView.addRenderCallback(mSHCallback);
        View view = this.renderView.getView();
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
        this.uri = uri;
        this.headers = headers;
        prepare();
    }

    private void prepare() {
        if (uri == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        mediaPlayer = getManager().prepare(mActivity.getApplicationContext(), uri, headers, false);
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

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != renderView) {
                MLog.e("onSurfaceChanged: unmatched render callback");
                return;
            }
            surfaceWidth = w;
            surfaceHeight = h;
            boolean isValidState = (getManager().targetState == MediaManager.STATE_PLAYING);
            boolean hasValidSize = !renderView.shouldWaitForResize() || (videoWidth == w && videoHeight == h);
            if (mediaPlayer != null && isValidState && hasValidSize) {
                if (getManager().seekWhenPrepared != 0) {
                    seekTo(getManager().seekWhenPrepared);
                }
                start();
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != renderView) {
                MLog.e("onSurfaceCreated: unmatched render callback");
                return;
            }
            if (mediaPlayer != null) {
                bindSurfaceHolder(mediaPlayer, holder);
            } else {
                prepare();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != renderView) {
                MLog.e("onSurfaceDestroyed: unmatched render callback");
                return;
            }
            // after we return from this we can't use the surface any more
            if (mediaPlayer != null) {
                mediaPlayer.setDisplay(null);
            }
        }
    };

    /**
     * release the media player in any state
     */
    public void release(boolean clearTargetState) {
        getManager().release(mActivity.getApplicationContext(), clearTargetState);
        mediaPlayer = null;
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
        if (listener != null) {
            listener.onLoading();
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (listener != null) {
            listener.onCompletion(mp);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        setArgs(mp);
        if (videoWidth != 0 && videoHeight != 0) {
            if (renderView != null) {
                renderView.setVideoSize(videoWidth, videoHeight);
                renderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
                if (!renderView.shouldWaitForResize() || surfaceWidth == videoWidth && surfaceHeight == videoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (getManager().targetState == MediaManager.STATE_PLAYING) {
                        start();
                    } else if (!isPlaying() && (getManager().seekWhenPrepared != 0
                            || getCurrentPosition() > 0)) {
                        // Show the media controls when we're paused into a video and make 'em stick.
                    }
                }
            }
        } else {
            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            if (getManager().targetState == MediaManager.STATE_PLAYING) {
                start();
            }
        }

        if (listener != null) {
            listener.onPrepared(mp);
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }
        return listener != null && listener.onError(mp, what, extra);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        //当what为MEDIA_INFO_VIDEO_RENDERING_START时播放第一帧画面了
        if (mActivity == null || mActivity.isFinishing()) {
            return false;
        }
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                videoRotationDegree = extra;
                MLog.d("MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + extra);
                if (renderView != null) {
                    renderView.setVideoRotation(extra);
                }
                break;
            //case IMediaPlayer.MEDIA_INFO_...
        }
        return listener == null || listener.onInfo(mp, what, extra);
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        setArgs(mp);
        if (videoWidth != 0 && videoHeight != 0) {
            if (renderView != null) {
                renderView.setVideoSize(videoWidth, videoHeight);
                renderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            }
            requestLayout();
        }
        if (listener != null) {
            listener.onVideoSizeChanged(mp, width, height, sarNum, sarDen);
        }
    }

    private void setArgs(IMediaPlayer mp) {
        videoWidth = mp.getVideoWidth();
        videoHeight = mp.getVideoHeight();
        videoSarNum = mp.getVideoSarNum();
        videoSarDen = mp.getVideoSarDen();
    }

    public void setLive(boolean live) {
        isLive = live;
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
        if (isPause) {
            isPause = false;
            prepare();
            seekTo(isLive ? 0 : pausePos);
            start();
            onLoading();
        }
    }

    public void onPause() {
        isPause = true;
        if (isLive) {
            release(false);
        } else {
            pausePos = getCurrentPosition();
            getManager().pause();
        }
    }

    public void onDestroy() {
        release(true);
    }

    public void setScaleType(int scaleType) {
        if (renderView != null) {
            for (int index : s_allAspectRatio) {
                if (index == scaleType) {
                    currentAspectRatioIndex = index;
                }
            }
            renderView.setAspectRatio(scaleType);
        }
    }

    public int toggleAspectRatio() {
        currentAspectRatioIndex++;
        currentAspectRatioIndex %= s_allAspectRatio.length;
        currentAspectRatio = s_allAspectRatio[currentAspectRatioIndex];
        if (renderView != null) {
            renderView.setAspectRatio(currentAspectRatio);
        }
        return currentAspectRatio;
    }

    public void setOnPlayerListener(IPlayerListener iPlayerListener) {
        listener = iPlayerListener;
    }
}
