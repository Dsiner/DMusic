package com.d.music.data.database.greendao.util;

import android.content.Context;

import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.database.greendao.operation.OpCustomList;
import com.d.music.data.database.greendao.operation.OpMusic;
import com.d.music.data.database.greendao.operation.OpTransfer;

/**
 * AppDBUtil
 * Created by D on 2016/3/17.
 */
public class AppDBUtil extends AppDB {
    private volatile static AppDBUtil instance;

    private OpMusic opMusic;
    private OpCustomList opCustomList;
    private OpTransfer opTransfer;

    private AppDBUtil(Context context) {
        super(context);
        initOps();
    }

    public static AppDBUtil getIns(Context context) {
        if (instance == null) {
            synchronized (AppDBUtil.class) {
                if (instance == null) {
                    instance = new AppDBUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 初始化操作句柄
     */
    private void initOps() {
        opMusic = new OpMusic(daos);
        opCustomList = new OpCustomList(daos);
        opTransfer = new OpTransfer(daos);
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