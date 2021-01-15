package com.d.music.transfer.manager;

import androidx.annotation.UiThread;

import com.d.lib.common.event.bus.AbstractBus;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.operation.OpMV;
import com.d.music.transfer.manager.operation.OpSong;
import com.d.music.transfer.manager.operation.TransferOperator;
import com.d.music.transfer.manager.pipe.MVPipe;
import com.d.music.transfer.manager.pipe.Pipe;
import com.d.music.transfer.manager.pipe.SongPipe;

import java.util.List;

/**
 * TransferManager
 * Created by D on 2018/10/9.
 */
public class TransferManager extends AbstractBus<Pipe, TransferDataObservable> {
    private TransferOperator mOpSong, mOpMV;

    private TransferManager() {
        TransferDataObservable observable = new TransferDataObservable() {
            @Override
            public void notifyDataSetChanged(List<List<TransferModel>> lists) {
                TransferManager.this.notifyDataSetChanged();
            }
        };
        mOpSong = new OpSong(new SongPipe());
        mOpMV = new OpMV(new MVPipe());
        mOpSong.register(observable);
        mOpMV.register(observable);
    }

    public static TransferManager getInstance() {
        return Singleton.INSTANCE;
    }

    public TransferOperator optSong() {
        return mOpSong;
    }

    public TransferOperator optMV() {
        return mOpMV;
    }

    /**
     * Downloading count
     */
    public int getCount() {
        return mOpSong.pipe().lists().get(0).size()
                + mOpMV.pipe().lists().get(0).size();
    }

    @UiThread
    private void notifyDataSetChanged() {
        for (int i = 0; i < mCallbacks.size(); i++) {
            TransferDataObservable l = mCallbacks.get(i);
            if (l != null) {
                l.notifyDataSetChanged(getCount());
            }
        }
    }

    private static class Singleton {
        private static final TransferManager INSTANCE = new TransferManager();
    }
}
