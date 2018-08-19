package com.d.music.module.media.controler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.listener.DownloadCallBack;
import com.d.lib.rxnet.listener.SimpleCallBack;
import com.d.music.R;
import com.d.music.api.API;
import com.d.music.common.Constants;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.online.model.SongInfoRespModel;

/**
 * MediaPresenter
 * Created by D on 2018/8/19.
 */
class MediaPresenter extends MvpBasePresenter<IMediaControler> {

    MediaPresenter(Context context) {
        super(context);
    }

    void play(@NonNull MusicModel model, boolean next) {
        if (model.type == MusicModel.TYPE_BAIDU) {
            playBaidu(model, next);
        }
    }

    private void playBaidu(@NonNull final MusicModel model, final boolean next) {
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
                            onError(new Exception("error"));
                            return;
                        }
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                        if (getView().getModel() != null && getView().getModel() == model) {
                            model.url = song.songLink;
                            download1(model);
                            getView().play(model.url, next);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        Util.toast(mContext, mContext.getResources().getString(R.string.lib_pub_net_error));
                    }
                });
    }

    private void download1(final MusicModel model) {
        Params params = new Params(API.SongInfo.rtpType);
        params.addParam(API.SongInfo.songIds, model.songId);
        RxNet.get(API.SongInfo.rtpType, params)
                .request(new SimpleCallBack<SongInfoRespModel>() {
                    @Override
                    public void onSuccess(SongInfoRespModel response) {
                        if (response.data == null || response.data.songList == null
                                || response.data.songList.size() <= 0
                                || response.data.songList.get(0) == null) {
                            return;
                        }
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                        // Download song
                        downloadSong(model, song.songLink, Constants.Path.song, song.songName + "." + song.format);
                        // Download lrc
                        downloadLrc(model, song.lrcLink, Constants.Path.lyric, song.songName + ".lrc");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void downloadSong(final MusicModel model, final String url, final String path, final String name) {
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
                    }
                });
    }
}
