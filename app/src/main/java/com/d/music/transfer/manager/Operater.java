package com.d.music.transfer.manager;

import android.support.annotation.NonNull;

import com.d.lib.common.utils.log.ULog;
import com.d.lib.rxnet.base.ApiManager;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.pipe.Pipe;

import java.util.List;

/**
 * Operater
 * Created by D on 2018/10/10.
 */
public class Operater {
    private int mLimit;
    private Pipe mPipe;

    public Operater(@NonNull Pipe pipe) {
        this.mPipe = pipe;
    }

    public void setLimit(int limit) {
        this.mLimit = limit;
    }

    @NonNull
    public Pipe pipe() {
        return mPipe;
    }

    public void add(MusicModel item) {
        mPipe.add(item);
        next();
    }

    public void start(TransferModel model) {
        List<TransferModel> list = mPipe.mDownloadingQueue;
        if (list.size() - 1 > 0) {
            pause(list.get(list.size() - 1));
        }
        startImpl(model);
    }

    public void pause(TransferModel model) {
        mPipe.pop(model);
        model.state = TransferModel.STATE_PENDDING;
        ApiManager.get().cancel(model.type + model.songId);
    }

    public void pauseAll() {
        List<TransferModel> list = mPipe.mDownloadingQueue;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            pause(list.get(i));
        }
        mPipe.notifyDataSetChanged();
    }

    public void clear(int type) {
        if (type == 0) {
            pauseAll();
            mPipe.mDownloadingQueue.clear();
            mPipe.mDownloading.clear();
        } else if (type == 1) {
            mPipe.mDownloaded.clear();
        }
        mPipe.notifyDataSetChanged();
    }

    private void next() {
        mPipe.notifyDataSetChanged();
        List<TransferModel> list = mPipe.peek();
        int size = list.size();
        if (size <= 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            startImpl(list.get(i));
        }
    }

    private void startImpl(final TransferModel item) {
        item.state = TransferModel.STATE_PROGRESS;
        mPipe.push(item);
        if (item.type == TransferModel.TYPE_TRANSFER_MV) {
            startMVImpl(item);
        } else {
            startSongImpl(item);
        }
    }

    private void startSongImpl(final TransferModel item) {
        if (HitTarget.secondPassSong(item)) {
            ULog.d("dsiner_request--> Second pass");
            item.state = TransferModel.STATE_DONE;
            if (item.downloadCallback != null) {
                item.downloadCallback.onComplete();
            }
            next(item, TransferModel.STATE_DONE);
            return;
        }

        Transfer.download(item, true, new Transfer.OnTransferCallback<TransferModel>() {
            @Override
            public void onFirst(TransferModel model) {

            }

            @Override
            public void onSecond(TransferModel model) {
                next(model, TransferModel.STATE_DONE);
            }

            @Override
            public void onError(TransferModel model, Throwable e) {
                next(model, TransferModel.STATE_ERROR);
            }
        });
    }

    private void startMVImpl(final TransferModel item) {
        if (HitTarget.secondPassMV(item)) {
            ULog.d("dsiner_request--> Second pass");
            item.state = TransferModel.STATE_DONE;
            if (item.downloadCallback != null) {
                item.downloadCallback.onComplete();
            }
            next(item, TransferModel.STATE_DONE);
            return;
        }

        Transfer.downloadMV(item, new Transfer.OnTransferCallback<TransferModel>() {
            @Override
            public void onFirst(TransferModel model) {

            }

            @Override
            public void onSecond(TransferModel model) {
                next(model, TransferModel.STATE_DONE);
            }

            @Override
            public void onError(TransferModel model, Throwable e) {
                next(model, TransferModel.STATE_ERROR);
            }
        });
    }

    private void next(TransferModel item, int state) {
        if (state == TransferModel.STATE_DONE) {
            mPipe.pop(item);
            mPipe.finish(item);
        }
        next();
    }
}
