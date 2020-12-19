package com.d.music.data.database.greendao.operation;

import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.dao.CustomListModelDao;
import com.d.music.data.database.greendao.dao.CustomMusic0Dao;
import com.d.music.data.database.greendao.dao.CustomMusic1Dao;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_LIST;
import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_MUSIC_COUNT;
import static com.d.music.data.database.greendao.db.AppDatabase.CUSTOM_MUSIC_INDEX;
import static com.d.music.data.database.greendao.db.AppDatabase.ORDER_TYPE_CUSTOM;
import static com.d.music.data.database.greendao.db.AppDatabase.ORDER_TYPE_NAME;
import static com.d.music.data.database.greendao.db.AppDatabase.ORDER_TYPE_TIME;

/**
 * CustomList操作
 * Created by D on 2017/11/8.
 */
public class OpCustomList extends AbstractOp {
    private AbstractDao[] daos;
    private CustomListModelDao dao;

    public OpCustomList(AbstractDao[] daos) {
        this.daos = daos;
        this.dao = (CustomListModelDao) daos[CUSTOM_LIST];
    }

    /**
     * 插入一条自定义列表
     */
    public void insertOrReplace(CustomListModel bean) {
        dao.insertOrReplace(bean);
    }

    /**
     * CustomList表 - 更新歌曲数目
     */
    public void updateCount(int type, long count) {
        if (type < CUSTOM_MUSIC_INDEX || type >= CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
            return;
        }
        List<CustomListModel> list = dao.queryBuilder().where(CustomListModelDao.Properties.Pointer.eq(type)).list();
        if (list != null && list.size() > 0) {
            CustomListModel bean = list.get(0);
            bean.setCount(count);
            dao.update(bean);
        }
    }

    /**
     * CustomList表 - 更新排序方式
     */
    public void updateortType(int type, int sortType) {
        if (type < CUSTOM_MUSIC_INDEX || type >= CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
            return;
        }
        if (sortType == ORDER_TYPE_CUSTOM || sortType == ORDER_TYPE_NAME || sortType == ORDER_TYPE_TIME) {
            List<CustomListModel> list = dao.queryBuilder().where(CustomListModelDao.Properties.Pointer.eq(type)).list();
            if (list != null && list.size() > 0) {
                CustomListModel bean = list.get(0);
                bean.sortType = sortType;
                dao.update(bean);
            }
        }
    }

    /**
     * CustomList - 查询排序方式
     */
    public int querySoryType(int type) {
        List<CustomListModel> list = dao.queryBuilder().where(CustomListModelDao.Properties.Pointer.eq(type)).list();
        if (list != null && list.size() > 0) {
            CustomListModel bean = list.get(0);
            return bean.sortType;
        }
        return 0;
    }

    /**
     * CustomList表 - 获取自定义列表
     */
    public List<CustomListModel> queryAll() {
        return dao.queryBuilder().orderAsc(CustomListModelDao.Properties.Seq).list();
    }

    /**
     * CustomList表 - 获取自定义列表 - 排除当前列表Type
     */
    public List<CustomListModel> queryAllNot(int notType) {
        return dao.queryBuilder().where(CustomListModelDao.Properties.Pointer.notEq(notType)).list();
    }

    /**
     * CustomList表 - 获取自定义列表 - Pointer自增
     */
    public List<CustomListModel> queryAllByPointerAsc() {
        return dao.queryBuilder().orderAsc(CustomListModelDao.Properties.Pointer).list();
    }

    /**
     * CustomList表 - 查询seq列，最大值
     */
    public int queryMaxSeq() {
        List<CustomListModel> list = dao.queryBuilder().orderDesc(CustomListModelDao.Properties.Seq).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getSeq();
        }
        return 0;
    }

    /**
     * CustomList表 - 查询seq列，最小值
     */
    public int queryMinSeq() {
        List<CustomListModel> list = dao.queryBuilder().orderAsc(CustomListModelDao.Properties.Seq).list();
        if (list != null && list.size() > 0) {
            return list.get(0).getSeq();
        }
        return 0;
    }

    /**
     * CustomList表 - 查询一条自定义列表--如果给出参数msgId不唯一，将返回符合条件集合的第一条
     */
    public CustomListModel queryByName(String name) {
        List<CustomListModel> list = dao.queryBuilder().where(CustomListModelDao.Properties.Name.eq(name)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询表
     *
     * @param type:      仅限自定义歌曲
     * @param orderType: 排序类型
     */
    @SuppressWarnings("unchecked")
    public List<MusicModel> queryAllCustomMusic(int type, int orderType) {
        if (type >= CUSTOM_MUSIC_INDEX && type < CUSTOM_MUSIC_INDEX + CUSTOM_MUSIC_COUNT) {
            switch (orderType) {
                case ORDER_TYPE_CUSTOM:
                    // 按自定义排序
                    return daos[type].loadAll();
                case ORDER_TYPE_NAME:
                    // 按名称排序
                    return daos[type].queryBuilder().orderAsc(CustomMusic0Dao.Properties.SongName).list();
                case ORDER_TYPE_TIME:
                    // 按时间排序
                    return daos[type].queryBuilder().orderAsc(CustomMusic1Dao.Properties.TimeStamp).list();
            }
        }
        return null;
    }
}
