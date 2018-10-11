package com.d.music.component.greendao.bean;

import com.d.lib.rxnet.callback.DownloadCallback;

/**
 * TransferModel
 * Created by D on 2018/8/31.
 */
public class TransferModel extends MusicModel {
    public final static int TYPE_TRANSFER_SONG = 100;
    public final static int TYPE_TRANSFER_MV = 101;

    public final static int VIEW_TYPE_SONG = 0;
    public final static int VIEW_TYPE_MV = 1;
    public final static int VIEW_TYPE_HEAD_NOT = 2;
    public final static int VIEW_TYPE_HEAD_DONE = 3;

    public final static int STATE_PROGRESS = 0;
    public final static int STATE_PENDDING = 1;
    public final static int STATE_ERROR = 2;
    public final static int STATE_DONE = 3;

    public DownloadCallback downloadCallback;
    public int viewType = VIEW_TYPE_SONG;
    public int state = STATE_PENDDING;

    public void setDownloadCallback(DownloadCallback l) {
        downloadCallback = l;
    }
}
