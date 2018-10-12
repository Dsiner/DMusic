package com.d.music.transfer.manager.operation;

import android.support.annotation.NonNull;

import com.d.lib.common.utils.log.ULog;
import com.d.music.component.media.HitTarget;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.Transfer;
import com.d.music.transfer.manager.pipe.Pipe;

/**
 * OpSong
 * Created by D on 2018/10/12.
 */
public class OpSong extends Operater {

    public OpSong(@NonNull Pipe pipe) {
        super(pipe);
    }

    @Override
    protected void downloadImp(TransferModel item) {
        if (HitTarget.secondPassSong(item)) {
            ULog.d("dsiner downloadSong--> Second pass");
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
}
