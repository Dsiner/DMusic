package com.d.dmusic.module.greendao.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.db.SQLUtil;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.music.CustomListDao;
import com.d.dmusic.module.greendao.music.CustomMusic0Dao;
import com.d.dmusic.module.greendao.music.LocalAllMusicDao;
import com.d.dmusic.module.greendao.music.Music;
import com.d.dmusic.module.greendao.music.MusicDao;
import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * MusicDBUtil
 * Created by D on 2016/3/17.
 */
public class MusicDBUtil extends MusicDB {
    private static MusicDBUtil instance;

    private MusicDBUtil(Context context) {
        super(context);
    }

    public static MusicDBUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicDBUtil.class) {
                if (instance == null) {
                    instance = new MusicDBUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 插入表一条记录
     */
    public void insertOrReplaceMusic(Object o, int type) {
        switch (type) {
            case MUSIC:
                insertOrReplace(daos[MUSIC], o);
                break;
            case LOCAL_ALL_MUSIC:
                insertOrReplace(daos[LOCAL_ALL_MUSIC], o);
                break;
            case COLLECTION_MUSIC:
                insertOrReplace(daos[COLLECTION_MUSIC], o);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲
                    insertOrReplace(daos[type], o);
                }
                break;
        }
    }

    /**
     * 插入表一组记录-开启一次事务Transaction
     */
    public void insertOrReplaceMusicInTx(List<? extends MusicModel> list, int type) {
        switch (type) {
            case MUSIC:
                insertOrReplace(daos[MUSIC], list, true);
                break;
            case LOCAL_ALL_MUSIC:
                insertOrReplace(daos[LOCAL_ALL_MUSIC], list, true);
                break;
            case COLLECTION_MUSIC:
                insertOrReplace(daos[COLLECTION_MUSIC], list, true);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲
                    insertOrReplace(daos[type], list, true);
                }
                break;
        }
    }

    /**
     * 更新记录
     */
    public void updateColleted(final String url, final boolean isCollected) {
        final ContentValues value = new ContentValues();
        final String key = MusicDao.Properties.IsCollected.columnName;
        final String where = MusicDao.Properties.Url.columnName + " = ?";
        value.put(key, isCollected);
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                for (int type = 0; type < TABLE_INDEX_COUNT; type++) {
                    switch (type) {
                        case MUSIC:
                            daos[MUSIC].getDatabase().update(daos[MUSIC].getTablename(), value, where, new String[]{url});
                            break;
                        case LOCAL_ALL_MUSIC:
                            daos[LOCAL_ALL_MUSIC].getDatabase().update(daos[LOCAL_ALL_MUSIC].getTablename(), value, where, new String[]{url});
                            break;
                        case COLLECTION_MUSIC:
                            daos[COLLECTION_MUSIC].getDatabase().update(daos[COLLECTION_MUSIC].getTablename(), value, where, new String[]{url});
                            break;
                        default:
                            if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                                //自定义歌曲
                                daos[type].getDatabase().update(daos[type].getTablename(), value, where, new String[]{url});
                            }
                            break;
                    }
                }
            }
        });
    }

    /**
     * 更新记录
     */
    public void updateColleted(MusicModel model) {
        final boolean isColleted = model.getIsCollected();
        List<Music> list = daos[MUSIC].queryBuilder().where(MusicDao.Properties.Url.eq(model.getUrl())).list();
        if (list != null && list.size() > 0) {
            for (Music music : list) {
                music.setIsCollected(isColleted);
                daos[MUSIC].update(music);//更新
            }
        }
    }

    /**
     * 查询一条记录--如果给出参数msgId不唯一，将返回符合条件集合的第一条
     */
    public MusicModel queryMusicByUrl(String url, int type) {
        switch (type) {
            case MUSIC:
                List<Music> lists = daos[MUSIC].queryBuilder().where(MusicDao.Properties.Url.eq(url)).list();
                if (lists != null && lists.size() > 0) {
                    return lists.get(0);
                }
                break;
        }
        return null;
    }

    /**
     * 获取歌曲
     */
    public List<? extends MusicModel> queryAllMusic(int type) {
        List<? extends MusicModel> list = null;
        switch (type) {
            case MUSIC:
                list = queryAll(daos[MUSIC]);
                break;
            case LOCAL_ALL_MUSIC:
                list = queryAll(daos[LOCAL_ALL_MUSIC]);
                break;
            case COLLECTION_MUSIC:
                list = queryAll(daos[COLLECTION_MUSIC]);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲
                    list = queryAll(daos[type]);
                }
                break;
        }
        return list;
    }

    public List<MusicModel> queryLocalAllBySinger(String singer) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.Singer.eq(singer)).list();
    }

    public List<MusicModel> queryLocalAllByAlbum(String album) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.Album.eq(album)).list();
    }

    public List<MusicModel> queryLocalAllByFolder(String folder) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.Folder.eq(folder)).list();
    }

    /**
     * 插入一条自定义列表
     */
    public void insertOrReplaceCustomList(CustomList bean) {
        daos[CUSTOM_LIST].insertOrReplace(bean);
    }

    /**
     * CustomList表-更新歌曲数目
     */
    public void updateCusListCount(int type, long count) {
        List<CustomList> list = daos[CUSTOM_LIST].queryBuilder().where(CustomListDao.Properties.Pointer.eq(type)).list();
        if (list != null && list.size() > 0) {
            CustomList bean = list.get(0);
            bean.setSongCount(count);
            daos[CUSTOM_LIST].update(bean);//更新
        }
    }

    /**
     * CustomList表-更新排序方式
     */
    public void updateCusListSoryByType(int type, int sortBy) {
        List<CustomList> list = daos[CUSTOM_LIST].queryBuilder().where(CustomListDao.Properties.Pointer.eq(type)).list();
        if (list != null && list.size() > 0) {
            CustomList bean = list.get(0);
            bean.sortBy = sortBy;
            daos[CUSTOM_LIST].update(bean);//更新
        }
    }

    /**
     * CustomList表-获取自定义列表
     */
    public List<CustomList> queryAllCustomList() {
        return daos[CUSTOM_LIST].queryBuilder().orderAsc(CustomListDao.Properties.Seq).list();
    }

    /**
     * CustomList表-获取自定义列表-排除当前列表Type
     */
    public List<CustomList> queryAllCustomList(int notType) {
        return daos[CUSTOM_LIST].queryBuilder().where(CustomListDao.Properties.Pointer.notEq(notType)).list();
    }

    /**
     * CustomList表-获取自定义列表-Pointer自增
     */
    public List<CustomList> queryAllCustomListByPointerAsc() {
        return daos[CUSTOM_LIST].queryBuilder().orderAsc(CustomListDao.Properties.Pointer).list();
    }

    /**
     * CustomList表-查询seq列，最大值
     */
    public int queryCustomListMaxSeq() {
        List<CustomList> list = daos[CUSTOM_LIST].queryBuilder().orderDesc(CustomListDao.Properties.Seq).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getSeq();
        }
        return 0;
    }

    /**
     * CustomList表-查询seq列，最小值
     */
    public int queryCustomListMinSeq() {
        List<CustomList> list = daos[CUSTOM_LIST].queryBuilder().orderAsc(CustomListDao.Properties.Seq).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getSeq();
        }
        return 0;
    }

    /**
     * CustomList表-查询一条自定义列表--如果给出参数msgId不唯一，将返回符合条件集合的第一条
     */
    public CustomList queryCustomListByName(String name) {
        List<CustomList> list = daos[CUSTOM_LIST].queryBuilder().where(CustomListDao.Properties.ListName.eq(name)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询表：字段递增
     *
     * @return 查询结果集
     */
    public List<? extends MusicModel> queryAllCustomMusic(int type, int orderType) {
        if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
            switch (orderType) {
                case 0:
                    //按名称
                    return daos[type].queryBuilder().orderAsc(CustomMusic0Dao.Properties.SongName).list();
                case 1:
                    //按时间
                    return daos[type].queryBuilder().orderAsc(CustomMusic0Dao.Properties.TimeStamp).list();
                case 2:
                    //按自定义
                    return daos[type].loadAll();
            }
        }
        return null;
    }

    public Cursor queryBySQL(String... sql) {
        return SQLUtil.findBySQL(daos[LOCAL_ALL_MUSIC].getDatabase(), sql);
    }

    /**
     * 删除一条记录
     *
     * @param customSeq:customSeq
     */
    public void deleteAllByCustomSeq(final String customSeq) {
        final String where = MusicDao.Properties.Seq.columnName + " = ?";
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                daos[MUSIC].getDatabase().delete(daos[MUSIC].getTablename(), where, new String[]{customSeq});
            }
        });
    }

    public void delete(int type, Object... o) {
        switch (type) {
            case MUSIC:
                daos[MUSIC].deleteInTx(o);
                break;
            case LOCAL_ALL_MUSIC:
                daos[LOCAL_ALL_MUSIC].deleteInTx(o);
                break;
            case COLLECTION_MUSIC:
                daos[COLLECTION_MUSIC].deleteInTx(o);
                break;
            case CUSTOM_LIST:
                daos[CUSTOM_LIST].deleteInTx(o);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲:index 0-19 一一对应
                    daos[type].deleteInTx(o);
                }
                break;
        }
    }

    /**
     * 删除表中所有记录
     *
     * @param type:type
     */
    public void deleteAll(int type) {
        switch (type) {
            case MUSIC:
                deleteAll(daos[MUSIC]);
                break;
            case LOCAL_ALL_MUSIC:
                deleteAll(daos[LOCAL_ALL_MUSIC]);
                break;
            case COLLECTION_MUSIC:
                deleteAll(daos[COLLECTION_MUSIC]);
                break;
            case CUSTOM_LIST:
                deleteAll(daos[CUSTOM_LIST]);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲:index 0-19 一一对应
                    deleteAll(daos[type]);
                }
                break;
        }
    }
}