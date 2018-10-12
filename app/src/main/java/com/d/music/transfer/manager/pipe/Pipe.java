package com.d.music.transfer.manager.pipe;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.event.TransferEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Pipe
 * Created by D on 2018/10/10.
 */
public abstract class Pipe {
    public int mLimit = 3;
    public List<List<TransferModel>> mArray = new ArrayList<>();
    public List<TransferModel> mList = new ArrayList<>();
    public List<TransferModel> mDownloadingQueue = new ArrayList<>();
    public List<TransferModel> mDownloading = new ArrayList<>();
    public List<TransferModel> mDownloaded = new ArrayList<>();
    public TransferEvent mEvent;

    public abstract void init();

    public void list(@NonNull List<TransferModel> datas) {
        datas.clear();
        datas.addAll(mDownloading);
        datas.addAll(mDownloaded);
    }

    @NonNull
    public List<TransferModel> list() {
        return mList;
    }

    @NonNull
    public List<List<TransferModel>> lists() {
        mArray.clear();
        mArray.add(mDownloading);
        mArray.add(mDownloaded);
        return mArray;
    }

    @UiThread
    public void add(MusicModel item) {
        TransferModel model = new TransferModel();
        model.type = item.type;
        model.viewType = item instanceof TransferModel ? ((TransferModel) item).viewType
                : TransferModel.VIEW_TYPE_SONG;
        model.songId = item.songId;
        model.url = item.url;
        model.songName = item.songName;
        model.artistId = item.artistId;
        model.artistName = item.artistName;
        model.albumId = item.albumId;
        model.albumUrl = item.albumUrl;
        model.fileFolder = item.fileFolder;
        model.fileFolder = item.filePostfix;

        for (int i = 0; i < mList.size(); i++) {
            TransferModel transfer = mList.get(i);
            if (transfer.type.equals(model.type)
                    && TextUtils.equals(transfer.songId, model.songId)) {
                return;
            }
        }
        mDownloading.add(model);
        mList.add(model);
    }

    @UiThread
    public void push(TransferModel item) {
        mDownloadingQueue.add(item);
    }

    @UiThread
    public void pop(TransferModel item) {
        mDownloadingQueue.remove(item);
    }

    @UiThread
    public List<TransferModel> peek() {
        List<TransferModel> list = new ArrayList<>();
        int size = mDownloading.size();
        for (int i = 0; i < size; i++) {
            if (list.size() + mDownloadingQueue.size() >= mLimit) {
                break;
            }
            TransferModel model = mDownloading.get(i);
            if (model.state == TransferModel.STATE_PENDDING) {
                list.add(model);
            }
        }
        return list;
    }

    @UiThread
    public void finish(TransferModel model) {
        mDownloading.remove(model);
        mDownloaded.add(model);
    }

    @UiThread
    public void remove(TransferModel model) {
        if (!mDownloading.remove(model)) {
            mDownloaded.remove(model);
        }
        mList.remove(model);
    }

    @UiThread
    public void notifyDataSetChanged() {
        if (mEvent != null) {
            mEvent.onEvent();
        }
    }

    @UiThread
    public void register(TransferEvent event) {
        this.mEvent = event;
    }

    @UiThread
    public void unregister(TransferEvent transferEvent) {
        mEvent = null;
    }
}
