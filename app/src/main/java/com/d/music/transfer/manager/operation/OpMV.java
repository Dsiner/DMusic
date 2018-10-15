package com.d.music.transfer.manager.operation;

import android.support.annotation.NonNull;

import com.d.lib.common.utils.log.ULog;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.transfer.manager.pipe.Pipe;

/**
 * OpMV
 * Created by D on 2018/10/12.
 */
public class OpMV extends Operater {

    public OpMV(@NonNull Pipe pipe) {
        super(pipe);
    }

    @Override
    public void add(MusicModel item) {
        if (item == null) {
            return;
        }
        TransferModel model = new TransferModel(item);
        model.transferId = model.id
                = TransferModel.generateId(item.type,
                MusicModel.Channel.CHANNEL_TYPE_MV, item.songId);
        model.transferType = TransferModel.TRANSFER_TYPE_MV;
        mPipe.add(model);
        next();
    }

    @Override
    protected void downloadImp(TransferModel item) {
        if (HitTarget.secondPassMV(item)) {
            ULog.d("dsiner downloadMV--> Second pass");
            item.transferState = TransferModel.TRANSFER_STATE_DONE;
            if (item.progressCallback != null) {
                item.progressCallback.onSuccess();
            }
            next(item, TransferModel.TRANSFER_STATE_DONE);
            return;
        }

        Transfer.downloadMV(item, new Transfer.OnTransferCallback<TransferModel>() {
            @Override
            public void onFirst(TransferModel model) {

            }

            @Override
            public void onSecond(TransferModel model) {
                next(model, TransferModel.TRANSFER_STATE_DONE);
            }

            @Override
            public void onError(TransferModel model, Throwable e) {
                next(model, TransferModel.TRANSFER_STATE_ERROR);
            }
        });
    }
}
