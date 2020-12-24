package com.d.lib.commenplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.d.lib.commenplayer.listener.IMediaPlayerControl;
import com.d.lib.commenplayer.listener.IPlayerListener;
import com.d.lib.commenplayer.listener.IRenderView;
import com.d.lib.commenplayer.listener.OnAnimatorUpdateListener;
import com.d.lib.commenplayer.listener.OnNetworkListener;
import com.d.lib.commenplayer.media.IjkVideoView;
import com.d.lib.commenplayer.media.TextureRenderView;
import com.d.lib.commenplayer.util.Util;
import com.d.lib.commenplayer.widget.ControlLayout;
import com.d.lib.commenplayer.widget.TouchLayout;

import java.lang.ref.WeakReference;

/**
 * CommenPlayer
 * Created by D on 2017/5/27.
 */
public class CommenPlayer extends FrameLayout implements IMediaPlayerControl {
    private final int TASK_LOOP_TIME = 1000;
    private Activity activity;
    private IjkVideoView ijkplayer;
    private TouchLayout tl_touch;
    private ControlLayout cl_control;
    private Handler handler = new Handler();
    private ProgressTask progressTask;
    private boolean progressTaskRunning;
    private boolean progressLock; // Progress lock
    private boolean live;
    private String url;
    private boolean isPortrait = true; // true: vertical; false: horizontal
    private OnAnimatorUpdateListener animatorUpdateListener; // Stick floating layer, animation
    private OnNetworkListener netListener;

    public CommenPlayer(Context context) {
        super(context);
        init(context);
    }

    public CommenPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommenPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void reStartProgressTask() {
        stopProgressTask();
        progressTaskRunning = true;
        handler.postDelayed(progressTask, 300);
    }

    public void stopProgressTask() {
        progressTaskRunning = false;
        handler.removeCallbacks(progressTask);
    }

    private void init(final Context context) {
        activity = (Activity) context;
        progressTask = new ProgressTask(this);
        View root = LayoutInflater.from(context).inflate(R.layout.lib_player_layout_player, this);
        initView(root);
    }

    protected void initView(View root) {
        ijkplayer = (IjkVideoView) root.findViewById(R.id.ijkplayer);
        tl_touch = (TouchLayout) root.findViewById(R.id.tl_touch);
        cl_control = (ControlLayout) root.findViewById(R.id.cl_control);
        tl_touch.setIMediaPlayerControl(this);
        cl_control.setIMediaPlayerControl(this);
    }

    public ControlLayout getControl() {
        return cl_control;
    }

    public TouchLayout getTouch() {
        return tl_touch;
    }

    @Override
    public boolean isLive() {
        return live;
    }

    @Override
    public void setLive(boolean live) {
        this.live = live;
        ijkplayer.setLive(live);
    }

    @Override
    public void setScaleType(int scaleType) {
        ijkplayer.setScaleType(scaleType);
    }

    @Override
    public void play(String url) {
        play(url, 0);
    }

    public void play(String url, int pos) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        this.url = url;
        ijkplayer.play(url, pos);
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        ijkplayer.setVideoPath(url);
    }

    @Override
    public void ignoreMobileNet() {
        if (netListener != null) {
            netListener.onIgnoreMobileData();
        }
    }

    @Override
    public void setPlayerVisibility(int visibility) {
        ijkplayer.setVisibility(visibility);
    }

    @Override
    public void toggleStick() {
        cl_control.toggleStick();
    }

    @Override
    public void lockProgress(boolean lock) {
        progressLock = lock;
        if (progressLock) {
            stopProgressTask();
        } else {
            reStartProgressTask();
        }
    }

    @Override
    public void progressTo(int position, int bufferPercentage) {
        cl_control.setProgress(position, getDuration(), bufferPercentage);
    }

    @Override
    public void seekTo(int pos) {
        ijkplayer.seekTo(Math.max(pos, 0));
    }

    @Override
    public boolean isPlaying() {
        return ijkplayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return ijkplayer.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return !live;
    }

    @Override
    public boolean canSeekBackward() {
        return !live;
    }

    @Override
    public boolean canSeekForward() {
        return !live;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void start() {
        ijkplayer.start();
    }

    @Override
    public void pause() {
        ijkplayer.pause();
    }

    @Override
    public int getDuration() {
        return ijkplayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return ijkplayer.getCurrentPosition();
    }

    @Override
    public int toggleAspectRatio() {
        return ijkplayer.toggleAspectRatio();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isPortrait = newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE;
        if (isPortrait) {
            Util.showSystemUIForce(activity, cl_control, tl_touch);
        } else {
            Util.hideSystemUI(activity, cl_control, tl_touch);
            Util.showSystemUI(activity, cl_control, tl_touch);
        }
        tl_touch.setVisibility(isPortrait ? GONE : VISIBLE);
        cl_control.onConfigurationChanged(isPortrait);
        setScaleType(IRenderView.AR_ASPECT_FIT_PARENT);
    }

    @Override
    public void toggleSystemUI(boolean show) {
        if (show) {
            Util.showSystemUI(activity, cl_control, tl_touch);
        } else {
            Util.hideSystemUI(activity, cl_control, tl_touch);
        }
    }

    @Override
    public void onAnimationUpdate(float factor) {
        if (animatorUpdateListener != null) {
            animatorUpdateListener.onAnimationUpdate(factor);
        }
    }

    @Override
    public void toggleOrientation() {
        activity.setRequestedOrientation(isPortrait ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onResume() {
        ijkplayer.onResume();
        reStartProgressTask();
    }

    @Override
    public void onPause() {
        stopProgressTask();
        ijkplayer.onPause();
    }

    @Override
    public boolean onBackPress() {
        if (!isPortrait) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        ijkplayer.onDestroy();
    }

    public CommenPlayer setOnPlayerListener(IPlayerListener listener) {
        this.ijkplayer.setOnPlayerListener(listener);
        return this;
    }

    public CommenPlayer setOnAnimatorUpdateListener(OnAnimatorUpdateListener listener) {
        this.animatorUpdateListener = listener;
        return this;
    }

    public CommenPlayer setOnNetListener(OnNetworkListener listener) {
        this.netListener = listener;
        return this;
    }

    /**
     * Get a screenshot of the video
     */
    public Bitmap getSnapShot() {
        IRenderView renderView = ijkplayer.getRenderView();
        if (renderView == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && renderView instanceof TextureRenderView) {
            return ((TextureRenderView) renderView).getBitmap();
        }
        return Util.getFrameAtTime(activity, url, getCurrentPosition());
    }

    private static class ProgressTask implements Runnable {
        private final WeakReference<CommenPlayer> reference;

        ProgressTask(CommenPlayer layout) {
            this.reference = new WeakReference<>(layout);
        }

        @Override
        public void run() {
            CommenPlayer layout = reference.get();
            if (layout == null || layout.getContext() == null || !layout.progressTaskRunning || layout.live) {
                return;
            }
            if (!layout.progressLock) {
                layout.progressTo(layout.getCurrentPosition(), layout.getBufferPercentage());
            }
            layout.handler.postDelayed(layout.progressTask, layout.TASK_LOOP_TIME);
        }
    }
}
