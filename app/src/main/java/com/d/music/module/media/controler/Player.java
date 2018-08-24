package com.d.music.module.media.controler;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.DownloadCallBack;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.music.R;
import com.d.music.api.API;
import com.d.music.common.Constants;
import com.d.music.module.events.MusicInfoEvent;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.media.HitTarget;
import com.d.music.online.model.SongInfoRespModel;
import com.d.music.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Player
 * Created by D on 2018/8/22.
 */
public class Player {
    public final static int STATE_PLAY = 0;
    public final static int STATE_START = 1;
    public final static int STATE_PAUSE = 2;
    public final static int STATE_SEEKTO = 3;
    public final static int STATE_PREV = 4;
    public final static int STATE_NEXT = 5;
    public final static int STATE_STOP = 6;

    private Context mContext;
    private List<Action> mActions = new ArrayList<>();
    private MediaPlayerManager mMediaPlayerManager;
    private Presenter mPresenter;
    private MediaPlayerManager.OnMediaPlayerListener mListener;

    public boolean isPlaying() {
        return mMediaPlayerManager.isPlaying();
    }

    public MediaPlayerManager getMediaManager() {
        return mMediaPlayerManager;
    }

    public static class Action {
        public int action;
        public MusicModel model;
        public int msec;

        public Action(int action) {
            this.action = action;
        }

        public Action(int action, MusicModel model) {
            this.action = action;
            this.model = model;
        }

        public Action(int action, int msec) {
            this.action = action;
            this.msec = msec;
        }
    }

    public Player(Context context) {
        this.mContext = context.getApplicationContext();
        this.mMediaPlayerManager = MediaPlayerManager.getIns();
        this.mPresenter = new Presenter(context.getApplicationContext(), this);
    }

    @UiThread
    public void excute(final Action action) {
        push(action);
        if (mActions.size() > 1) {
            return;
        }
        switch (action.action) {
            case STATE_PLAY:
                play(action.model, true);
                break;
            case STATE_START:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.start();
                break;
            case STATE_PAUSE:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.pause();
                break;
            case STATE_SEEKTO:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.seekTo(action.msec);
                break;
            case STATE_PREV:
                play(action.model, false);
                break;
            case STATE_NEXT:
                play(action.model, true);
                break;
            case STATE_STOP:
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.stop();
                break;
        }
    }

    @UiThread
    private void play(final MusicModel model, final boolean next) {
        mMediaPlayerManager.reset();
        if (model.type == MusicModel.TYPE_LOCAL) {
            playImpl(model.url, next);
        } else if (model.type == MusicModel.TYPE_BAIDU) {
            mPresenter.getBaidu(model, next);
        }
    }

    @UiThread
    private void playImpl(final String url, final boolean next) {
        mMediaPlayerManager.play(url, new MediaPlayerManager.OnMediaPlayerListener() {
            @Override
            public void onLoading(MediaPlayer mp, String url) {
                if (mActions.size() > 1) {
                    return;
                }
                if (mListener != null) {
                    mListener.onLoading(mMediaPlayerManager.getMediaPlayer(), url);
                }
            }

            @Override
            public void onPrepared(MediaPlayer mp, String url) {
                if (pop()) {
                    return;
                }
                mMediaPlayerManager.start();
                if (mListener != null) {
                    mListener.onPrepared(mp, url);
                }
            }

            @Override
            public void onError(MediaPlayer mp, String url) {
                if (pop()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onError(mp, url);
                }
            }

            @Override
            public void onCompletion(MediaPlayer mp, String url) {
                if (mListener != null) {
                    mListener.onCompletion(mp, url);
                }
            }

            @Override
            public void onCancel(MediaPlayer mp) {
                if (mListener != null) {
                    mListener.onCancel(mp);
                }
            }
        });
    }

    @UiThread
    private void push(Action action) {
        Action ac = mActions.size() > 1 ? mActions.get(0) : null;
        mActions.clear();
        if (ac != null) {
            mActions.add(ac);
        }
        mActions.add(action);
    }

    @UiThread
    private boolean pop() {
        Action ac = mActions.size() > 1 ? mActions.get(mActions.size() - 1) : null;
        mActions.clear();
        if (ac != null) {
            mActions.add(ac);
            excute(ac);
            return true;
        }
        return false;
    }

    public void setOnMediaPlayerListener(MediaPlayerManager.OnMediaPlayerListener l) {
        mListener = l;
    }

    public static class Presenter {
        private Context mContext;
        private WeakReference<Player> mViewRef;

        @UiThread
        @Nullable
        public Player getView() {
            return mViewRef == null ? null : mViewRef.get();
        }

        Presenter(Context context, Player player) {
            this.mContext = context.getApplicationContext();
            this.mViewRef = new WeakReference<>(player);
        }

        private void getBaidu(@NonNull final MusicModel model, final boolean next) {
            if (getView() == null) {
                return;
            }
            String path = HitTarget.hitSong(model);
            if (FileUtil.isFileExist(path)) {
                getView().playImpl(path, next);
                return;
            }

            Params params = new Params(API.SongInfo.rtpType);
            params.addParam(API.SongInfo.songIds, model.songId);
            RxNet.get(API.SongInfo.rtpType, params)
                    .request(new SimpleCallBack<SongInfoRespModel>() {
                        @Override
                        public void onSuccess(SongInfoRespModel response) {
                            if (getView() == null) {
                                return;
                            }
                            if (response.data == null || response.data.songList == null
                                    || response.data.songList.size() <= 0) {
                                onError(new Exception("Get baidu request error."));
                                return;
                            }
                            SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                            getView().playImpl(song.songLink, next);
                            // Download song
                            downloadSong(model, song.songLink, Constants.Path.cache, song.songName + "." + song.format);
                            // Download lrc
                            downloadLrc(model, song.lrcLink, Constants.Path.lyric, song.songName + ".lrc");
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (getView() == null) {
                                return;
                            }
                            if (getView().pop()) {
                                return;
                            }
                            if (getView().mListener != null) {
                                getView().mListener.onError(getView().mMediaPlayerManager.getMediaPlayer(), "");
                            }
                            Util.toast(mContext, mContext.getResources().getString(R.string.lib_pub_net_error));
                        }
                    });
        }

        private void downloadSong(final MusicModel model, final String url, final String path, final String name) {
            if (FileUtil.isFileExist(path + name)) {
                return;
            }
            RxNet.download(url)
                    .connectTimeout(60 * 1000)
                    .readTimeout(60 * 1000)
                    .writeTimeout(60 * 1000)
                    .retryCount(3)
                    .retryDelayMillis(1000)
                    .tag(path + name)
                    .request(path, name, new DownloadCallBack() {

                        @Override
                        public void onProgress(long currentLength, long totalLength) {
                            ULog.d("dsiner_request onProgresss: --> download: " + currentLength + " total: " + totalLength);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ULog.d("dsiner_request onError " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            ULog.d("dsiner_request onComplete:");
                        }
                    });
        }

        private void downloadLrc(final MusicModel model, final String url, final String path, final String name) {
            if (FileUtil.isFileExist(path + name)) {
                return;
            }
            RxNet.download(url)
                    .connectTimeout(60 * 1000)
                    .readTimeout(60 * 1000)
                    .writeTimeout(60 * 1000)
                    .retryCount(3)
                    .retryDelayMillis(1000)
                    .tag(path + name)
                    .request(path, name, new DownloadCallBack() {

                        @Override
                        public void onProgress(long currentLength, long totalLength) {
                            ULog.d("dsiner_request onProgresss: --> download: " + currentLength + " total: " + totalLength);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ULog.d("dsiner_request onError " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            ULog.d("dsiner_request onComplete:");
                            model.lrcUrl = path + name;
                            MusicInfoEvent event = new MusicInfoEvent();
                            event.type = MusicInfoEvent.TYPE_LRC;
                            EventBus.getDefault().post(event);
                        }
                    });
        }
    }
}
