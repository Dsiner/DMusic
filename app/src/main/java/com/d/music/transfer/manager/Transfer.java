package com.d.music.transfer.manager;

import android.media.MediaScannerConnection;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.ProgressCallback;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.util.log.ULog;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.lib.taskscheduler.callback.Function;
import com.d.lib.taskscheduler.callback.Observer;
import com.d.lib.taskscheduler.callback.Task;
import com.d.lib.taskscheduler.schedule.Schedulers;
import com.d.music.App;
import com.d.music.component.aster.API;
import com.d.music.component.cache.base.ExpireLruCache;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.online.model.SongInfoRespModel;
import com.d.music.util.FileUtils;

import java.io.File;

/**
 * Transfer
 * Created by D on 2018/10/10.
 */
public class Transfer {
    public static final String PREFIX_SONG = ".mp3";
    public static final String PREFIX_MV = ".mp4";
    public static final String PREFIX_LRC = ".lrc";
    public static final String PREFIX_DOWNLOAD = ".download";

    public static <T extends MusicModel> void getInfo(@NonNull final T model, final SimpleCallback<T> callback) {
        getInfo(model.songId, new SimpleCallback<MusicModel>() {
            @Override
            public void onSuccess(@NonNull MusicModel response) {
                Cache.get().put(model.id, new Cache.Bean(response.songUrl, response.lrcUrl));
                model.songName = response.songName;
                model.songUrl = response.songUrl;
                model.artistId = response.artistId;
                model.artistName = response.artistName;
                model.albumId = "" + response.albumId;
                model.albumName = response.albumName;
                model.albumUrl = response.albumUrl;
                model.lrcUrl = response.lrcUrl;
                model.fileFolder = response.fileFolder;
                model.filePostfix = response.filePostfix;

                if (callback != null) {
                    callback.onSuccess(model);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    private static void getInfo(@NonNull final String songId, final SimpleCallback<MusicModel> callback) {
        Params params = new Params(API.SongInfo.rtpType);
        params.addParam(API.SongInfo.songIds, songId);
        Aster.get(API.SongInfo.rtpType, params)
                .request(new SimpleCallback<SongInfoRespModel>() {
                    @Override
                    public void onSuccess(SongInfoRespModel response) {
                        if (response.data == null || response.data.songList == null
                                || response.data.songList.size() <= 0) {
                            onError(new Exception("Data is empty!"));
                            return;
                        }
                        SongInfoRespModel.DataBean.SongListBean song = response.data.songList.get(0);
                        MusicModel model = new MusicModel();
                        model.songName = song.songName;
                        model.songUrl = song.songLink;
                        model.artistId = song.artistId;
                        model.artistName = song.artistName;
                        model.albumId = "" + song.albumId;
                        model.albumName = song.albumName;
                        model.albumUrl = song.songPicSmall;
                        model.lrcUrl = song.lrcLink;
                        model.fileFolder = Constants.Path.SONG;
                        model.filePostfix = song.format;

                        if (callback != null) {
                            callback.onSuccess(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void download(final T model, final boolean withLrc,
                                                       final OnTransferCallback<T> callback) {
        downloadFirstImp(false, model, withLrc, callback);
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void downloadCache(final T model, final boolean withLrc,
                                                            final OnTransferCallback<T> callback) {
        downloadFirstImp(true, model, withLrc, callback);
    }

    private static <T extends MusicModel> void downloadFirstImp(final boolean isCache, final T model, final boolean withLrc,
                                                                final OnTransferCallback<T> callback) {
        TaskScheduler.create(new Task<Boolean>() {
            @Override
            public Boolean run() {
                final Cache.Bean cache = Cache.get().get(model.id);
                if (cache != null && !TextUtils.isEmpty(cache.songUrl) && !TextUtils.isEmpty(cache.lrcUrl)) {
                    model.songUrl = cache.songUrl;
                    model.lrcUrl = cache.lrcUrl;
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.mainThread())
                .observeOn(Schedulers.mainThread())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (callback != null) {
                                callback.onFirst(model);
                            }
                            downloadSecondImp(isCache, model, withLrc, callback);
                        }
                        return aBoolean;
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        if (result) {
                            return;
                        }
                        getInfo(model, new SimpleCallback<T>() {
                            @Override
                            public void onSuccess(T response) {
                                if (callback != null) {
                                    callback.onFirst(response);
                                }
                                downloadSecondImp(isCache, model, withLrc, callback);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onError(model, e);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private static <T extends MusicModel> void downloadSecondImp(boolean isCache, T model, boolean withLrc,
                                                                 OnTransferCallback<T> callback) {
        // Download song
        if (isCache) {
            downloadSongSecondImp(Constants.Path.CACHE, model, callback);
        } else {
            downloadSongSecondImp(Constants.Path.SONG, model, callback);
        }

        if (withLrc) {
            // Download lrc
            if (isCache) {
                downloadLrcSecondImp(Constants.Path.CACHE, model, null);
            } else {
                downloadLrcSecondImp(Constants.Path.LYRIC, model, null);
            }
        }
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void downloadSong(final T model, final OnTransferCallback<T> callback) {
        downloadSongFirstImp(Constants.Path.SONG, model, callback);
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void downloadSongCache(final T model, final OnTransferCallback<T> callback) {
        downloadSongFirstImp(Constants.Path.CACHE, model, callback);
    }

    private static <T extends MusicModel> void downloadSongFirstImp(final String path, @NonNull final T model,
                                                                    final OnTransferCallback<T> callback) {
        TaskScheduler.create(new Task<Boolean>() {
            @Override
            public Boolean run() {
                final Cache.Bean cache = Cache.get().get(model.id);
                if (cache != null && !TextUtils.isEmpty(cache.songUrl)) {
                    model.songUrl = cache.songUrl;
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.mainThread())
                .observeOn(Schedulers.mainThread())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (callback != null) {
                                callback.onFirst(model);
                            }
                            downloadSongSecondImp(path, model, callback);
                        }
                        return aBoolean;
                    }
                })
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        if (result) {
                            return;
                        }
                        getInfo(model, new SimpleCallback<T>() {
                            @Override
                            public void onSuccess(T response) {
                                // Download song
                                downloadSongSecondImp(path, model, callback);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (callback != null) {
                                    callback.onError(model, e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(model, e);
                        }
                    }
                });
    }

    private static <T extends MusicModel> void downloadSongSecondImp(@NonNull final String path, @NonNull final T model,
                                                                     final OnTransferCallback<T> callback) {
        final String url = model.songUrl;
        final String name = model.songName + "." + model.filePostfix;
        final String cache = model.songName + "." + model.filePostfix + PREFIX_DOWNLOAD;
        Aster.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(TransferModel.generateId(model))
                .request(path, cache, new ProgressCallback() {

                    Speed speed = new Speed();

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgress download: " + currentLength + " total: " + totalLength);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_PROGRESS;
                            transferModel.transferCurrentLength = currentLength;
                            transferModel.transferTotalLength = totalLength;
                            transferModel.transferSpeed = speed.calculateSpeed(currentLength);
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onProgress(currentLength, totalLength);
                            }
                        }
                    }

                    @Override
                    public void onSuccess() {
                        ULog.d("dsiner_request--> onComplete");
                        FileUtils.renameFile(path + File.separator + cache,
                                path + File.separator + name);
                        File file = new File(path + File.separator + name);
                        if (file.exists()) {
                            MediaScannerConnection.scanFile(App.getContext(),
                                    new String[]{file.getAbsolutePath()}, null, null);
                        }

                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_DONE;
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onSuccess();
                            }
                        }
                        if (callback != null) {
                            callback.onSecond(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        FileUtils.deleteFile(path + File.separator + cache);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_ERROR;
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onError(e);
                            }
                        }
                        if (callback != null) {
                            callback.onError(model, e);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    public static <T extends MusicModel> void downloadMV(@NonNull final T model, final OnTransferCallback<T> callback) {
        final String path = Constants.Path.MV;
        final String url = model.songUrl;
        final String name = model.songName + PREFIX_MV;
        final String cache = model.songName + PREFIX_MV + PREFIX_DOWNLOAD;
        Aster.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .tag(TransferModel.generateId(model))
                .request(path, cache, new ProgressCallback() {
                    Speed speed = new Speed();

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgress download: " + currentLength + " total: " + totalLength);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_PROGRESS;
                            transferModel.transferCurrentLength = currentLength;
                            transferModel.transferTotalLength = totalLength;
                            transferModel.transferSpeed = speed.calculateSpeed(currentLength);
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onProgress(currentLength, totalLength);
                            }
                        }
                    }

                    @Override
                    public void onSuccess() {
                        ULog.d("dsiner_request--> onComplete");
                        FileUtils.renameFile(path + File.separator + cache,
                                path + File.separator + name);
                        File file = new File(path + File.separator + name);
                        if (file.exists()) {
                            MediaScannerConnection.scanFile(App.getContext(),
                                    new String[]{file.getAbsolutePath()}, null, null);
                        }

                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_DONE;
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onSuccess();
                            }
                        }
                        if (callback != null) {
                            callback.onSecond(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        FileUtils.deleteFile(path + File.separator + cache);
                        if (model instanceof TransferModel) {
                            TransferModel transferModel = (TransferModel) model;
                            transferModel.transferState = TransferModel.TRANSFER_STATE_ERROR;
                            if (transferModel.progressCallback != null) {
                                transferModel.progressCallback.onError(e);
                            }
                        }
                        if (callback != null) {
                            callback.onError(model, e);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void downloadLrc(@NonNull final T model,
                                                          final SimpleCallback<T> callback) {
        downloadLrcFirstImp(Constants.Path.LYRIC, model, callback);
    }

    @SuppressWarnings("unused")
    public static <T extends MusicModel> void downloadLrcCache(@NonNull final T model,
                                                               final SimpleCallback<T> callback) {
        downloadLrcFirstImp(Constants.Path.CACHE, model, callback);
    }

    private static <T extends MusicModel> void downloadLrcFirstImp(final String path, @NonNull final T model,
                                                                   final SimpleCallback<T> callback) {
        TaskScheduler.create(new Task<Boolean>() {
            @Override
            public Boolean run() {
                final Cache.Bean cache = Cache.get().get(model.id);
                if (cache != null && !TextUtils.isEmpty(cache.lrcUrl)) {
                    model.lrcUrl = cache.lrcUrl;
                    return true;
                }
                return false;
            }
        }).subscribeOn(Schedulers.mainThread())
                .observeOn(Schedulers.mainThread())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            downloadLrcSecondImp(path, model, callback);
                        }
                        return aBoolean;
                    }
                })
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean result) {
                        if (result) {
                            return;
                        }
                        getInfo(model, new SimpleCallback<T>() {
                            @Override
                            public void onSuccess(T response) {
                                // Download lrc
                                downloadLrcSecondImp(path, model, callback);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (callback != null) {
                                    callback.onError(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                });
    }

    private static <T extends MusicModel> void downloadLrcSecondImp(final String path, @NonNull final T model, final SimpleCallback<T> callback) {
        if (TextUtils.isEmpty(model.lrcUrl)) {
            if (callback != null) {
                callback.onError(new Exception("Lrc link is Empty!"));
            }
            return;
        }
        final String url = model.lrcUrl;
        final String name = model.songName + PREFIX_LRC;
        final String cache = model.songName + PREFIX_LRC + PREFIX_DOWNLOAD;
        Aster.download(url)
                .connectTimeout(60 * 1000)
                .readTimeout(60 * 1000)
                .writeTimeout(60 * 1000)
                .retryCount(3)
                .retryDelayMillis(1000)
                .request(path, cache, new ProgressCallback() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(long currentLength, long totalLength) {
                        ULog.d("dsiner_request--> onProgress --> download: " + currentLength + " total: " + totalLength);
                    }

                    @Override
                    public void onSuccess() {
                        ULog.d("dsiner_request--> onComplete");
                        FileUtils.renameFile(path + File.separator + cache,
                                path + File.separator + name);
                        if (callback != null) {
                            callback.onSuccess(model);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ULog.d("dsiner_request--> onError: " + e.getMessage());
                        FileUtils.deleteFile(path + File.separator + cache);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    public interface OnTransferCallback<T extends MusicModel> {
        void onFirst(T model);

        void onSecond(T model);

        void onError(T model, Throwable e);
    }

    public static class Cache {
        private ExpireLruCache<String, Bean> mLruCache;

        private Cache() {
            mLruCache = new ExpireLruCache<>(30, 2 * 60 * 60 * 1000);
        }

        static Cache get() {
            return Singleton.INSTANCE;
        }

        public void put(String key, Bean bean) {
            mLruCache.put(key, bean);
        }

        public Bean get(String key) {
            return mLruCache.get(key);
        }

        public void release() {
            mLruCache.clear();
        }

        private static class Singleton {
            private static final Cache INSTANCE = new Cache();
        }

        public static class Bean {
            String songUrl;
            String lrcUrl;

            Bean(String songUrl, String lrcUrl) {
                this.songUrl = songUrl;
                this.lrcUrl = lrcUrl;
            }
        }
    }

    public static class Speed {

        /**
         * K单位转换大小, 如 1K=1024 Byte
         */
        private static final int KB = 1024;

        /**
         * M单位转换大小, 如 1M = 1024*1024 Byte
         */
        private static final int MB = KB * KB;

        /**
         * G单位转换大小, 如 1G = 1024*1024*1024 Byte
         */
        private static final long GB = MB * KB;

        private static final int MIN_DELAY_TIME = 1000; // 两次进度更新间隔不能少于1000ms

        private float speed;
        private long currentLength;
        private long lastLength;
        private long lastTime;

        public static String formatSpeed(float speed) {
            if (speed <= 0) {
                return "0KB/S";
            } else if (speed < KB) {
                return String.format("%.0f B/S", speed);
            } else if (speed < MB) {
                return String.format("%.2f KB/S", speed / KB);
            } else if (speed < GB) {
                return String.format("%.2f MB/S", speed / MB);
            } else {
                return String.format("%.2f GB/S", speed / GB);
            }
        }

        public static String formatInfo(float currentLength, float totalLength) {
            return String.format("%.2fM/%.2fM", currentLength / MB, totalLength / MB);
        }

        public float calculateSpeed(long current) {
            currentLength = current;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= MIN_DELAY_TIME || lastTime == 0) {
                if (lastTime != 0 && currentTime - lastTime > 0 && currentLength - lastLength >= 0) {
                    speed = 1f * (currentLength - lastLength) / ((currentTime - lastTime) / 1000);
                } else {
                    speed = 0;
                }
                lastLength = currentLength;
                lastTime = currentTime;
            }
            return speed;
        }
    }
}
