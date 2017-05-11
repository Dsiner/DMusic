package com.d.dmusic.mvp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.api.IQueueListener;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.events.PlayOrPauseEvent;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.media.SyncUtil;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.utils.Util;
import com.d.dmusic.utils.log.ULog;
import com.d.dmusic.view.popup.PlayQueuePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * PlayActivity
 * Created by D on 2017/4/29.
 */
public class PlayActivity extends BaseActivity<MvpBasePresenter> implements SeekBar.OnSeekBarChangeListener, MvpView, IQueueListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_song_name)
    TextView tvSongName;
    @Bind(R.id.iv_album)
    ImageView ivAlbum;
    @Bind(R.id.tv_time_start)
    TextView tvTimeStart;
    @Bind(R.id.tv_time_end)
    TextView tvTimeEnd;
    @Bind(R.id.sb_progress)
    SeekBar seekBar;
    @Bind(R.id.ib_play_collect)
    ImageButton ibColect;
    @Bind(R.id.ib_play_prev)
    ImageButton ibPrev;
    @Bind(R.id.ib_play_play_pause)
    ImageButton ibPlayPause;
    @Bind(R.id.ib_play_next)
    ImageButton ibNext;
    @Bind(R.id.ib_play_queue)
    ImageButton ibPlayQueue;

    private Context context;
    private MusicControl control;
    private ObjectAnimator animator;
    private PlayQueuePopup queuePopup;
    private PlayerReceiver playerReceiver;
    private boolean isRegisterReceiver;// 是否注册了广播监听器
    private int type = MusicDB.MUSIC;
    private boolean isNeedReLoad;//为了同步收藏状态，需要重新加载数据

    @OnClick({R.id.iv_back, R.id.ib_play_collect, R.id.ib_play_prev,
            R.id.ib_play_play_pause, R.id.ib_play_next, R.id.ib_play_queue})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ib_play_collect:
                if (control != null && control.getCurModel() != null) {
                    MusicModel item = control.getCurModel();
                    item.isCollected = !item.isCollected;
                    SyncUtil.upCollected(context.getApplicationContext(), item);
                }
                break;
            case R.id.ib_play_prev:
                Intent prev = new Intent(MusciCst.PLAYER_CONTROL_PREV);
                prev.putExtra("flag", MusciCst.PLAY_FLAG_PRE);
                sendBroadcast(prev);
                break;
            case R.id.ib_play_play_pause:
                Intent playPause = new Intent(MusciCst.PLAYER_CONTROL_PLAY_PAUSE);
                playPause.putExtra("flag", MusciCst.PLAY_FLAG_PLAY_PAUSE);
                sendBroadcast(playPause);
                break;
            case R.id.ib_play_next:
                Intent next = new Intent(MusciCst.PLAYER_CONTROL_NEXT);
                next.putExtra("flag", MusciCst.PLAY_FLAG_NEXT);
                sendBroadcast(next);
                break;
            case R.id.ib_play_queue:
                showQueue();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.play;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        StatusBarCompat.compat(this, 0xff000000);//沉浸式状态栏
        EventBus.getDefault().register(this);
        registerReceiver();
        isRegisterReceiver = true;
    }

    @Override
    protected void init() {
        control = MusicService.getControl();
        seekBar.setOnSeekBarChangeListener(this);
        onPlayModeChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            control.reLoad();
        }
        tvSongName.setText(control.getCurSongName());
        MediaPlayer mediaPlayer = control.getMediaPlayer();
        if (mediaPlayer != null && control.getStatus() == MusciCst.PLAY_STATUS_PLAYING) {
            final int duration = mediaPlayer.getDuration();
            final int currentPosition = mediaPlayer.getCurrentPosition();
            tvTimeEnd.setText(Util.formatTime(duration));
            seekBar.setMax(duration / 1000);
            seekBar.setProgress(currentPosition / 1000);
        } else {
            tvTimeEnd.setText(Util.formatTime(0));
            seekBar.setMax(0);
            seekBar.setProgress(0);
        }

        if (control.getStatus() == MusciCst.PLAY_STATUS_PLAYING) {
            //正在播放
            ibPlayPause.setImageResource(R.drawable.play_pause);
            rotationAnimator();
        } else {
            //无列表播放/暂停
            ibPlayPause.setImageResource(R.drawable.play_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    private void showQueue() {
        if (queuePopup == null) {
            queuePopup = new PlayQueuePopup(context);
            queuePopup.setOnQueueListener(this);
        }
        queuePopup.show();
    }

    private void dismissQueue() {
        if (queuePopup != null) {
            queuePopup.dismiss();
        }
    }

    private void registerReceiver() {
        // 定义和注册广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusciCst.MUSIC_CURRENT_POSITION);
        filter.addAction(MusciCst.MUSIC_CURRENT_INFO);
        registerReceiver(playerReceiver, filter);
    }

    private void rotationAnimator() {
        animator = ObjectAnimator.ofFloat(ivAlbum, "rotation", 0f, 360f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(10000);// 重复次数
        animator.setDuration(5000);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                float f = animation.getDuration();
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
        tvTimeStart.setText(String.format("%02d:%02d", progress / 60, progress % 60));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //开始拖动
        MusicService.progressLock = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //停止拖动
        progressChanged(seekBar.getProgress() * 1000);
        ULog.v("跳转到1：" + seekBar.getProgress());
    }

    /**
     * 播放进度改变
     */
    public void progressChanged(int progress) {
        ULog.v("跳转到:--" + progress);
        Intent intent = new Intent();
        intent.setAction(MusciCst.MUSIC_SEEK_TO_TIME);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    @Override
    public void onPlayModeChange() {
        if (ibPlayQueue != null) {
            ibPlayQueue.setImageResource(MusciCst.PLAY_MODE_DRAWABLE[MusicControl.playMode]);
        }
    }

    @Override
    public void onCountChange(int count) {

    }

    /**
     * 用来接收从service传回来的广播的内部类
     */
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusciCst.MUSIC_CURRENT_POSITION)) {
                int currentPosition = intent.getIntExtra("currentPosition", 0);
                int duration = intent.getIntExtra("duration", 0);
                tvTimeStart.setText(Util.formatTime(currentPosition));
                tvTimeEnd.setText(Util.formatTime(duration));
                seekBar.setMax(duration / 1000);
                seekBar.setProgress(currentPosition / 1000);
            }
            if (action.equals(MusciCst.MUSIC_CURRENT_INFO)) {
                tvSongName.setText(control.getCurSongName());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlayOrPauseEvent event) {
        if (event.isPlay) {
            ibPlayPause.setImageResource(R.drawable.play_pause);
            rotationAnimator();
        } else {
            ibPlayPause.setImageResource(R.drawable.play_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRefreshEvent(RefreshEvent event) {
        if (event == null || isFinishing()
                || event.event == type || event.type != RefreshEvent.SYNC_COLLECTIONG) {
            return;
        }
        isNeedReLoad = true;
    }

    @Override
    protected void onDestroy() {
        Preferences.getInstance(getApplicationContext()).putPlayMode(MusicControl.playMode);
        if (isRegisterReceiver && playerReceiver != null) {
            unregisterReceiver(playerReceiver);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
