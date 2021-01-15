package com.d.music.play.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.DateUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.dialog.AbsSheetDialog;
import com.d.music.App;
import com.d.music.R;
import com.d.music.component.cache.LrcCache;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.component.media.controler.MediaPlayerManager;
import com.d.music.component.operation.MoreOperator;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicInfoEvent;
import com.d.music.event.eventbus.ProgressEvent;
import com.d.music.play.adapter.PlayQueueAdapter;
import com.d.music.play.presenter.PlayPresenter;
import com.d.music.play.view.IPlayView;
import com.d.music.setting.activity.ModeActivity;
import com.d.music.widget.dialog.OperationDialog;
import com.d.music.widget.lrc.LrcRow;
import com.d.music.widget.lrc.LrcView;
import com.d.music.widget.popup.PlayQueuePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayActivity
 * Created by D on 2017/4/29.
 */
public class PlayActivity extends BaseActivity<PlayPresenter>
        implements IPlayView, View.OnClickListener, PlayQueueAdapter.IQueueListener {

    public static boolean sIsNeedReLoad; // 为了同步收藏状态，需要重新加载数据

    TextView tv_title;
    LrcView lrcv_lrc;
    ImageView iv_album;
    TextView tv_time_start;
    TextView tv_time_end;
    SeekBar sb_progress;

    ImageView iv_play_collect;
    ImageView iv_play_play_pause;
    ImageView iv_play_queue;

    private int mType = AppDatabase.MUSIC;
    private MediaControl mControl;
    private ObjectAnimator mAnimator;
    private PlayQueuePopup mQueuePopup;
    private boolean mProgressLock;

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (Constants.PlayerMode.sPlayerMode == Constants.PlayerMode.PLAYER_MODE_NORMAL) {
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(
                        R.anim.module_common_push_bottom_in,
                        R.anim.module_common_push_stay);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_more:
                if (mControl.list().size() <= 0) {
                    return;
                }
                showMore();
                break;

            case R.id.iv_play_collect:
                collect(false);
                break;

            case R.id.iv_play_prev:
                if (mControl.list().size() <= 0) {
                    return;
                }
                mControl.prev();
                break;

            case R.id.iv_play_play_pause:
                if (mControl.list().size() <= 0) {
                    return;
                }
                if (mControl.getStatus() == Constants.PlayStatus.PLAY_STATUS_PLAYING) {
                    mControl.pause();
                } else if (mControl.getStatus() == Constants.PlayStatus.PLAY_STATUS_PAUSE) {
                    mControl.start();
                }
                break;

            case R.id.iv_play_next:
                if (mControl.list().size() <= 0) {
                    return;
                }
                mControl.next();
                break;

            case R.id.iv_play_queue:
                if (mControl.list().size() <= 0) {
                    return;
                }
                showQueue();
                break;
        }
    }

    private void collect(boolean isTip) {
        if (mControl.getModel() == null) {
            return;
        }
        MusicModel item = mControl.getModel();
        MoreOperator.collect(getApplicationContext(), mType, item, isTip);
        resetFav(item.isCollected);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_play_activity_play;
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
    protected void bindView() {
        super.bindView();
        tv_title = findViewById(R.id.tv_title);
        lrcv_lrc = findViewById(R.id.lrcv_lrc);
        iv_album = findViewById(R.id.iv_album);
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_time_end = findViewById(R.id.tv_time_end);
        sb_progress = findViewById(R.id.sb_progress);

        iv_play_collect = findViewById(R.id.iv_play_collect);
        iv_play_play_pause = findViewById(R.id.iv_play_play_pause);
        iv_play_queue = findViewById(R.id.iv_play_queue);

        ViewHelper.setOnClickListener(this, this,
                R.id.iv_back, R.id.iv_more,
                R.id.iv_play_collect, R.id.iv_play_prev,
                R.id.iv_play_play_pause, R.id.iv_play_next,
                R.id.iv_play_queue);
    }

    @Override
    protected void init() {
        if (App.toFinish(getIntent())) {
            finish();
            return;
        }
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ff000000"));
        EventBus.getDefault().register(this);
        mControl = MediaControl.getInstance(this);
        initSeekBar();
        initLrc();
        initAlbum();
        onPlayModeChange(Preferences.getInstance(getApplicationContext()).getPlayMode());
    }

    private void initSeekBar() {
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                // 数值改变
                tv_time_start.setText(String.format("%02d:%02d", progress / 1000 / 60, progress / 1000 % 60));
                lrcv_lrc.seekTo(progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 开始拖动
                mProgressLock = true; // 加锁
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 停止拖动
                mProgressLock = false; // 解锁

                if (mControl.list().size() <= 0) {
                    return;
                }
                final int currentPosition = seekBar.getProgress();
                mControl.seekTo(currentPosition);

                lrcv_lrc.seekTo(seekBar.getProgress(), true);
            }
        });
    }

    private void initLrc() {
        lrcv_lrc.setOnSeekChangeListener(new LrcView.OnSeekChangeListener() {
            @Override
            public void onProgressChanged(int progress) {
                if (progress >= 0 && progress <= sb_progress.getMax()) {
                    sb_progress.setProgress(progress);
                }
                mControl.seekTo(progress);
            }
        });

        MediaPlayerManager mediaManager = mControl.getMediaManager();
        final int status = mControl.getStatus();
        if (mediaManager != null
                && (status == Constants.PlayStatus.PLAY_STATUS_PLAYING
                || status == Constants.PlayStatus.PLAY_STATUS_PAUSE)) {
            LrcCache.with(mContext).load(mControl.getModel())
                    .placeholder("")
                    .error("")
                    .into(lrcv_lrc);
        }
    }

    private void initAlbum() {
        tv_title.setText(mControl.getSongName());
        MusicModel model = mControl.getModel();
        resetFav(model != null ? model.isCollected : false);

        MediaPlayerManager mediaManager = mControl.getMediaManager();
        final int status = mControl.getStatus();
        if (mediaManager != null && (status == Constants.PlayStatus.PLAY_STATUS_PLAYING
                || status == Constants.PlayStatus.PLAY_STATUS_PAUSE)) {
            final int duration = mediaManager.getDuration();
            final int currentPosition = mediaManager.getCurrentPosition();
            setProgress(currentPosition, duration);
        } else {
            setProgress(0, 0);
        }

        if (status == Constants.PlayStatus.PLAY_STATUS_PLAYING) {
            // 正在播放
            iv_play_play_pause.setImageResource(R.drawable.module_play_ic_play_pause);
            rotationAnimator();
        } else {
            // 无列表播放/暂停
            iv_play_play_pause.setImageResource(R.drawable.module_play_ic_play_play);
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sIsNeedReLoad) {
            sIsNeedReLoad = false;
            mPresenter.overLoad();
        }
    }

    private void resetFav(boolean isCollected) {
        int fav = isCollected ? R.drawable.module_play_ic_play_fav_cover
                : R.drawable.module_play_ic_play_fav;
        iv_play_collect.setImageDrawable(getResources().getDrawable(fav));
    }

    private void showQueue() {
        if (mQueuePopup == null) {
            mQueuePopup = new PlayQueuePopup(mActivity);
            mQueuePopup.setOnQueueListener(this);
        }
        mQueuePopup.show();
    }

    @SuppressWarnings("unused")
    private void dismissQueue() {
        if (mQueuePopup != null) {
            mQueuePopup.dismiss();
        }
    }

    private void showMore() {
        final MusicModel item = mControl.getModel();
        final List<OperationDialog.Bean> datas = new ArrayList<>();
        datas.add(new OperationDialog.Bean().with(mContext,
                OperationDialog.Bean.TYPE_ADDLIST, true));
        datas.add(new OperationDialog.Bean().with(mContext,
                OperationDialog.Bean.TYPE_FAV, true)
                .item(item != null && item.isCollected
                        ? getResources().getString(R.string.module_common_collected)
                        : getResources().getString(R.string.module_common_collect)));
        datas.add(new OperationDialog.Bean().with(mContext,
                OperationDialog.Bean.TYPE_INFO, true));
        if (Constants.PlayerMode.sPlayerMode == Constants.PlayerMode.PLAYER_MODE_MINIMALIST) {
            datas.add(new OperationDialog.Bean(OperationDialog.Bean.TYPE_CHANGE_MODE,
                    getResources().getString(R.string.module_common_mode_switch),
                    R.drawable.module_common_ic_song_edit_m));
            datas.add(new OperationDialog.Bean(OperationDialog.Bean.TYPE_EXIT,
                    getResources().getString(R.string.module_common_exit),
                    R.drawable.module_setting_ic_menu_exit));
        }
        OperationDialog.getOperationDialog(mContext,
                OperationDialog.TYPE_NIGHT,
                "",
                datas,
                new AbsSheetDialog.OnItemClickListener<OperationDialog.Bean>() {
                    @Override
                    public void onClick(Dialog dlg, int position, OperationDialog.Bean bean) {
                        if (bean.type == OperationDialog.Bean.TYPE_ADDLIST) {
                            MoreOperator.addToList(mContext, mType, item);
                        } else if (bean.type == OperationDialog.Bean.TYPE_FAV) {
                            collect(true);
                        } else if (bean.type == OperationDialog.Bean.TYPE_INFO) {
                            if (item == null) {
                                return;
                            }
                            MoreOperator.showInfo(mContext, item);
                        } else if (bean.type == OperationDialog.Bean.TYPE_CHANGE_MODE) {
                            startActivity(new Intent(mContext, ModeActivity.class));
                        } else if (bean.type == OperationDialog.Bean.TYPE_EXIT) {
                            App.Companion.exit();
                        }
                    }

                    @Override
                    public void onCancel(Dialog dlg) {

                    }
                });
    }

    private void rotationAnimator() {
        mAnimator = ObjectAnimator.ofFloat(iv_album, "rotation", 0f, 360f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setDuration(5000);
        mAnimator.start();
        mAnimator.addListener(new AnimatorListenerAdapter() {
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
    public void onPlayModeChange(int playMode) {
        if (iv_play_queue != null) {
            iv_play_queue.setImageResource(Constants.PlayMode.PLAY_MODE_DRAWABLE[playMode]);
        }
    }

    @Override
    public void onCountChange(int count) {
        if (count <= 0) {
            setProgress(0, 0);
            lrcv_lrc.setLrcRows(new ArrayList<LrcRow>());
        }
    }

    @Override
    public void overLoad(List<MusicModel> list) {
        if (list.size() > 0) {
            MediaControl control = MediaControl.getInstance(mContext);
            control.overLoad(list);
            MusicModel model = control.list().get(control.getPosition());
            resetFav(model != null ? model.isCollected : false);
        }
    }

    private void setProgress(int currentPosition, int duration) {
        sb_progress.setMax(duration);
        sb_progress.setProgress(currentPosition);
        tv_time_start.setText(DateUtils.formatTime(currentPosition));
        tv_time_end.setText(DateUtils.formatTime(duration));
    }

    private void togglePlay(boolean isPlay) {
        if (isPlay) {
            iv_play_play_pause.setImageResource(R.drawable.module_play_ic_play_pause);
            rotationAnimator();
        } else {
            iv_play_play_pause.setImageResource(R.drawable.module_play_ic_play_play);
            if (mAnimator != null && mAnimator.isRunning()) {
                mAnimator.cancel();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEventMainThread(MusicInfoEvent event) {
        if (event == null || isFinishing()
                || mPresenter == null || !mPresenter.isViewAttached()) {
            return;
        }
        final MusicModel model = mControl.getModel();
        tv_title.setText(model != null ? model.songName : "");
        resetFav(model != null ? model.isCollected : false);
        LrcCache.with(mContext).load(model)
                .placeholder("")
                .error("")
                .into(lrcv_lrc);
        togglePlay(model != null && event.status == Constants.PlayStatus.PLAY_STATUS_PLAYING);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEventProgress(ProgressEvent event) {
        if (event == null || isFinishing()
                || mPresenter == null || !mPresenter.isViewAttached()) {
            return;
        }
        if (mProgressLock) {
            return;
        }
        final int currentPosition = event.currentPosition;
        final int duration = event.duration;
        setProgress(currentPosition, duration);
        lrcv_lrc.seekTo(currentPosition, false);
    }

    @Override
    public void finish() {
        super.finish();
        if (Constants.PlayerMode.sPlayerMode == Constants.PlayerMode.PLAYER_MODE_NORMAL) {
            overridePendingTransition(R.anim.module_common_push_stay,
                    R.anim.module_common_push_bottom_out);
        } else {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
