package com.d.music.data.database.greendao.operation;

import android.content.ContentValues;
import android.database.Cursor;

import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.dao.LocalAllMusicDao;
import com.d.music.data.database.greendao.dao.MusicModelDao;
import com.d.music.data.database.greendao.db.SQLUtils;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

import static com.d.music.data.database.greendao.db.AppDatabase.COLLECTION_MUSIC;
import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_LIST;
import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_MUSIC_COUNT;
import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_MUSIC_INDEX;
import static com.d.music.data.database.greendao.db.AppDatabase.LOCAL_ALL_MUSIC;
import static com.d.music.data.database.greendao.db.AppDatabase.MUSIC;
import static com.d.music.data.database.greendao.db.AppDatabase.TABLE_INDEX_COUNT;

/**
 * Music操作
 * Created by D on 2017/11/8.
 */
public class OpMusic extends AbstractOp {
    private AbstractDao[] daos;

    public OpMusic(AbstractDao[] daos) {
        this.daos = daos;
    }

    /**
     * 插入表一条记录
     */
    public void insertOrReplace(int type, MusicModel o) {
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
                    // 自定义歌曲
                    insertOrReplace(daos[type], o);
                }
                break;
        }
    }

    /**
     * 插入表一组记录-开启一次事务Transaction
     */
    public void insertOrReplaceInTx(int type, List<MusicModel> list) {
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
                    // 自定义歌曲
                    insertOrReplace(daos[type], list, true);
                }
                break;
        }
    }

    /**
     * 更新记录
     */
    public void updateColleted(final String id, final boolean collected) {
        final ContentValues value = new ContentValues();
        final String key = MusicModelDao.Properties.IsCollected.columnName;
        final String where = MusicModelDao.Properties.Id.columnName + " = ?";
        value.put(key, collected);
        daos[MUSIC].getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int type = 0; type < TABLE_INDEX_COUNT; type++) {
                    switch (type) {
                        case MUSIC:
                            daos[MUSIC].getDatabase().update(daos[MUSIC].getTablename(), value, where, new String[]{id});
                            break;
                        case LOCAL_ALL_MUSIC:
                            daos[LOCAL_ALL_MUSIC].getDatabase().update(daos[LOCAL_ALL_MUSIC].getTablename(), value, where, new String[]{id});
                            break;
                        case COLLECTION_MUSIC:
                            daos[COLLECTION_MUSIC].getDatabase().update(daos[COLLECTION_MUSIC].getTablename(), value, where, new String[]{id});
                            break;
                        default:
                            if (type >= CUSTOM_MUSIC_INDEX) {
                                // 自定义歌曲
                                daos[type].getDatabase().update(daos[type].getTablename(), value, where, new String[]{id});
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
        List<MusicModel> list = daos[MUSIC].queryBuilder().where(MusicModelDao.Properties.Id.eq(model.getId())).list();
        if (list != null && list.size() > 0) {
            for (MusicModel musicModel : list) {
                musicModel.setIsCollected(isColleted);
                daos[MUSIC].update(musicModel);
            }
        }
    }

    /**
     * 查询一条记录--如果给出参数msgId不唯一，将返回符合条件集合的第一条
     */
    public MusicModel queryMusicByUrl(int type, String id) {
        switch (type) {
            case MUSIC:
                List<MusicModel> lists = daos[MUSIC].queryBuilder().where(MusicModelDao.Properties.Id.eq(id)).list();
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
    public List<MusicModel> queryAll(int type) {
        List<MusicModel> list = null;
        switch (type) {
            case MUSIC:
                list = (List<MusicModel>) queryAll(daos[MUSIC]);
                break;
            case LOCAL_ALL_MUSIC:
                list = (List<MusicModel>) queryAll(daos[LOCAL_ALL_MUSIC]);
                break;
            case COLLECTION_MUSIC:
                list = (List<MusicModel>) queryAll(daos[COLLECTION_MUSIC]);
                break;
            default:
                if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
                    // 自定义歌曲
                    list = (List<MusicModel>) queryAll(daos[type]);
                }
                break;
        }
        return list;
    }

    public List<MusicModel> queryLocalAllBySinger(String singer) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.ArtistName.eq(singer)).list();
    }

    public List<MusicModel> queryLocalAllByAlbum(String album) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.AlbumName.eq(album)).list();
    }

    public List<MusicModel> queryLocalAllByFolder(String folder) {
        return daos[LOCAL_ALL_MUSIC].queryBuilder().where(LocalAllMusicDao.Properties.FileFolder.eq(folder)).list();
    }


    public Cursor queryBySQL(String... sql) {
        return SQLUtils.findBySQL(daos[LOCAL_ALL_MUSIC].getDatabase(), sql);
    }

    /**
     * 删除一条记录
     */
    public void deleteAllBySeq(final String seq) {
        final String where = MusicModelDao.Properties.Seq.columnName + " = ?";
        daos[MUSIC].getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                daos[MUSIC].getDatabase().delete(daos[MUSIC].getTablename(), where, new String[]{seq});
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
                    // 自定义歌曲: index 0 - 19 一一对应
                    daos[type].deleteInTx(o);
                }
                break;
        }
    }

    /**
     * 删除表中所有记录
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
                    // 自定义歌曲: index 0 - 19 一一对应
                    deleteAll(daos[type]);
                }
                break;
        }
    }
}
