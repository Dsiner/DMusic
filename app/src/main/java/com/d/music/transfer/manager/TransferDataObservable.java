package com.d.music.transfer.manager;

import com.d.lib.common.event.bus.callback.SimpleCallback;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.pipe.Pipe;

import java.util.List;

/**
 * TransferDataObservable
 * Created by D on 2018/10/10.
 */
public abstract class TransferDataObservable implements SimpleCallback<Pipe> {
    public void notifyDataSetChanged(List<List<TransferModel>> lists) {

    }

    public void notifyDataSetChanged(int count) {

    }

    @Override
    public void onSuccess(Pipe response) {

    }

    @Override
    public void onError(Throwable e) {

    }
}
