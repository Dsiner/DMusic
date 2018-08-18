package com.d.music.module.greendao.util;

import android.content.Context;

import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.operation.OpCustomList;
import com.d.music.module.greendao.operation.OpMusic;

/**
 * MusicDBUtil
 * Created by D on 2016/3/17.
 */
public class AppDBUtil extends AppDB {
    private volatile static AppDBUtil instance;

    private OpMusic opMusic;
    private OpCustomList opCustomList;

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
    }

    /****************************** Music ******************************/
    public OpMusic optMusic() {
        return opMusic;
    }

    /****************************** CustomList ******************************/
    public OpCustomList optCustomList() {
        return opCustomList;
    }
}