package com.d.commenplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d.commenplayer.R;
import com.d.commenplayer.listener.IMediaPlayerControl;
import com.d.commenplayer.util.MUtil;

/**
 * 视频浮层-滑动控制
 * Created by D on 2017/8/8.
 */
public class TouchLayout extends FrameLayout implements View.OnTouchListener {
    private int TYPE_PROGRESS = 0;
    private int TYPE_BRIGHTNESS = 1;
    private int TYPE_VOLUME = 2;

    private Activity mActivity;
    private int screenWidth;
    private LinearLayout llytProgress;
    private TextView tvProgress;
    private ProgressBar prbProgress;
    private LinearLayout llytBrightness;
    private ProgressBar prbBrightness;
    private LinearLayout llytVolume;
    private ProgressBar prbVolume;
    private AudioManager audioManager;
    private int touchSlop;
    private boolean scrollValid;
    private boolean toVolume;
    private boolean toSeek;

    private int position;
    private int duration;
    private int newPosition = -1;
    private int maxVolume;
    private float brightness = -1;
    private int volume = -1;


    private GestureDetector gestureDetector;
    private IMediaPlayerControl listener;
    private OnGestureListener gestureListener;

    public TouchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TouchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mActivity = (Activity) context;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View root = LayoutInflater.from(context).inflate(R.layout.lib_player_layout_touch, this);
        initView(root);
        int[] size = MUtil.getScreenSize(mActivity);
        screenWidth = size[0];
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gestureDetector = new GestureDetector(context, new PlayerGestureListener());

        root.setClickable(true);
        root.setOnTouchListener(this);
    }

    private void initView(View root) {
        llytProgress = (LinearLayout) root.findViewById(R.id.llyt_player_adj_prg);
        llytBrightness = (LinearLayout) root.findViewById(R.id.llyt_player_adj_brightness);
        llytVolume = (LinearLayout) root.findViewById(R.id.llyt_player_adj_volume);
        tvProgress = (TextView) root.findViewById(R.id.tv_player_adj_prg);
        prbProgress = (ProgressBar) root.findViewById(R.id.prb_player_adj_prg);
        prbBrightness = (ProgressBar) root.findViewById(R.id.prb_player_adj_brightness);
        prbVolume = (ProgressBar) root.findViewById(R.id.prb_player_adj_volume);
    }

    private void show(int type) {
        llytProgress.setVisibility(type == TYPE_PROGRESS ? VISIBLE : GONE);
        llytBrightness.setVisibility(type == TYPE_BRIGHTNESS ? VISIBLE : GONE);
        llytVolume.setVisibility(type == TYPE_VOLUME ? VISIBLE : GONE);
    }

    private void onProgressSlide(float percent) {
        if (listener == null || listener.isLive()) {
            return;
        }
        show(TYPE_PROGRESS);
        newPosition = (int) (1f * position + 1f * duration * percent);
        newPosition = Math.min(newPosition, duration);
        newPosition = Math.max(newPosition, 0);
        prbProgress.setProgress((int) (newPosition * 1.0 / duration * 100));
        tvProgress.setText(MUtil.generateTime(newPosition));
        listener.progressTo(newPosition, 0);
    }

    /**
     * 滑动改变亮度
     */
    private void onBrightnessSlide(float percent) {
        show(TYPE_BRIGHTNESS);
        if (brightness < 0) {
            brightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.screenBrightness = brightness + percent;
        if (lp.screenBrightness > 1.0f) {
            lp.screenBrightness = 1.0f;
        } else if (lp.screenBrightness < 0.01f) {
            lp.screenBrightness = 0.01f;
        }
        prbBrightness.setProgress((int) (lp.screenBrightness * 100));
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * 滑动调节声音大小
     */
    private void onVolumeSlide(float percent) {
        show(TYPE_VOLUME);
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0) {
                volume = 0;
            }
        }
        int index = volume + (int) (maxVolume * percent);
        index = Math.min(index, maxVolume);
        index = Math.max(index, 0);
        //变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        //变更进度条
        prbVolume.setProgress((int) (index * 1.0 / maxVolume * 100));
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        show(GONE);
        volume = -1;
        brightness = -1f;
        if (scrollValid && toSeek && listener != null && !listener.isLive()) {
            listener.seekTo(newPosition);
            listener.lockProgress(false);
            scrollValid = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        //处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return false;
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float oldX = e1.getX(), oldY = e1.getY();
            float deltaX = oldX - e2.getX();
            float deltaY = oldY - e2.getY();
            if (!scrollValid && (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop)) {
                toSeek = Math.abs(deltaX) >= Math.abs(deltaY);
                toVolume = oldX > screenWidth * 0.5f;
                scrollValid = true;//滑动生效
                if (toSeek && listener != null) {
                    position = listener.getCurrentPosition();
                    duration = listener.getDuration();
                    listener.lockProgress(true);
                }
            }
            if (scrollValid) {
                if (toSeek) {
                    onProgressSlide(-deltaX / getWidth());
                } else {
                    float percent = deltaY / getHeight();
                    if (toVolume) {
                        onVolumeSlide(percent);
                    } else {
                        onBrightnessSlide(percent);
                    }
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            scrollValid = false;
            if (gestureListener != null) {
                gestureListener.onTouch();
            }
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (listener != null) {
                listener.toggleAspectRatio();
                return true;
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (listener != null) {
                listener.toggleStick();
                return true;
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public void setIMediaPlayerControl(IMediaPlayerControl listener) {
        this.listener = listener;
    }

    public interface OnGestureListener {
        void onTouch();
    }

    public void setOnGestureListener(OnGestureListener l) {
        this.gestureListener = l;
    }
}
