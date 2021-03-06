package com.d.music.data.database.greendao.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.d.lib.aster.callback.ProgressCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity mapped to table "TRANSFER_MODEL".
 */
public class TransferModel extends MusicModel {
    public static final int TRANSFER_TYPE_NONE = 100;
    public static final int TRANSFER_TYPE_SONG = 101;
    public static final int TRANSFER_TYPE_MV = 102;
    public static final int TRANSFER_TYPE_HEAD_NOT = 103;
    public static final int TRANSFER_TYPE_HEAD_DONE = 104;

    public static final int TRANSFER_STATE_PROGRESS = 0;
    public static final int TRANSFER_STATE_PENDDING = 1;
    public static final int TRANSFER_STATE_ERROR = 2;
    public static final int TRANSFER_STATE_DONE = 3;

    public String transferId = "";
    public Integer transferType = TRANSFER_TYPE_NONE;
    public Integer transferState = TRANSFER_STATE_PENDDING;
    public Long transferCurrentLength = 0L;
    public Long transferTotalLength = 0L;

    /**
     * Extra: Not in the database
     */
    // Extra properties: Transmission speed
    public float transferSpeed;
    // Extra properties: Transmission ProgressCallback
    public ProgressCallback progressCallback;

    public TransferModel() {
    }

    public TransferModel(String transferId) {
        this.transferId = transferId;
    }

    public TransferModel(MusicModel model) {
        this("", TRANSFER_TYPE_NONE, TRANSFER_STATE_PENDDING, 0L, 0L,
                model.id, model.type, model.seq, model.songId, model.songName, model.songUrl,
                model.artistId, model.artistName,
                model.albumId, model.albumName, model.albumUrl,
                model.lrcName, model.lrcUrl,
                model.fileDuration, model.fileSize, model.filePostfix, model.fileFolder,
                model.isCollected, model.timeStamp);
    }

    public TransferModel(String transferId, Integer transferType, Integer transferState, Long transferCurrentLength, Long transferTotalLength, String id, Integer type, Integer seq, String songId, String songName, String songUrl, String artistId, String artistName, String albumId, String albumName, String albumUrl, String lrcName, String lrcUrl, Long fileDuration, Long fileSize, String filePostfix, String fileFolder, Boolean isCollected, Long timeStamp) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.transferState = transferState;
        this.transferCurrentLength = transferCurrentLength;
        this.transferTotalLength = transferTotalLength;
        this.id = id;
        this.type = type;
        this.seq = seq;
        this.songId = songId;
        this.songName = songName;
        this.songUrl = songUrl;
        this.artistId = artistId;
        this.artistName = artistName;
        this.albumId = albumId;
        this.albumName = albumName;
        this.albumUrl = albumUrl;
        this.lrcName = lrcName;
        this.lrcUrl = lrcUrl;
        this.fileDuration = fileDuration;
        this.fileSize = fileSize;
        this.filePostfix = filePostfix;
        this.fileFolder = fileFolder;
        this.isCollected = isCollected;
        this.timeStamp = timeStamp;
    }

    public static String generateId(@NonNull MusicModel model) {
        if (model instanceof TransferModel) {
            return ((TransferModel) model).transferId;
        }
        return generateId(model.type, Channel.CHANNEL_TYPE_NONE, model.songId);
    }

    public static String generateId(int type, int channel, String id) {
        return "" + type + channel + (!TextUtils.isEmpty(id) ? id : "");
    }

    public static List<MusicModel> convertTo(@NonNull List<TransferModel> datas) {
        List<MusicModel> list = new ArrayList<>();
        for (TransferModel model : datas) {
            if (model == null) {
                continue;
            }
            list.add(convertTo(model));
        }
        return list;
    }

    public static MusicModel convertTo(@NonNull TransferModel model) {
        return new MusicModel(model.id, model.type, model.seq, model.songId, model.songName, model.songUrl,
                model.artistId, model.artistName,
                model.albumId, model.albumName, model.albumUrl,
                model.lrcName, model.lrcUrl,
                model.fileDuration, model.fileSize, model.filePostfix, model.fileFolder,
                model.isCollected, model.timeStamp);
    }

    public void setProgressCallback(ProgressCallback l) {
        progressCallback = l;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    public Integer getTransferState() {
        return transferState;
    }

    public void setTransferState(Integer transferState) {
        this.transferState = transferState;
    }

    public Long getTransferCurrentLength() {
        return transferCurrentLength;
    }

    public void setTransferCurrentLength(Long transferCurrentLength) {
        this.transferCurrentLength = transferCurrentLength;
    }

    public Long getTransferTotalLength() {
        return transferTotalLength;
    }

    public void setTransferTotalLength(Long transferTotalLength) {
        this.transferTotalLength = transferTotalLength;
    }
}
