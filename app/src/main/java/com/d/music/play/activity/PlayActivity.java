package com.d.music.play.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseActivity;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.log.ULog;
import com.d.music.App;
import com.d.music.R;
import com.d.music.common.Preferences;
import com.d.music.listener.IQueueListener;
import com.d.music.module.events.MusicInfoEvent;
import com.d.music.module.global.MusicCst;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.service.MusicControl;
import com.d.music.module.service.MusicService;
import com.d.music.module.utils.MoreUtil;
import com.d.music.play.presenter.PlayPresenter;
import com.d.music.play.view.IPlayView;
import com.d.music.utils.StatusBarCompat;
import com.d.music.view.lrc.LrcRow;
import com.d.music.view.lrc.LrcView;
import com.d.music.view.popup.MorePopup;
import com.d.music.view.popup.PlayQueuePopup;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * PlayActivity
 * Created by D on 2017/4/29.
 */
public class PlayActivity extends BaseActivity<PlayPresenter> implements IPlayView, SeekBar.OnSeekBarChangeListener, IQueueListener {

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (MusicCst.playerMode == MusicCst.PLAYER_MODE_NORMAL) {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, R.anim.push_stay);
            }
        }
    }

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lrcv_lrc)
    LrcView lrc;
    @BindView(R.id.iv_album)
    ImageView ivAlbum;
    @BindView(R.id.tv_time_start)
    TextView tvTimeStart;
    @BindView(R.id.tv_time_end)
    TextView tvTimeEnd;
    @BindView(R.id.sb_progress)
    SeekBar seekBar;

    @BindView(R.id.iv_play_collect)
    ImageView ivColect;
    @BindView(R.id.iv_play_play_pause)
    ImageView ivPlayPause;
    @BindView(R.id.iv_play_queue)
    ImageView ivPlayQueue;

    private int type = MusicDB.MUSIC;
    private MusicControl control;
    private ObjectAnimator animator;
    private PlayQueuePopup queuePopup;
    private PlayerReceiver playerReceiver;
    private boolean isRegisterReceiver;// 是否注册了广播监听器
    public static boolean isNeedReLoad;//为了同步收藏状态，需要重新加载数据

    @OnClick({R.id.iv_back, R.id.iv_more, R.id.iv_play_collect, R.id.iv_play_prev,
            R.id.iv_play_play_pause, R.id.iv_play_next, R.id.iv_play_queue})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_more:
                showMore();
                break;
            case R.id.iv_play_collect:
                collect(false);
                break;
            case R.id.iv_play_prev:
                if (control == null || control.getModels() == null || control.getModels().size() <= 0) {
                    return;
                }
                Intent prev = new Intent(MusicCst.PLAYER_CONTROL_PREV);
                prev.putExtra("flag", MusicCst.PLAY_FLAG_PRE);
                sendBroadcast(prev);
                break;
            case R.id.iv_play_play_pause:
                if (control == null || control.getModels() == null || control.getModels().size() <= 0) {
                    return;
                }
                Intent playPause = new Intent(MusicCst.PLAYER_CONTROL_PLAY_PAUSE);
                playPause.putExtra("flag", MusicCst.PLAY_FLAG_PLAY_PAUSE);
                sendBroadcast(playPause);
                break;
            case R.id.iv_play_next:
                if (control == null || control.getModels() == null || control.getModels().size() <= 0) {
                    return;
                }
                Intent next = new Intent(MusicCst.PLAYER_CONTROL_NEXT);
                next.putExtra("flag", MusicCst.PLAY_FLAG_NEXT);
                sendBroadcast(next);
                break;
            case R.id.iv_play_queue:
                showQueue();
                break;
        }
    }

    private void collect(boolean isTip) {
        if (control != null && control.getCurModel() != null) {
            MusicModel item = control.getCurModel();
            MoreUtil.collect(getApplicationContext(), item, type, isTip);
            resetFav(item.isCollected);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_play;
    }

    @Override
    public PlayPresenter getPresenter() {
        return new PlayPresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (App.toFinish(intent)) {
            finish();
        }
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.compat(this, Color.parseColor("#ff000000"));//沉浸式状态栏
        EventBus.getDefault().register(this);
        registerReceiver();
        control = MusicService.getControl(getApplicationContext());
        seekBar.setOnSeekBarChangeListener(this);
        initLrcListener();
        onPlayModeChange(Preferences.getInstance(getApplicationContext()).getPlayMode());
        initAlbum();
    }

    private void initAlbum() {
        tvTitle.setText(control.getCurSongName());
        MediaPlayer mediaPlayer = control.getMediaPlayer();
        final int status = control.getStatus();
        if (mediaPlayer != null && (status == MusicCst.PLAY_STATUS_PLAYING
                || status == MusicCst.PLAY_STATUS_PAUSE)) {
            final int duration = mediaPlayer.getDuration();
            final int currentPosition = mediaPlayer.getCurrentPosition();
            setProgress(currentPosition, duration);
            tvTimeStart.setText(Util.formatTime(currentPosition));
            MusicModel model = control.getCurModel();
            resetFav(model.isCollected);
            mPresenter.getLrcRows(model);
        } else {
            setProgress(0, 0);
        }

        if (status == MusicCst.PLAY_STATUS_PLAYING) {
            //正在播放
            ivPlayPause.setImageResource(R.drawable.ic_play_pause);
            rotationAnimator();
        } else {
            //无列表播放/暂停
            ivPlayPause.setImageResource(R.drawable.ic_play_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.reLoad();
        }
    }

    private void resetFav(boolean isCollected) {
        int fav = isCollected ? R.drawable.ic_play_fav_cover : R.drawable.ic_play_fav;
        ivColect.setImageDrawable(getResources().getDrawable(fav));
    }

    private void initLrcListener() {
        lrc.setOnSeekChangeListener(new LrcView.OnSeekChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                if (progress >= 0 && progress <= seekBar.getMax()) {
                    seekBar.setProgress(progress);
                }
                control.seekTo(progress);
            }
        });
    }

    private void showQueue() {
        if (queuePopup == null) {
            queuePopup = new PlayQueuePopup(mActivity);
            queuePopup.setOnQueueListener(this);
        }
        queuePopup.show();
    }

    private void dismissQueue() {
        if (queuePopup != null) {
            queuePopup.dismiss();
        }
    }

    private void showMore() {
        MorePopup morePopup = new MorePopup(mActivity, MorePopup.TYPE_SONG_PLAY, control.getCurModel(), type);
        morePopup.setOnOperationLitener(new MorePopup.OnOperationLitener() {
            @Override
            public void onCollect() {
                collect(true);
            }
        });
        morePopup.show();
    }

    private void registerReceiver() {
        // 定义和注册广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicCst.MUSIC_CURRENT_POSITION);
        registerReceiver(playerReceiver, filter);
        isRegisterReceiver = true;
    }

    private void rotationAnimator() {
        animator = ObjectAnimator.ofFloat(ivAlbum, "rotation", 0f, 360f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(5000);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // rotationAnimator();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        //数值改变
        tvTimeStart.setText(String.format("%02d:%02d", progress / 1000 / 60, progress / 1000 % 60));
        lrc.seekTo(progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //开始拖动
        MusicService.progressLock = true;//加锁
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //停止拖动
        progressChanged(seekBar.getProgress());
        lrc.seekTo(seekBar.getProgress(), true);
        ULog.v("跳转到1：" + seekBar.getProgress());
    }

    /**
     * 播放进度改变
     */
    public void progressChanged(int progress) {
        if (control == null || control.getModels() == null || control.getModels().size() <= 0) {
            MusicService.progressLock = false;//解锁
            return;
        }
        ULog.v("跳转到:--" + progress);
        Intent intent = new Intent();
        intent.setAction(MusicCst.MUSIC_SEEK_TO_TIME);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    @Override
    public void onPlayModeChange(int playMode) {
        if (ivPlayQueue != null) {
            ivPlayQueue.setImageResource(MusicCst.PLAY_MODE_DRAWABLE[playMode]);
        }
    }

    @Override
    public void onCountChange(int count) {
        if (count <= 0) {
            setProgress(0, 0);
            tvTimeStart.setText(Util.formatTime(0));
            lrc.setLrcRows(new ArrayList<LrcRow>());
        }
    }

    @Override
    public void reLoad(List<MusicModel> list) {
        if (list.size() > 0) {
            MusicControl control = MusicService.getControl(getApplicationContext());
            control.reLoad(list);
            MusicModel model = control.getCurModel();
            resetFav(model != null ? model.isCollected : false);
        }
    }

    @Override
    public void setLrcRows(String path, List<LrcRow> lrcRows) {
        lrc.setLrcRows(lrcRows);
        lrc.seekTo(1000, true);
    }

    @Override
    public void seekTo(int progress) {
        lrc.seekTo(progress, false);
    }

    /**
     * 用来接收从service传回来的广播的内部类
     */
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || isFinishing() || mPresenter == null || !mPresenter.isViewAttached()) {
                return;
            }
            if (intent.getAction().equals(MusicCst.MUSIC_CURRENT_POSITION)) {
                int currentPosition = intent.getIntExtra("currentPosition", 0);
                int duration = intent.getIntExtra("duration", 0);
                tvTimeStart.setText(Util.formatTime(currentPosition));
                setProgress(currentPosition, duration);
                lrc.seekTo(currentPosition, false);
            }
        }
    }

    private void setProgress(int currentPosition, int duration) {
        tvTimeEnd.setText(Util.formatTime(duration));
        seekBar.setMax(duration);
        seekBar.setProgress(currentPosition);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MusicInfoEvent event) {
        if (event == null || isFinishing() || mPresenter == null || !mPresenter.isViewAttached()) {
            return;
        }
        MusicModel model = control.getCurModel();
        tvTitle.setText(model != null ? model.songName : "");
        resetFav(model != null ? model.isCollected : false);
        mPresenter.getLrcRows(model);
        togglePlay(model != null && event.status == MusicCst.PLAY_STATUS_PLAYING);
    }

    private void togglePlay(boolean isPlay) {
        if (isPlay) {
            ivPlayPause.setImageResource(R.drawable.ic_play_pause);
            rotationAnimator();
        } else {
            ivPlayPause.setImageResource(R.drawable.ic_play_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (MusicCst.playerMode == MusicCst.PLAYER_MODE_NORMAL) {
            overridePendingTransition(R.anim.push_stay, R.anim.push_bottom_out);
        } else {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            if (playerReceiver != null) {
                unregisterReceiver(playerReceiver);
            }
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
