package com.d.music.data.database.greendao;

import android.content.Context;

import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.database.greendao.operation.OpCustomList;
import com.d.music.data.database.greendao.operation.OpMusic;
import com.d.music.data.database.greendao.operation.OpTransfer;

/**
 * AppDBUtil
 * Created by D on 2016/3/17.
 */
public class DBManager extends AppDatabase {
    private volatile static DBManager instance;

    private OpMusic opMusic;
    private OpCustomList opCustomList;
    private OpTransfer opTransfer;

    private DBManager(Context context) {
        super(context);
        initOps();
    }

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManager.class) {
                if (instance == null) {
                    instance = new DBManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 初始化操作句柄
     */
    private void initOps() {
        opMusic = new OpMusic(mDaos);
        opCustomList = new OpCustomList(mDaos);
        opTransfer = new OpTransfer(mDaos);
    }

    /****************************** Music ******************************/
    public OpMusic optMusic() {
        return opMusic;
    }

    /****************************** CustomList ******************************/
    public OpCustomList optCustomList() {
        return opCustomList;
    }

    /****************************** Transfer ******************************/
    public OpTransfer optTransfer() {
        return opTransfer;
    }
}