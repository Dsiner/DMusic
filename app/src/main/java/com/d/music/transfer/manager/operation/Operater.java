package com.d.music.transfer.manager.operation;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.d.lib.common.event.bus.AbstractBus;
import com.d.lib.rxnet.base.RequestManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.TransferDataObservable;
import com.d.music.transfer.manager.pipe.Pipe;

import java.util.List;

/**
 * Operater
 * Created by D on 2018/10/10.
 */
public abstract class Operater extends AbstractBus<Pipe, TransferDataObservable> {
    private Pipe mPipe;

    Operater(@NonNull Pipe pipe) {
        this.mPipe = pipe;
    }

    @SuppressWarnings("unused")
    public void setLimit(int limit) {
        this.mPipe.setLimit(limit);
    }

    @NonNull
    public Pipe pipe() {
        return mPipe;
    }

    @UiThread
    public void add(MusicModel item) {
        mPipe.add(item);
        next();
    }

    @UiThread
    public void start(TransferModel model) {
        List<TransferModel> list = mPipe.mDownloadingQueue;
        if (list.size() - 1 > 0) {
            pause(list.get(list.size() - 1));
        }
        startImpl(model);
    }

    @UiThread
    public void pause(TransferModel model) {
        mPipe.pop(model);
        model.state = TransferModel.STATE_PENDDING;
        RequestManager.getIns().cancel(model.type + model.songId);
    }

    @UiThread
    public void pauseAll() {
        List<TransferModel> list = mPipe.mDownloadingQueue;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            pause(list.get(i));
        }
        notifyDataSetChanged();
    }

    @UiThread
    public void clear(int type) {
        if (type == 0) {
            pauseAll();
            mPipe.mDownloadingQueue.clear();
            mPipe.mDownloading.clear();
        } else if (type == 1) {
            mPipe.mDownloaded.clear();
        }
        notifyDataSetChanged();
    }

    private void next() {
        notifyDataSetChanged();
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
        downloadImp(item);
    }

    protected void next(TransferModel item, int state) {
        if (state == TransferModel.STATE_DONE) {
            mPipe.pop(item);
            mPipe.finish(item);
        }
        next();
    }

    @UiThread
    private void notifyDataSetChanged() {
        for (int i = 0; i < mCallbacks.size(); i++) {
            TransferDataObservable l = mCallbacks.get(i);
            if (l != null) {
                l.notifyDataSetChanged(mPipe.lists());
            }
        }
    }

    protected abstract void downloadImp(final TransferModel item);
}
