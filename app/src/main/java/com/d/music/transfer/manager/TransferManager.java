package com.d.music.transfer.manager;

import com.d.music.transfer.manager.pipe.MVPipe;
import com.d.music.transfer.manager.pipe.SongPipe;

/**
 * TransferManager
 * Created by D on 2018/10/9.
 */
public class TransferManager {
    private Operater opSong, opMV;

    private static class Singleton {
        private final static TransferManager INSTANCE = new TransferManager();
    }

    public static TransferManager getIns() {
        return Singleton.INSTANCE;
    }

    private TransferManager() {
        opSong = new Operater(new SongPipe());
        opMV = new Operater(new MVPipe());
    }

    public Operater optSong() {
        return opSong;
    }

    public Operater optMV() {
        return opMV;
    }
}
