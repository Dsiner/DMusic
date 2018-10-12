package com.d.music.transfer.manager;

import android.support.annotation.UiThread;

import com.d.lib.common.event.bus.AbstractBus;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.manager.operation.OpMV;
import com.d.music.transfer.manager.operation.OpSong;
import com.d.music.transfer.manager.operation.Operater;
import com.d.music.transfer.manager.pipe.MVPipe;
import com.d.music.transfer.manager.pipe.Pipe;
import com.d.music.transfer.manager.pipe.SongPipe;

import java.util.List;

/**
 * TransferManager
 * Created by D on 2018/10/9.
 */
public class TransferManager extends AbstractBus<Pipe, TransferDataObservable> {
    private Operater opSong, opMV;

    private static class Singleton {
        private final static TransferManager INSTANCE = new TransferManager();
    }

    public static TransferManager getIns() {
        return Singleton.INSTANCE;
    }

    private TransferManager() {
        TransferDataObservable observable = new TransferDataObservable() {
            @Override
            public void notifyDataSetChanged(List<List<TransferModel>> lists) {
                TransferManager.this.notifyDataSetChanged();
            }
        };
        opSong = new OpSong(new SongPipe());
        opMV = new OpMV(new MVPipe());
        opSong.register(observable);
        opMV.register(observable);
    }

    public Operater optSong() {
        return opSong;
    }

    public Operater optMV() {
        return opMV;
    }

    /**
     * Downloading count
     */
    public int getCount() {
        return opSong.pipe().lists().get(0).size()
                + opMV.pipe().lists().get(0).size();
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
}
