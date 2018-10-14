package com.d.music.transfer.manager.operation;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import com.d.lib.common.event.bus.AbstractBus;
import com.d.lib.rxnet.base.RequestManager;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.App;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.transfer.manager.TransferDataObservable;
import com.d.music.transfer.manager.pipe.MVPipe;
import com.d.music.transfer.manager.pipe.Pipe;
import com.d.music.transfer.manager.pipe.SongPipe;

import java.util.List;

/**
 * Operater
 * Created by D on 2018/10/10.
 */
public abstract class Operater extends AbstractBus<Pipe, TransferDataObservable> {
    protected Pipe mPipe;

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
    public abstract void add(MusicModel item);

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
        model.transferState = TransferModel.TRANSFER_STATE_PENDDING;
        mPipe.pop(model);
        RequestManager.getIns().cancel(model.transferId);
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
    public void remove(TransferModel model) {
        pause(model);
        mPipe.remove(model);
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
        notifyItemAllMoved();
    }

    protected void next() {
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
        item.transferState = TransferModel.TRANSFER_STATE_PROGRESS;
        mPipe.push(item);
        downloadImp(item);
    }

    protected void next(TransferModel item, int state) {
        if (state == TransferModel.TRANSFER_STATE_DONE) {
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

    private void notifyItemAllMoved() {
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                if (mPipe == null) {
                    return;
                }
                if (mPipe instanceof MVPipe) {
                    AppDBUtil.getIns(App.getContext()).optTransfer().deleteAll(TransferModel.TRANSFER_TYPE_MV);
                } else if (mPipe instanceof SongPipe) {
                    AppDBUtil.getIns(App.getContext()).optTransfer().deleteAll(TransferModel.TRANSFER_TYPE_SONG);
                }
            }
        });
    }

    protected abstract void downloadImp(final TransferModel item);
}
