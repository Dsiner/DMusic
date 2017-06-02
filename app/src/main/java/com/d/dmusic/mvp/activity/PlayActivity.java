package com.d.dmusic.mvp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d.commen.base.BaseActivity;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.api.IQueueListener;
import com.d.dmusic.commen.Preferences;
import com.d.dmusic.module.events.PlayOrPauseEvent;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.media.SyncUtil;
import com.d.dmusic.module.repeatclick.ClickUtil;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.presenter.PlayPresenter;
import com.d.dmusic.mvp.view.IPlayView;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.utils.Util;
import com.d.dmusic.utils.log.ULog;
import com.d.dmusic.view.lrc.LrcRow;
import com.d.dmusic.view.lrc.LrcView;
import com.d.dmusic.view.popup.PlayQueuePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * PlayActivity
 * Created by D on 2017/4/29.
 */
public class PlayActivity extends BaseActivity<PlayPresenter> implements IPlayView, SeekBar.OnSeekBarChangeListener, IQueueListener {
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.push_bottom_in, R.anim.push_stay);
        }
    }

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_song_name)
    TextView tvSongName;
    @Bind(R.id.lrcv_lrc)
    LrcView lrc;
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
    private int type = MusicDB.MUSIC;
    private MusicControl control;
    private ObjectAnimator animator;
    private PlayQueuePopup queuePopup;
    private PlayerReceiver playerReceiver;
    private boolean isRegisterReceiver;// 是否注册了广播监听器
    public static boolean isNeedReLoad;//为了同步收藏状态，需要重新加载数据

    @OnClick({R.id.iv_back, R.id.ib_play_collect, R.id.ib_play_prev,
            R.id.ib_play_play_pause, R.id.ib_play_next, R.id.ib_play_queue})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ib_play_collect:
                if (control != null && control.getCurModel() != null) {
                    MusicModel item = control.getCurModel();
                    item.isCollected = !item.isCollected;
                    resetFav(item.isCollected);
                    SyncUtil.upCollected(context.getApplicationContext(), item, type);
                }
                break;
            case R.id.ib_play_prev:
                Intent prev = new Intent(MusicCst.PLAYER_CONTROL_PREV);
                prev.putExtra("flag", MusicCst.PLAY_FLAG_PRE);
                sendBroadcast(prev);
                break;
            case R.id.ib_play_play_pause:
                Intent playPause = new Intent(MusicCst.PLAYER_CONTROL_PLAY_PAUSE);
                playPause.putExtra("flag", MusicCst.PLAY_FLAG_PLAY_PAUSE);
                sendBroadcast(playPause);
                break;
            case R.id.ib_play_next:
                Intent next = new Intent(MusicCst.PLAYER_CONTROL_NEXT);
                next.putExtra("flag", MusicCst.PLAY_FLAG_NEXT);
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
    public PlayPresenter getPresenter() {
        return new PlayPresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        context = this;
        StatusBarCompat.compat(this, 0xff000000);//沉浸式状态栏
        EventBus.getDefault().register(this);
        registerReceiver();
        isRegisterReceiver = true;
        control = MusicService.getControl();
        seekBar.setOnSeekBarChangeListener(this);
        initLrcListener();
        onPlayModeChange(Preferences.getInstance(getApplicationContext()).getPlayMode());
        initAlbum();
    }

    private void initAlbum() {
        tvSongName.setText(control.getCurSongName());
        MediaPlayer mediaPlayer = control.getMediaPlayer();
        final int status = control.getStatus();
        if (mediaPlayer != null && (status == MusicCst.PLAY_STATUS_PLAYING
                || status == MusicCst.PLAY_STATUS_PAUSE)) {
            final int duration = mediaPlayer.getDuration();
            final int currentPosition = mediaPlayer.getCurrentPosition();
            tvTimeEnd.setText(Util.formatTime(duration));
            tvTimeStart.setText(Util.formatTime(currentPosition));
            seekBar.setMax(duration);
            seekBar.setProgress(currentPosition);
            MusicModel model = control.getCurModel();
            resetFav(model.isCollected);
            getLrc(model);
        } else {
            tvTimeEnd.setText(Util.formatTime(0));
            seekBar.setMax(0);
            seekBar.setProgress(0);
        }

        if (status == MusicCst.PLAY_STATUS_PLAYING) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.reLoad();
        }
    }

    private void resetFav(boolean isCollected) {
        int fav = isCollected ? R.drawable.img_favourite_press : R.drawable.img_favourite_normal;
        ibColect.setImageDrawable(getResources().getDrawable(fav));
    }

    private void getLrc(MusicModel model) {
        if (model != null) {
            String lrcUrl = !TextUtils.isEmpty(model.lrcUrl) ? model.lrcUrl : model.folder + "/" + model.songName + ".lrc";
            mPresenter.getLrcRows(lrcUrl);
        }
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
        filter.addAction(MusicCst.MUSIC_CURRENT_POSITION);
        filter.addAction(MusicCst.MUSIC_CURRENT_INFO);
        registerReceiver(playerReceiver, filter);
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
        MusicService.progressLock = true;
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
        ULog.v("跳转到:--" + progress);
        Intent intent = new Intent();
        intent.setAction(MusicCst.MUSIC_SEEK_TO_TIME);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }

    @Override
    public void onPlayModeChange(int playMode) {
        if (ibPlayQueue != null) {
            ibPlayQueue.setImageResource(MusicCst.PLAY_MODE_DRAWABLE[playMode]);
        }
    }

    @Override
    public void onCountChange(int count) {

    }

    @Override
    public void reLoad(List<MusicModel> list) {
        if (list.size() > 0) {
            MusicControl control = MusicService.getControl();
            control.reLoad(list);
            MusicModel model = control.getCurModel();
            resetFav(model != null ? model.isCollected : false);
        }
    }

    @Override
    public void setLrcRows(String path, List<LrcRow> lrcRows) {
        lrc.setLrcRows(lrcRows);
        if (lrcRows.size() > 0) {
            lrc.seekTo(control.getMediaPlayer().getCurrentPosition(), true);
        }
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
            if (isFinishing() || mPresenter == null || !mPresenter.isViewAttached()) {
                return;
            }
            String action = intent.getAction();
            if (action.equals(MusicCst.MUSIC_CURRENT_POSITION)) {
                int currentPosition = intent.getIntExtra("currentPosition", 0);
                int duration = intent.getIntExtra("duration", 0);
                tvTimeStart.setText(Util.formatTime(currentPosition));
                tvTimeEnd.setText(Util.formatTime(duration));
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
                lrc.seekTo(currentPosition, false);
            } else if (action.equals(MusicCst.MUSIC_CURRENT_INFO)) {
                MusicModel model = control.getCurModel();
                tvSongName.setText(model != null ? model.songName : "");
                resetFav(model != null ? model.isCollected : false);
                getLrc(model);
                if (model == null) {
                    togglePlay(false);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(PlayOrPauseEvent event) {
        togglePlay(event.isPlay);
    }

    private void togglePlay(boolean isPlay) {
        if (isPlay) {
            ibPlayPause.setImageResource(R.drawable.play_pause);
            rotationAnimator();
        } else {
            ibPlayPause.setImageResource(R.drawable.play_play);
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_stay, R.anim.push_bottom_out);
    }

    @Override
    protected void onDestroy() {
        if (isRegisterReceiver && playerReceiver != null) {
            unregisterReceiver(playerReceiver);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
