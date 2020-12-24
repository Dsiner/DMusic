package com.d.lib.commenplayer.widget;

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

import com.d.lib.commenplayer.R;
import com.d.lib.commenplayer.listener.IMediaPlayerControl;
import com.d.lib.commenplayer.util.Util;

/**
 * Video Floating Layer - Sliding Control
 * Created by D on 2017/8/8.
 */
public class TouchLayout extends FrameLayout implements View.OnTouchListener {
    private int TYPE_PROGRESS = 0;
    private int TYPE_BRIGHTNESS = 1;
    private int TYPE_VOLUME = 2;

    private Activity mActivity;
    private int mScreenWidth;
    private LinearLayout ll_player_adj_prg;
    private TextView tv_player_adj_prg;
    private ProgressBar prb_player_adj_prg;
    private LinearLayout ll_player_adj_brightness;
    private ProgressBar prb_player_adj_brightness;
    private LinearLayout ll_player_adj_volume;
    private ProgressBar prb_player_adj_volume;
    private AudioManager mAudioManager;
    private int mTouchSlop;
    private boolean mScrollValid;
    private boolean mToVolume;
    private boolean mToSeek;
    private int mPosition;
    private int mDuration;
    private int mNewPosition = -1;
    private int mMaxVolume;
    private float mBrightness = -1;
    private int mVolume = -1;
    private GestureDetector mGestureDetector;
    private IMediaPlayerControl mMediaPlayerControl;
    private OnGestureListener mOnGestureListener;

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
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View root = LayoutInflater.from(context).inflate(R.layout.lib_player_layout_touch, this);
        initView(root);
        int[] size = Util.getScreenSize(mActivity);
        mScreenWidth = size[0];
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(context, new PlayerGestureListener());

        root.setClickable(true);
        root.setOnTouchListener(this);
    }

    private void initView(View root) {
        ll_player_adj_prg = (LinearLayout) root.findViewById(R.id.ll_player_adj_prg);
        ll_player_adj_brightness = (LinearLayout) root.findViewById(R.id.ll_player_adj_brightness);
        ll_player_adj_volume = (LinearLayout) root.findViewById(R.id.ll_player_adj_volume);
        tv_player_adj_prg = (TextView) root.findViewById(R.id.tv_player_adj_prg);
        prb_player_adj_prg = (ProgressBar) root.findViewById(R.id.prb_player_adj_prg);
        prb_player_adj_brightness = (ProgressBar) root.findViewById(R.id.prb_player_adj_brightness);
        prb_player_adj_volume = (ProgressBar) root.findViewById(R.id.prb_player_adj_volume);
    }

    private void show(int type) {
        ll_player_adj_prg.setVisibility(type == TYPE_PROGRESS ? VISIBLE : GONE);
        ll_player_adj_brightness.setVisibility(type == TYPE_BRIGHTNESS ? VISIBLE : GONE);
        ll_player_adj_volume.setVisibility(type == TYPE_VOLUME ? VISIBLE : GONE);
    }

    private void onProgressSlide(float percent) {
        if (mMediaPlayerControl == null || mMediaPlayerControl.isLive()) {
            return;
        }
        show(TYPE_PROGRESS);
        mNewPosition = (int) (1f * mPosition + 1f * mDuration * percent);
        mNewPosition = Math.min(mNewPosition, mDuration);
        mNewPosition = Math.max(mNewPosition, 0);
        prb_player_adj_prg.setProgress((int) (mNewPosition * 1.0 / mDuration * 100));
        tv_player_adj_prg.setText(Util.generateTime(mNewPosition));
        mMediaPlayerControl.progressTo(mNewPosition, 0);
    }

    /**
     * Slide to change brightness
     */
    private void onBrightnessSlide(float percent) {
        show(TYPE_BRIGHTNESS);
        if (mBrightness < 0) {
            mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f;
            } else if (mBrightness < 0.01f) {
                mBrightness = 0.01f;
            }
        }
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.screenBrightness = mBrightness + percent;
        if (lp.screenBrightness > 1.0f) {
            lp.screenBrightness = 1.0f;
        } else if (lp.screenBrightness < 0.01f) {
            lp.screenBrightness = 0.01f;
        }
        prb_player_adj_brightness.setProgress((int) (lp.screenBrightness * 100));
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * Slide to adjust the sound size
     */
    private void onVolumeSlide(float percent) {
        show(TYPE_VOLUME);
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0) {
                mVolume = 0;
            }
        }
        int index = mVolume + (int) (mMaxVolume * percent);
        index = Math.min(index, mMaxVolume);
        index = Math.max(index, 0);
        // Change sound
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // Change progress bar
        prb_player_adj_volume.setProgress((int) (index * 1.0 / mMaxVolume * 100));
    }

    /**
     * End of gesture
     */
    private void endGesture() {
        show(GONE);
        mVolume = -1;
        mBrightness = -1f;
        if (mScrollValid && mToSeek && mMediaPlayerControl != null && !mMediaPlayerControl.isLive()) {
            mMediaPlayerControl.seekTo(mNewPosition);
            mMediaPlayerControl.lockProgress(false);
            mScrollValid = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        // Handling the end of gesture
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return false;
    }

    public void setIMediaPlayerControl(IMediaPlayerControl listener) {
        this.mMediaPlayerControl = listener;
    }

    public void setOnGestureListener(OnGestureListener l) {
        this.mOnGestureListener = l;
    }

    public interface OnGestureListener {
        void onTouch();
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float oldX = e1.getX(), oldY = e1.getY();
            float deltaX = oldX - e2.getX();
            float deltaY = oldY - e2.getY();
            if (!mScrollValid && (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop)) {
                mToSeek = Math.abs(deltaX) >= Math.abs(deltaY);
                mToVolume = oldX > mScreenWidth * 0.5f;
                mScrollValid = true; // Sliding takes effect
                if (mToSeek && mMediaPlayerControl != null) {
                    mPosition = mMediaPlayerControl.getCurrentPosition();
                    mDuration = mMediaPlayerControl.getDuration();
                    mMediaPlayerControl.lockProgress(true);
                }
            }
            if (mScrollValid) {
                if (mToSeek) {
                    onProgressSlide(-deltaX / getWidth());
                } else {
                    float percent = deltaY / getHeight();
                    if (mToVolume) {
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
            mScrollValid = false;
            if (mOnGestureListener != null) {
                mOnGestureListener.onTouch();
            }
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mMediaPlayerControl != null) {
                mMediaPlayerControl.toggleAspectRatio();
                return true;
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mMediaPlayerControl != null) {
                mMediaPlayerControl.toggleStick();
                return true;
            }
            return super.onSingleTapConfirmed(e);
        }
    }
}
