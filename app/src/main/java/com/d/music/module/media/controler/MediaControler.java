package com.d.music.module.media.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;

import com.d.lib.common.module.taskscheduler.TaskScheduler;
import com.d.music.common.Constants;
import com.d.music.common.preferences.Preferences;
import com.d.music.module.events.MusicInfoEvent;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;

import org.greenrobot.eventbus.EventBus;

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
public class MediaControler implements IMediaControler {
    @SuppressLint("StaticFieldLeak")
    private volatile static MediaControler instance;

    private Context mContext;
    private Preferences mPreferences;
    private MediaPlayerManager mMediaPlayerManager;
    private MediaPresenter mPresenter;
    private List<MusicModel> mDatas = new ArrayList<>();
    private int mPosition;
    private int mStatus;

    private MediaControler(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = Preferences.getIns(mContext);
        mMediaPlayerManager = MediaPlayerManager.getIns();
        mPresenter = new MediaPresenter(mContext);
        mPresenter.attachView(this);
    }

    public static MediaControler getIns(Context context) {
        if (instance == null) {
            synchronized (MediaControler.class) {
                if (instance == null) {
                    instance = new MediaControler(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void init(@android.support.annotation.NonNull final List<MusicModel> datas,
                     final int position, final boolean play) {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = new ArrayList<>(datas);
                AppDBUtil.getIns(mContext).optMusic().deleteAll(AppDB.MUSIC);
                AppDBUtil.getIns(mContext).optMusic().insertOrReplaceInTx(AppDB.MUSIC, list);
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

    public void overLoad(@android.support.annotation.NonNull List<MusicModel> list) {
        mDatas.clear();
        mDatas.addAll(list);
        mPosition = (mPosition >= 0 && mPosition < mDatas.size()) ? mPosition : 0;
    }

    @android.support.annotation.NonNull
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
        mMediaPlayerManager.seekTo(msec);
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
        if (mDatas.get(mPosition).type != MusicModel.TYPE_LOCAL) {
            mMediaPlayerManager.reset();
            mPresenter.play(mDatas.get(mPosition), next);
        } else {
            play(mDatas.get(mPosition).url, next);
        }
        sendBroadcast();
        // 保存当前播放位置
        mPreferences.putLastPlayPosition(mPosition);
    }

    public void play(final String url, final boolean next) {
        mMediaPlayerManager.play(url, new MediaPlayerManager.OnMediaPlayerListener() {
            @Override
            public void onLoading(MediaPlayer mp, String url) {

            }

            @Override
            public void onPrepared(MediaPlayer mp, String url) {

            }

            @Override
            public void onError(MediaPlayer mp, String url) {
                if (next) {
                    autoNext();
                } else {
                    prev();
                }
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
    }

    @Override
    public void start() {
        mStatus = Constants.PlayStatus.PLAY_STATUS_PLAYING;
        mMediaPlayerManager.start();
        sendBroadcast();
    }

    @Override
    public void pause() {
        mStatus = Constants.PlayStatus.PLAY_STATUS_PAUSE;
        mMediaPlayerManager.pause();
        sendBroadcast();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayerManager.isPlaying();
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
        mMediaPlayerManager.stop();
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
            final boolean isPlaying = mMediaPlayerManager.isPlaying();
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
                AppDBUtil.getIns(mContext).optMusic().deleteAll(AppDB.MUSIC);
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
        return mMediaPlayerManager;
    }

    private int calculate(int position) {
        if (position < 0 || position >= mDatas.size()) {
            return mDatas.size();
        }
        MusicModel delete = mDatas.get(position);
        mDatas.remove(position);
        AppDBUtil.getIns(mContext).optMusic().delete(AppDB.MUSIC, delete);
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
        stop();
        reset();
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void closeLoading() {

    }
}
