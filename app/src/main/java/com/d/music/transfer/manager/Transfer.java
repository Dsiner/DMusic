package com.d.music.transfer.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.common.utils.log.ULog;
import com.d.lib.rxnet.RxNet;
import com.d.lib.rxnet.base.ApiManager;
import com.d.lib.rxnet.base.Params;
import com.d.lib.rxnet.callback.DownloadCallback;
import com.d.lib.rxnet.callback.SimpleCallback;
import com.d.music.api.API;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.online.model.SongInfoRespModel;
import com.d.music.utils.FileUtil;

/**
 * Transfer
 * Created by D on 2018/10/10.
 */
public class Transfer {
    public final static String PREFIX_SONG = ".mp3";
    public final static String PREFIX_MV = ".mp4";
    public final static String PREFIX_LRC = ".lrc";
    public final static String PREFIX_DOWNLOAD = ".download";

    public static <T extends MusicModel> void getInfo(final T model, final SimpleCallback<T> callback) {
        Params params = new Params(API.SongInfo.rtpType);
        params.addParam(API.SongInfo.songIds, model.songId);
        RxNet.get(API.SongInfo.rtpType, params)
                .tag(model.type + model.songId)
                .request(new SimpleCallback<SongInfoRespModel>() {
                    @Override
                    public void onSuccess(SongInfoRespModel response) {
                        ApiManager.get().remove(model.type + model.songId);
                        if (response.data == null || response.data.songList == null
                                || response.data.songList.size() <= 0) {
                            onError(new Exception("Data is empty!"));
                            return;
                        }
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                        model.url = song.songLink;
                        model.lrcUrl = song.lrcLink;
                        model.songName = song.songName;
                        model.artistId = song.artistId;
                        model.artistName = song.artistName;
                        model.albumId = "" + song.albumId;
                        model.albumName = song.albumName;
                        model.albumUrl = song.songPicSmall;
                        model.fileFolder = Constants.Path.song;
                        model.filePostfix = song.format;

                        if (callback != null) {
                            callback.onSuccess(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ApiManager.get().remove(model.type + model.songId);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
    }

    public static <T extends MusicModel> void download(final T model, final boolean withLrc,
                                                       final OnTransferCallback<T> callback) {
        download(false, model, withLrc, callback);
    }

    public static <T extends MusicModel> void downloadCache(final T model, final boolean withLrc,
                                                            final OnTransferCallback<T> callback) {
        download(true, model, withLrc, callback);
    }

    private static <T extends MusicModel> void download(final boolean isCache, final T model, final boolean withLrc,
                                                        final OnTransferCallback<T> callback) {
        getInfo(model, new SimpleCallback<T>() {
            @Override
            public void onSuccess(T response) {
                if (callback != null) {
                    callback.onFirst(response);
                }
                // Download song
                if (isCache) {
                    downloadSongCache(model, callback);
                } else {
                    downloadSong(model, callback);
                }

                if (withLrc) {
                    // Download lrc
                    if (isCache) {
                        downloadLrcCache(model, null);
                    } else {
                        downloadLrc(model, null);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(model, e);
            }
        });
    }

    private static <T extends MusicModel> void downloadSong(final T model, final OnTransferCallback<T> callback) {
        downloadSong(Constants.Path.song, model, callback);
    }

    private static <T extends MusicModel> void downloadSongCache(final T model, final OnTransferCallback<T> callback) {
        downloadSong(Constants.Path.cache, model, callback);
    }

    private static <T extends MusicModel> void downloadSong(final String path, final T model, final OnTransferCallback<T> callback) {
        final String url = model.url;
        final String name = model.songName + "." + model.filePostfix;
        final String cache = model.songName + "." + model.filePostfix + PREFIX_DOWNLOAD;
        RxNet.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(model.type + model.songId)
                .request(path, cache, new DownloadCallback() {

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgresss download: " + currentLength + " total: " + totalLength);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_PROGRESS;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onProgress(currentLength, totalLength);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        ApiManager.get().remove(model.type + model.songId);
                        FileUtil.deleteFile(path + cache);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_ERROR;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onError(e);
                            }
                        }
                        if (callback != null) {
                            callback.onError(model, e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        ULog.d("dsiner_request--> onComplete");
                        ApiManager.get().remove(model.type + model.songId);
                        FileUtil.renameFile(path + cache, path + name);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_DONE;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onComplete();
                            }
                        }
                        if (callback != null) {
                            callback.onSecond(model);
                        }
                    }
                });
    }

    public static <T extends MusicModel> void downloadMV(final T model, final OnTransferCallback<T> callback) {
        final String path = Constants.Path.mv;
        final String url = model.url;
        final String name = model.songName + PREFIX_MV;
        final String cache = model.songName + PREFIX_MV + PREFIX_DOWNLOAD;
        RxNet.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(model.type + model.songId)
                .request(path, cache, new DownloadCallback() {

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgresss download: " + currentLength + " total: " + totalLength);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_PROGRESS;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onProgress(currentLength, totalLength);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        ApiManager.get().remove(model.type + model.songId);
                        FileUtil.deleteFile(path + cache);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_ERROR;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onError(e);
                            }
                        }
                        if (callback != null) {
                            callback.onError(model, e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        ULog.d("dsiner_request--> onComplete");
                        ApiManager.get().remove(model.type + model.songId);
                        FileUtil.renameFile(path + cache, path + name);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.state = TransferModel.STATE_DONE;
                            if (transferModel.downloadCallback != null) {
                                transferModel.downloadCallback.onComplete();
                            }
                        }
                        if (callback != null) {
                            callback.onSecond(model);
                        }
                    }
                });
    }

    private static <T extends MusicModel> void downloadLrc(@NonNull final T model, final SimpleCallback<T> callback) {
        downloadLrc(Constants.Path.lyric, model, callback);
    }

    public static <T extends MusicModel> void downloadLrcCache(@NonNull final T model, final SimpleCallback<T> callback) {
        downloadLrc(Constants.Path.cache, model, callback);
    }

    private static <T extends MusicModel> void downloadLrc(final String path, @NonNull final T model, final SimpleCallback<T> callback) {
        if (TextUtils.isEmpty(model.lrcUrl)) {
            getInfo(model, new SimpleCallback<T>() {
                @Override
                public void onSuccess(T response) {
                    // Download lrc
                    downloadLrc(path, model, callback);
                }

                @Override
                public void onError(Throwable e) {

                }
            });
            return;
        }
        final String url = model.lrcUrl;
        final String name = model.songName + PREFIX_LRC;
        final String cache = model.songName + PREFIX_LRC + PREFIX_DOWNLOAD;
        RxNet.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .request(path, cache, new DownloadCallback() {

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgresss --> download: " + currentLength + " total: " + totalLength);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        FileUtil.deleteFile(path + cache);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        ULog.d("dsiner_request--> onComplete");
                        FileUtil.renameFile(path + cache, path + name);
                        if (callback != null) {
                            callback.onSuccess(model);
                        }
                    }
                });
    }

    public interface OnTransferCallback<T extends MusicModel> {
        void onFirst(T model);

        void onSecond(T model);

        void onError(T model, Throwable e);
    }
}
