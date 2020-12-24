package com.d.lib.commenplayer.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d.lib.commenplayer.R;
import com.d.lib.commenplayer.listener.IMediaPlayerControl;
import com.d.lib.commenplayer.util.Util;

import java.lang.ref.WeakReference;

public class ControlLayout extends RelativeLayout {
    public static final int STATE_LOADING = 0;
    public static final int STATE_PREPARED = 1;
    public static final int STATE_MOBILE_NET = 2;
    public static final int STATE_COMPLETION = 3;
    public static final int STATE_ERROR = 4;

    private final int TASK_STICK_TIME = 5000;

    /**
     * Top
     */
    private FrameLayout layout_player_top;
    private ImageView iv_player_back;
    private TextView tv_player_title;

    /**
     * Bottom
     */
    private RelativeLayout layout_player_bottom;
    private ImageView iv_player_play_pause;
    private TextView tv_player_current;
    private SeekBar seek_player_progress;
    private TextView tv_player_total;
    private ImageView iv_player_fullscreen;

    /**
     * Center-loading
     */
    private ProgressBar prb_player_loading;

    /**
     * Center-tips
     */
    private LinearLayout layout_player_tips;
    private TextView tv_player_tips_text;
    private TextView tv_player_tips_btn;

    private Activity mActivity;
    private int mDuration;
    private Handler mHandler = new Handler();
    private StickTask mStickTask;
    private boolean mIsStickTaskRunning;
    private ValueAnimator mAnimation;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private float mFactor; // Factor: 0-1
    private int mHeight42;
    private boolean mIsPortrait = true;
    private IMediaPlayerControl mMediaPlayerControl;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_player_play_pause) {
                if (mMediaPlayerControl == null) {
                    return;
                }
                if (mMediaPlayerControl.isPlaying()) {
                    mMediaPlayerControl.pause();
                    iv_player_play_pause.setImageDrawable(getResources().getDrawable(R.drawable.lib_player_ic_play));
                } else {
                    mMediaPlayerControl.start();
                    iv_player_play_pause.setImageDrawable(getResources().getDrawable(R.drawable.lib_player_ic_pause));
                }

            } else if (id == R.id.iv_player_fullscreen) {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.toggleOrientation();
                }

            } else if (id == R.id.iv_player_back) {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.toggleOrientation();
                }
            }
        }
    };

    public ControlLayout(Context context) {
        this(context, null);
    }

    public ControlLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void reStartStickTask() {
        stopStickTask();
        mIsStickTaskRunning = true;
        mHandler.postDelayed(mStickTask, TASK_STICK_TIME);
    }

    private void stopStickTask() {
        mIsStickTaskRunning = false;
        mHandler.removeCallbacks(mStickTask);
    }

    private void init(Context context) {
        this.mActivity = (Activity) context;
        View root = LayoutInflater.from(context).inflate(R.layout.lib_player_layout_control, this);
        initView(root);
        setClipToPadding(false);
        setClipChildren(false);
        initAnim();
        mHeight42 = Util.dp2px(context, 42);
        mStickTask = new StickTask(this);
        iv_player_play_pause.setOnClickListener(mOnClickListener);
        iv_player_fullscreen.setOnClickListener(mOnClickListener);
        iv_player_back.setOnClickListener(mOnClickListener);
        seek_player_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                tv_player_current.setText(Util.generateTime(Util.getPosition(progress, mDuration)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.lockProgress(true); // Lock
                }
                if (!mIsPortrait) {
                    stopStickTask();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.seekTo(Util.getPosition(seekBar.getProgress(), mDuration));
                    mMediaPlayerControl.lockProgress(false); // Release lock
                }
                if (!mIsPortrait) {
                    reStartStickTask();
                }
            }
        });
    }

    private void initView(View root) {
        // Top
        layout_player_top = (FrameLayout) root.findViewById(R.id.layout_player_top);
        iv_player_back = (ImageView) root.findViewById(R.id.iv_player_back);
        tv_player_title = (TextView) root.findViewById(R.id.tv_player_title);

        // Bottom
        layout_player_bottom = (RelativeLayout) root.findViewById(R.id.layout_player_bottom);
        iv_player_play_pause = (ImageView) root.findViewById(R.id.iv_player_play_pause);
        tv_player_current = (TextView) root.findViewById(R.id.tv_player_current);
        seek_player_progress = (SeekBar) root.findViewById(R.id.seek_player_progress);
        seek_player_progress.setMax(Util.SEEKBAR_MAX);
        tv_player_total = (TextView) root.findViewById(R.id.tv_player_total);
        iv_player_fullscreen = (ImageView) root.findViewById(R.id.iv_player_fullscreen);

        // Center-loading
        prb_player_loading = (ProgressBar) root.findViewById(R.id.prb_player_loading);

        // Center-tips
        layout_player_tips = (LinearLayout) root.findViewById(R.id.layout_player_tips);
        tv_player_tips_text = (TextView) root.findViewById(R.id.tv_player_tips_text);
        tv_player_tips_btn = (TextView) root.findViewById(R.id.tv_player_tips_btn);
    }

    private void initAnim() {
        mAnimation = ValueAnimator.ofFloat(0f, 1f);
        mAnimation.setDuration(250);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mIsPortrait) {
                    return;
                }
                mFactor = (float) animation.getAnimatedValue();
                layout_player_top.setVisibility(mFactor == 1 ? GONE : VISIBLE);
                layout_player_top.scrollTo(0, (int) (mHeight42 * mFactor));
                layout_player_bottom.scrollTo(0, (int) (-mHeight42 * mFactor));
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.onAnimationUpdate(mFactor);
                }
            }
        };
        mAnimation.addUpdateListener(mAnimatorUpdateListener);
        mAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startAnim() {
        stopAnim();
        if (mAnimation != null) {
            if (mFactor == 0) {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.toggleSystemUI(false);
                }
                mAnimation.setFloatValues(0, 1); // Dismiss
            } else {
                if (mMediaPlayerControl != null) {
                    mMediaPlayerControl.toggleSystemUI(true);
                }
                mAnimation.setFloatValues(1, 0); // Show
                reStartStickTask();
            }
            mAnimation.addUpdateListener(mAnimatorUpdateListener);
            mAnimation.start();
        }
    }

    private void stopAnim() {
        if (mAnimation != null) {
            mAnimation.removeUpdateListener(mAnimatorUpdateListener);
            mAnimation.end();
        }
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                setPlayerVisibility(VISIBLE, GONE, GONE);
                setControlVisibility(INVISIBLE, INVISIBLE);
                break;

            case STATE_PREPARED:
                setPlayerVisibility(GONE, GONE, VISIBLE);
                setControlVisibility(mMediaPlayerControl == null || mMediaPlayerControl.isLive() ? INVISIBLE : VISIBLE, VISIBLE);
                break;

            case STATE_MOBILE_NET:
                setPlayerVisibility(GONE, VISIBLE, GONE);
                setControlVisibility(INVISIBLE, INVISIBLE);
                setControl(getResources().getString(R.string.lib_player_mobile_data_continue),
                        getResources().getString(R.string.lib_player_continue_playing),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPlayerVisibility(GONE, GONE, VISIBLE);
                                setControlVisibility(mMediaPlayerControl == null || mMediaPlayerControl.isLive() ? INVISIBLE : VISIBLE, VISIBLE);
                                if (mMediaPlayerControl != null) {
                                    mMediaPlayerControl.ignoreMobileNet();
                                    mMediaPlayerControl.start();
                                }
                            }
                        });
                break;

            case STATE_COMPLETION:
                setPlayerVisibility(GONE, VISIBLE, GONE);
                setControlVisibility(INVISIBLE, INVISIBLE);
                setControl(getResources().getString(R.string.lib_player_play_end_replay),
                        getResources().getString(R.string.lib_player_replay),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPlayerVisibility(VISIBLE, GONE, GONE);
                                if (mMediaPlayerControl != null) {
                                    mMediaPlayerControl.play(mMediaPlayerControl.getUrl());
                                }
                            }
                        });
                break;

            case STATE_ERROR:
                setPlayerVisibility(GONE, VISIBLE, GONE);
                setControlVisibility(INVISIBLE, INVISIBLE);
                setControl(getResources().getString(R.string.lib_player_play_failed_retry),
                        getResources().getString(R.string.lib_player_retry),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPlayerVisibility(VISIBLE, GONE, GONE);
                                if (mMediaPlayerControl != null) {
                                    mMediaPlayerControl.play(mMediaPlayerControl.getUrl());
                                }
                            }
                        });
                break;
        }
    }

    private void setPlayerVisibility(int visibility0, int visibility1, int visibility2) {
        prb_player_loading.setVisibility(visibility0);
        layout_player_tips.setVisibility(visibility1);
        if (mMediaPlayerControl != null) {
            mMediaPlayerControl.setPlayerVisibility(visibility2);
        }
    }

    /**
     * Set the bottom control display state
     *
     * @param visibility0 Control button
     * @param visibility1 Full screen button
     */
    private void setControlVisibility(int visibility0, int visibility1) {
        iv_player_play_pause.setVisibility(visibility0);
        tv_player_current.setVisibility(visibility0);
        seek_player_progress.setVisibility(visibility0);
        tv_player_total.setVisibility(visibility0);
        iv_player_fullscreen.setVisibility(visibility1);
    }

    private void setControl(String text, String button, View.OnClickListener l) {
        layout_player_tips.setVisibility(VISIBLE);
        tv_player_tips_text.setText(text);
        tv_player_tips_text.setVisibility(View.VISIBLE);
        tv_player_tips_btn.setText(button);
        tv_player_tips_btn.setVisibility(View.VISIBLE);
        tv_player_tips_btn.setOnClickListener(l);
    }

    public void setProgress(int position, int duration, int bufferPercentage) {
        position = Math.min(position, duration);
        position = Math.max(position, 0);
        if (duration > 0) {
            seek_player_progress.setProgress(Util.getProgress(position, duration));
            seek_player_progress.setSecondaryProgress(Util.getSecondaryProgress(bufferPercentage));
            tv_player_current.setText(Util.generateTime(position));
            tv_player_total.setText(Util.generateTime(duration));
            this.mDuration = duration;
        }
    }

    public void toggleStick() {
        if (mFactor != 0 && mFactor != 1) {
            return;
        }
        stopStickTask();
        startAnim();
    }

    public void onConfigurationChanged(boolean isPortrait) {
        this.mIsPortrait = isPortrait;
        stopAnim();
        stopStickTask();
        mFactor = 0;
        layout_player_top.scrollTo(0, 0);
        layout_player_bottom.scrollTo(0, 0);
        layout_player_bottom.setVisibility(VISIBLE);
        if (isPortrait) {
            layout_player_top.setVisibility(GONE);
            iv_player_fullscreen.setImageResource(R.drawable.lib_player_ic_fullscreen_in);
        } else {
            layout_player_top.setVisibility(VISIBLE);
            iv_player_fullscreen.setImageResource(R.drawable.lib_player_ic_fullscreen_out);
            reStartStickTask();
        }
    }

    public void setIMediaPlayerControl(IMediaPlayerControl l) {
        this.mMediaPlayerControl = l;
    }

    static class StickTask implements Runnable {
        private final WeakReference<ControlLayout> ref;

        StickTask(ControlLayout layout) {
            this.ref = new WeakReference<>(layout);
        }

        @Override
        public void run() {
            ControlLayout layout = ref.get();
            if (layout == null || layout.getContext() == null || !layout.mIsStickTaskRunning) {
                return;
            }
            layout.startAnim();
        }
    }
}
