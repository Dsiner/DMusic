package com.d.lib.commenplayer.ui;

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
    private FrameLayout top;
    private ImageView ivBack;
    private TextView tvTitle;

    /**
     * Bottom
     */
    private RelativeLayout bottom;
    private ImageView playPause;
    private TextView current;
    private SeekBar seekBar;
    private TextView total;
    private ImageView fullscreen;

    /**
     * Center-loading
     */
    private ProgressBar loading;

    /**
     * Center-tips
     */
    private LinearLayout tips;
    private TextView tipsText;
    private TextView tipsBtn;

    private Activity activity;
    private int duration;

    private Handler handler = new Handler();
    private StickTask stickTask;
    private boolean stickTaskRunning;

    private ValueAnimator animation;
    private ValueAnimator.AnimatorUpdateListener amListener;
    private float factor; // Factor: 0-1
    private int height42;

    private boolean isPortrait = true;
    private IMediaPlayerControl listener;

    static class StickTask implements Runnable {
        private final WeakReference<ControlLayout> reference;

        StickTask(ControlLayout layout) {
            this.reference = new WeakReference<>(layout);
        }

        @Override
        public void run() {
            ControlLayout layout = reference.get();
            if (layout == null || layout.getContext() == null || !layout.stickTaskRunning) {
                return;
            }
            layout.startAnim();
        }
    }

    private void reStartStickTask() {
        stopStickTask();
        stickTaskRunning = true;
        handler.postDelayed(stickTask, TASK_STICK_TIME);
    }

    private void stopStickTask() {
        stickTaskRunning = false;
        handler.removeCallbacks(stickTask);
    }

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

    private void init(Context context) {
        this.activity = (Activity) context;
        View root = LayoutInflater.from(context).inflate(R.layout.lib_player_layout_control, this);
        initView(root);
        setClipToPadding(false);
        setClipChildren(false);
        initAnim();
        height42 = Util.dip2px(context, 42);
        stickTask = new StickTask(this);
        playPause.setOnClickListener(onClickListener);
        fullscreen.setOnClickListener(onClickListener);
        ivBack.setOnClickListener(onClickListener);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                current.setText(Util.generateTime(Util.getPosition(progress, duration)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.lockProgress(true); // Lock
                }
                if (!isPortrait) {
                    stopStickTask();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.seekTo(Util.getPosition(seekBar.getProgress(), duration));
                    listener.lockProgress(false); // Release lock
                }
                if (!isPortrait) {
                    reStartStickTask();
                }
            }
        });
    }

    private void initView(View root) {
        // Top
        top = (FrameLayout) root.findViewById(R.id.layout_player_top);
        ivBack = (ImageView) root.findViewById(R.id.iv_player_back);
        tvTitle = (TextView) root.findViewById(R.id.tv_player_title);

        // Bottom
        bottom = (RelativeLayout) root.findViewById(R.id.layout_player_bottom);
        playPause = (ImageView) root.findViewById(R.id.iv_player_play_pause);
        current = (TextView) root.findViewById(R.id.tv_player_current);
        seekBar = (SeekBar) root.findViewById(R.id.seek_player_progress);
        seekBar.setMax(Util.SEEKBAR_MAX);
        total = (TextView) root.findViewById(R.id.tv_player_total);
        fullscreen = (ImageView) root.findViewById(R.id.iv_player_fullscreen);

        // Center-loading
        loading = (ProgressBar) root.findViewById(R.id.prb_player_loading);

        // Center-tips
        tips = (LinearLayout) root.findViewById(R.id.layout_player_tips);
        tipsText = (TextView) root.findViewById(R.id.tv_player_tips_text);
        tipsBtn = (TextView) root.findViewById(R.id.tv_player_tips_btn);
    }

    private void initAnim() {
        animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(250);
        animation.setInterpolator(new LinearInterpolator());
        amListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isPortrait) {
                    return;
                }
                factor = (float) animation.getAnimatedValue();
                top.setVisibility(factor == 1 ? GONE : VISIBLE);
                top.scrollTo(0, (int) (height42 * factor));
                bottom.scrollTo(0, (int) (-height42 * factor));
                if (listener != null) {
                    listener.onAnimationUpdate(factor);
                }
            }
        };
        animation.addUpdateListener(amListener);
        animation.addListener(new Animator.AnimatorListener() {
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
        if (animation != null) {
            if (factor == 0) {
                if (listener != null) {
                    listener.toggleSystemUI(false);
                }
                animation.setFloatValues(0, 1); // Dismiss
            } else {
                if (listener != null) {
                    listener.toggleSystemUI(true);
                }
                animation.setFloatValues(1, 0); // Show
                reStartStickTask();
            }
            animation.addUpdateListener(amListener);
            animation.start();
        }
    }

    private void stopAnim() {
        if (animation != null) {
            animation.removeUpdateListener(amListener);
            animation.end();
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
                setControlVisibility(listener == null || listener.isLive() ? INVISIBLE : VISIBLE, VISIBLE);
                break;
            case STATE_MOBILE_NET:
                setPlayerVisibility(GONE, VISIBLE, GONE);
                setControlVisibility(INVISIBLE, INVISIBLE);
                setControl(getResources().getString(R.string.lib_player_mobile_network_continue),
                        getResources().getString(R.string.lib_player_continue_playing),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPlayerVisibility(GONE, GONE, VISIBLE);
                                setControlVisibility(listener == null || listener.isLive() ? INVISIBLE : VISIBLE, VISIBLE);
                                if (listener != null) {
                                    listener.ignoreMobileNet();
                                    listener.start();
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
                                if (listener != null) {
                                    listener.play(listener.getUrl());
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
                                if (listener != null) {
                                    listener.play(listener.getUrl());
                                }
                            }
                        });
                break;
        }
    }

    private void setPlayerVisibility(int visibility0, int visibility1, int visibility2) {
        loading.setVisibility(visibility0);
        tips.setVisibility(visibility1);
        if (listener != null) {
            listener.setPlayerVisibility(visibility2);
        }
    }

    /**
     * Set the bottom control display state
     *
     * @param visibility0 Control button
     * @param visibility1 Full screen button
     */
    private void setControlVisibility(int visibility0, int visibility1) {
        playPause.setVisibility(visibility0);
        current.setVisibility(visibility0);
        seekBar.setVisibility(visibility0);
        total.setVisibility(visibility0);
        fullscreen.setVisibility(visibility1);
    }

    private void setControl(String text, String button, View.OnClickListener l) {
        tips.setVisibility(VISIBLE);
        tipsText.setText(text);
        tipsText.setVisibility(View.VISIBLE);
        tipsBtn.setText(button);
        tipsBtn.setVisibility(View.VISIBLE);
        tipsBtn.setOnClickListener(l);
    }

    public void setProgress(int position, int duration, int bufferPercentage) {
        position = Math.min(position, duration);
        position = Math.max(position, 0);
        if (duration > 0) {
            seekBar.setProgress(Util.getProgress(position, duration));
            seekBar.setSecondaryProgress(Util.getSecondaryProgress(bufferPercentage));
            current.setText(Util.generateTime(position));
            total.setText(Util.generateTime(duration));
            this.duration = duration;
        }
    }

    public void toggleStick() {
        if (factor != 0 && factor != 1) {
            return;
        }
        stopStickTask();
        startAnim();
    }

    public void onConfigurationChanged(boolean isPortrait) {
        this.isPortrait = isPortrait;
        stopAnim();
        stopStickTask();
        factor = 0;
        top.scrollTo(0, 0);
        bottom.scrollTo(0, 0);
        bottom.setVisibility(VISIBLE);
        if (isPortrait) {
            top.setVisibility(GONE);
            fullscreen.setImageResource(R.drawable.lib_player_ic_fullscreen_in);
        } else {
            top.setVisibility(VISIBLE);
            fullscreen.setImageResource(R.drawable.lib_player_ic_fullscreen_out);
            reStartStickTask();
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_player_play_pause) {
                if (listener == null) {
                    return;
                }
                if (listener.isPlaying()) {
                    listener.pause();
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.lib_player_ic_play));
                } else {
                    listener.start();
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.lib_player_ic_pause));
                }
            } else if (id == R.id.iv_player_fullscreen) {
                if (listener != null) {
                    listener.toggleOrientation();
                }
            } else if (id == R.id.iv_player_back) {
                if (listener != null) {
                    listener.toggleOrientation();
                }
            }
        }
    };

    public void setIMediaPlayerControl(IMediaPlayerControl l) {
        this.listener = l;
    }
}
