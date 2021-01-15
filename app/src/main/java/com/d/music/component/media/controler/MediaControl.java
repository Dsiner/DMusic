package com.d.music.component.media.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.d.lib.common.util.ToastUtils;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.R;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicInfoEvent;
import com.d.music.event.eventbus.ProgressEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * MediaControler
 * Created by D on 2017/4/29.
 */
public class MediaControl implements IMediaControl {
    @SuppressLint("StaticFieldLeak")
    private volatile static MediaControl instance;

    private Context mContext;
    private Preferences mPreferences;
    private Player mPlayer;
    private List<MusicModel> mDatas = new ArrayList<>();
    private int mPosition;
    private int mStatus;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ProgressRunable mProgressTask;
    private ProgressEvent mProgressEvent = new ProgressEvent();

    private MediaControl(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = Preferences.getInstance(mContext);
        mPlayer = new Player(mContext);
        mProgressTask = new ProgressRunable(this);
        restartProgressTask();
    }

    public static MediaControl getInstance(Context context) {
        if (instance == null) {
            synchronized (MediaControl.class) {
                if (instance == null) {
                    instance = new MediaControl(context);
                }
            }
        }
        return instance;
    }

    private void restartProgressTask() {
        stopProgressTask();
        mHandler.postDelayed(mProgressTask, 1000);
    }

    private void stopProgressTask() {
        mHandler.removeCallbacks(mProgressTask);
    }

    @Override
    public void init(@androidx.annotation.NonNull final List<MusicModel> datas,
                     final int position, final boolean play) {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = new ArrayList<>(datas);
                DBManager.getInstance(mContext).optMusic().deleteAll(AppDatabase.MUSIC);
                DBManager.getInstance(mContext).optMusic().insertOrReplaceInTx(AppDatabase.MUSIC, list);
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MusicModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MusicModel> list) {
                        mDatas.clear();
                        mDatas.addAll(list);
                        mPosition = (position >= 0 && position < mDatas.size()) ? position : 0;
                        if (play) {
                            play(true);
                        } else {
                            sendBroadcast();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void overLoad(@androidx.annotation.NonNull List<MusicModel> list) {
        mDatas.clear();
        mDatas.addAll(list);
        mPosition = (mPosition >= 0 && mPosition < mDatas.size()) ? mPosition : 0;
    }

    public void cut(MusicModel item) {
        if (item == null) {
            return;
        }
        List<MusicModel> list = new ArrayList<>();
        list.add(item);
        list.addAll(mDatas);
        init(list, 0, true);
    }

    @androidx.annotation.NonNull
    @Override
    public List<MusicModel> list() {
        return mDatas;
    }

    @Nullable
    @Override
    public MusicModel getModel() {
        if (mDatas.size() <= 0) {
            return null;
        }
        return mDatas.get(mPosition);
    }

    @Override
    public void seekTo(int msec) {
        mPlayer.excute(new Player.Action(Player.STATE_SEEKTO, msec));
    }

    @Override
    public void play(int position) {
        if (position >= 0 && position < mDatas.size()) {
            mPosition = position;
            play(true);
        }
    }

    private void play(final boolean next) {
        if (isEmpty()) {
            return;
        }
        mStatus = Constants.PlayStatus.PLAY_STATUS_PLAYING;
        mPlayer.setOnMediaPlayerListener(new MediaPlayerManager.OnMediaPlayerListener() {
            @Override
            public void onLoading(MediaPlayer mp, String url) {

            }

            @Override
            public void onPrepared(MediaPlayer mp, String url) {

            }

            @Override
            public void onError(MediaPlayer mp, String url) {
                ToastUtils.toast(mContext, mContext.getResources().getString(R.string.module_common_playback_failed));
            }

            @Override
            public void onCompletion(MediaPlayer mp, String url) {
                // 自动下一首
                autoNext();
            }

            @Override
            public void onCancel(MediaPlayer mp) {

            }
        });
        mPlayer.excute(new Player.Action(Player.STATE_PLAY, mDatas.get(mPosition)));
        sendBroadcast();
        // 保存当前播放位置
        mPreferences.putLastPlayPosition(mPosition);
    }

    @Override
    public void start() {
        mStatus = Constants.PlayStatus.PLAY_STATUS_PLAYING;
        mPlayer.excute(new Player.Action(Player.STATE_START));
        sendBroadcast();
    }

    @Override
    public void pause() {
        mStatus = Constants.PlayStatus.PLAY_STATUS_PAUSE;
        mPlayer.excute(new Player.Action(Player.STATE_PAUSE));
        sendBroadcast();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void prev() {
        if (isEmpty()) {
            return;
        }
        switch (mPreferences.getPlayMode()) {
            case Constants.PlayMode.PLAY_MODE_ALL_REPEAT:
            case Constants.PlayMode.PLAY_MODE_ORDER:
            case Constants.PlayMode.PLAY_MODE_SINGLE_CYCLE:
                // 列表循环、顺序播放、单曲循环
                if (--mPosition < 0) {
                    mPosition = mDatas.size() - 1;
                }
                break;
            case Constants.PlayMode.PLAY_MODE_SHUFFLE:
                // 随机播放
                Random random = new Random(System.currentTimeMillis());
                mPosition = Math.abs(random.nextInt()) % mDatas.size();
                break;
        }
        play(false);
    }

    @Override
    public void next() {
        if (isEmpty()) {
            return;
        }
        switch (mPreferences.getPlayMode()) {
            case Constants.PlayMode.PLAY_MODE_ALL_REPEAT:
            case Constants.PlayMode.PLAY_MODE_ORDER:
            case Constants.PlayMode.PLAY_MODE_SINGLE_CYCLE:
                // 列表循环、顺序播放、单曲循环
                if (++mPosition >= mDatas.size()) {
                    mPosition = 0;
                }
                break;
            case Constants.PlayMode.PLAY_MODE_SHUFFLE:
                // 随机播放
                Random random = new Random(System.currentTimeMillis());
                mPosition = Math.abs(random.nextInt()) % mDatas.size();
                break;
            default:
                break;
        }
        play(true);
    }

    @Override
    public void autoNext() {
        if (isEmpty()) {
            return;
        }
        switch (mPreferences.getPlayMode()) {
            case Constants.PlayMode.PLAY_MODE_ALL_REPEAT:
                if (++mPosition >= mDatas.size()) {
                    mPosition = 0;
                }
                play(true);
                break;
            case Constants.PlayMode.PLAY_MODE_ORDER:
                if (++mPosition >= mDatas.size()) {
                    mPosition--;
                    stop();
                } else {
                    play(true);
                }
                break;
            case Constants.PlayMode.PLAY_MODE_SHUFFLE:
                Random random = new Random(System.currentTimeMillis());
                mPosition = Math.abs(random.nextInt()) % mDatas.size();
                play(true);
                break;
            case Constants.PlayMode.PLAY_MODE_SINGLE_CYCLE:
                play(true);
                break;
        }
    }

    @Override
    public void stop() {
        mStatus = Constants.PlayStatus.PLAY_STATUS_STOP;
        mPlayer.excute(new Player.Action(Player.STATE_STOP));
    }

    @Override
    public void delete(final int position) {
        int count = mDatas.size();
        if (position < 0 || position >= count) {
            return;
        }
        if (position > mPosition) {
            calculate(position);
        } else if (position < mPosition) {
            calculate(position);
            mPosition--;
        } else if (position == mPosition) {
            final boolean isPlaying = isPlaying();
            stop();
            count = calculate(position);
            if (isEmpty()) {
                return;
            }
            if (position > count - 1) {
                mPosition = 0;
            }
            if (isPlaying) {
                play(true);
            }
            sendBroadcast();
        }
    }

    @Override
    public void deleteAll() {
        stop();
        reset();
        sendBroadcast();
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance(mContext).optMusic().deleteAll(AppDatabase.MUSIC);
            }
        });
    }

    public int getPosition() {
        return mPosition;
    }

    public String getSongName() {
        return mDatas.size() > 0 ? mDatas.get(mPosition).songName : "";
    }

    public String getArtistName() {
        return mDatas.size() > 0 ? mDatas.get(mPosition).artistName : "";
    }

    public int getStatus() {
        return mStatus;
    }

    public MediaPlayerManager getMediaManager() {
        return mPlayer.getMediaManager();
    }

    private int calculate(int position) {
        if (position < 0 || position >= mDatas.size()) {
            return mDatas.size();
        }
        MusicModel delete = mDatas.get(position);
        mDatas.remove(position);
        DBManager.getInstance(mContext).optMusic().delete(AppDatabase.MUSIC, delete);
        return mDatas.size();
    }

    private void sendBroadcast() {
        MusicInfoEvent event = new MusicInfoEvent();
        event.songName = getSongName();
        event.artistName = getArtistName();
        event.status = getStatus();
        event.isUpdateNotif = true;
        EventBus.getDefault().post(event);
    }

    private boolean isEmpty() {
        if (mDatas.size() <= 0) {
            mStatus = Constants.PlayStatus.PLAY_STATUS_STOP;
            stop();
            reset();
            sendBroadcast();
            return true;
        }
        return false;
    }

    private void reset() {
        mPosition = 0;
        mDatas.clear();
    }

    public void onDestroy() {
        stopProgressTask();
        stop();
        reset();
    }

    static class ProgressRunable implements Runnable {
        WeakReference<MediaControl> weakRef;

        ProgressRunable(MediaControl mediaControler) {
            weakRef = new WeakReference<>(mediaControler);
        }

        @Override
        public void run() {
            if (isDestroyed()) {
                return;
            }
            MediaControl theControler = weakRef.get();
            MediaPlayerManager mediaManager = theControler.getMediaManager();
            if (mediaManager != null && theControler.getStatus() == Constants.PlayStatus.PLAY_STATUS_PLAYING) {
                // 获取当前音乐播放的位置
                theControler.mProgressEvent.currentPosition = mediaManager.getCurrentPosition();
                // 获取当前音乐播放总时间
                theControler.mProgressEvent.duration = mediaManager.getDuration();
                EventBus.getDefault().post(theControler.mProgressEvent);
            }
            theControler.restartProgressTask();
        }

        boolean isDestroyed() {
            return weakRef == null || weakRef.get() == null;
        }
    }
}
