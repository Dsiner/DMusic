package com.d.dmusic.view.popup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.api.IQueueListener;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.adapter.PlayQueueAdapter;
import com.d.dmusic.utils.Util;
import com.d.xrv.LRecyclerView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.List;

/**
 * PlayQueuePopup
 * Created by D on 2017/4/29.
 */
public class PlayQueuePopup extends AbstractPopup implements View.OnClickListener, IQueueListener {
    private int animTime = 300;
    private ValueAnimator anim;
    private int heightRec;

    private View vBlank;
    private FrameLayout flytPlayMode;
    private ImageView ivPlayMode;
    private TextView tvPlayMode;
    private TextView tvCount;
    private TextView ivDeleteAll;
    private TextView ivQuit;
    private LRecyclerView lrvList;
    private PlayQueueAdapter adapter;
    private IQueueListener listener;
    private List<MusicModel> models;

    public PlayQueuePopup(Context context) {
        super(context, R.style.popupAnimation);
    }

    @Override
    protected void init() {
        vBlank = rootView.findViewById(R.id.v_queue_blank);
        flytPlayMode = (FrameLayout) rootView.findViewById(R.id.flyt_play_mode);
        ivPlayMode = (ImageView) rootView.findViewById(R.id.iv_play_mode);
        tvPlayMode = (TextView) rootView.findViewById(R.id.tv_play_mode);
        tvCount = (TextView) rootView.findViewById(R.id.tv_count);
        ivDeleteAll = (TextView) rootView.findViewById(R.id.tv_delete_all);
        ivQuit = (TextView) rootView.findViewById(R.id.tv_quit);
        lrvList = (LRecyclerView) rootView.findViewById(R.id.lrv_list);

        models = MusicService.getControl().getModels();
        adapter = new PlayQueueAdapter(context, models, R.layout.adapter_play_queue, this);
        lrvList.setAdapter(adapter);

        tvCount.setText("(" + (models != null ? models.size() : 0) + "首)");
        ivPlayMode.setBackgroundResource(MusciCst.PLAY_MODE_DRAWABLE[MusicControl.playMode]);
        tvPlayMode.setText(MusciCst.PLAY_MODE[MusicControl.playMode]);

        vBlank.setOnClickListener(this);
        flytPlayMode.setOnClickListener(this);
        ivDeleteAll.setOnClickListener(this);
        ivQuit.setOnClickListener(this);

        heightRec = Util.dip2px(context.getApplicationContext(), 260);
        initAnim();
    }

    private void changeMode() {
        MusicControl.playMode++;
        if (MusicControl.playMode > 3) {
            MusicControl.playMode = 0;
        }
        ivPlayMode.setBackgroundResource(MusciCst.PLAY_MODE_DRAWABLE[MusicControl.playMode]);
        tvPlayMode.setText(MusciCst.PLAY_MODE[MusicControl.playMode]);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flyt_play_mode:
                changeMode();
                if (listener != null) {
                    listener.onPlayModeChange();
                }
                break;
            case R.id.tv_delete_all:
                MusicService.getControl().delelteAll();
                adapter.notifyDataSetChanged();
                tvCount.setText("(" + 0 + "首)");
                break;
            case R.id.v_queue_blank:
            case R.id.tv_quit:
                dismiss();
                break;
        }
    }

    private void initAnim() {
        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setTarget(rootView);
        anim.setDuration(250);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rootView.setVisibility(View.VISIBLE);
                float bee = 1 - value;
                rootView.setY(heightRec * bee);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                rootView.setY(0);
                rootView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                rootView.setY(0);
                rootView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void show() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            if (context != null && !((Activity) context).isFinishing()) {
                popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                rootView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_bottom_in));
                //初始化
                Animation anim = new AlphaAnimation(0, 1);
                anim.setDuration(animTime);
                rootView.startAnimation(anim);
                this.anim.start();
            }
        }
    }

    @Override
    public void dismiss() {
        if (popupWindow != null) {
            rootView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_bottom_out));
            //初始化
            Animation anim = new AlphaAnimation(1, 0);
            anim.setDuration(animTime);
            rootView.startAnimation(anim);
            handler.sendEmptyMessageDelayed(0, animTime);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.popup_play_queue;
    }

    @Override
    public void onPlayModeChange() {

    }

    @Override
    public void onCountChange(int count) {
        tvCount.setText(count + "首");
    }

    @Override
    public List<MusicModel> getQueue() {
        return null;
    }

    public void setOnQueueListener(IQueueListener listener) {
        this.listener = listener;
    }
}