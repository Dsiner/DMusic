package com.d.dmusic.module.greendao.db;

import android.content.Context;

import com.d.dmusic.module.greendao.music.DaoMaster;
import com.d.dmusic.module.greendao.music.DaoSession;
import com.d.dmusic.module.greendao.music.base.MusicModel;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.identityscope.IdentityScopeType;

/**
 * db util
 * Created by D on 2016/3/17.
 */
public abstract class MusicDB extends AbstractDB<DaoMaster, DaoSession, MusicModel> {
    public static final int TABLE_COUNT = 24;//所有表数目4+20
    public static final int TABLE_INDEX_COUNT = 30;

    public static final int MUSIC = 1;//歌曲
    public static final int LOCAL_ALL_MUSIC = 2;//本地歌曲
    public static final int COLLECTION_MUSIC = 3;//收藏歌曲

    public static final int CUSTOM_LIST = 4;//自定义列表

    public static final int CUSTOM_MUSIC_INDEX = 10;//自定义歌曲 +0...19--->20张表
    public static final int CUSTOM_MUSIC_COUNT = 20;//自定义歌曲，表数目

    protected MusicDB(Context context) {
        super(context);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context.getApplicationContext(), "dmusic.db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        //GreenDao的Session会将第一次query的结果缓存起来，后面如果调用相同的查询语句则会直接显示缓存的对象
        //两种解决方式：
        //type1:每次查询更新的表之前调用一下<daoSession.clear();>清除缓存
        //type2:初始化时使用无缓存模式
        daoSession = daoMaster.newSession(IdentityScopeType.None);//无缓存模式
        initDaos();
    }

    private void initDaos() {
        daos = new AbstractDao[TABLE_INDEX_COUNT];
        for (int i = 0; i < TABLE_INDEX_COUNT; i++) {
            if (i / 10 == 0) {
                switch (i) {
                    case 1:
                        daos[i] = daoSession.getMusicDao();
                        break;
                    case 2:
                        daos[i] = daoSession.getLocalAllMusicDao();
                        break;
                    case 3:
                        daos[i] = daoSession.getCollectionMusicDao();
                        break;
                    case 4:
                        daos[i] = daoSession.getCustomListDao();
                        break;
                }
            } else {
                switch (i - 10) {
                    case 0:
                        daos[i] = daoSession.getCustomMusic0Dao();
                        break;
                    case 1:
                        daos[i] = daoSession.getCustomMusic1Dao();
                        break;
                    case 2:
                        daos[i] = daoSession.getCustomMusic2Dao();
                        break;
                    case 3:
                        daos[i] = daoSession.getCustomMusic3Dao();
                        break;
                    case 4:
                        daos[i] = daoSession.getCustomMusic4Dao();
                        break;
                    case 5:
                        daos[i] = daoSession.getCustomMusic5Dao();
                        break;
                    case 6:
                        daos[i] = daoSession.getCustomMusic6Dao();
                        break;
                    case 7:
                        daos[i] = daoSession.getCustomMusic7Dao();
                        break;
                    case 8:
                        daos[i] = daoSession.getCustomMusic8Dao();
                        break;
                    case 9:
                        daos[i] = daoSession.getCustomMusic9Dao();
                        break;
                    case 10:
                        daos[i] = daoSession.getCustomMusic10Dao();
                        break;
                    case 11:
                        daos[i] = daoSession.getCustomMusic11Dao();
                        break;
                    case 12:
                        daos[i] = daoSession.getCustomMusic12Dao();
                        break;
                    case 13:
                        daos[i] = daoSession.getCustomMusic13Dao();
                        break;
                    case 14:
                        daos[i] = daoSession.getCustomMusic14Dao();
                        break;
                    case 15:
                        daos[i] = daoSession.getCustomMusic15Dao();
                        break;
                    case 16:
                        daos[i] = daoSession.getCustomMusic16Dao();
                        break;
                    case 17:
                        daos[i] = daoSession.getCustomMusic17Dao();
                        break;
                    case 18:
                        daos[i] = daoSession.getCustomMusic18Dao();
                        break;
                    case 19:
                        daos[i] = daoSession.getCustomMusic19Dao();
                        break;
                }
            }
        }
    }
}
