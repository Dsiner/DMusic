package com.d.music.transfer.manager.operation;

import android.support.annotation.NonNull;

import com.d.lib.common.utils.log.ULog;
import com.d.music.component.media.HitTarget;
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
    protected void downloadImp(TransferModel item) {
        if (HitTarget.secondPassMV(item)) {
            ULog.d("dsiner downloadMV--> Second pass");
            item.state = TransferModel.STATE_DONE;
            if (item.downloadCallback != null) {
                item.downloadCallback.onSuccess();
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
}
