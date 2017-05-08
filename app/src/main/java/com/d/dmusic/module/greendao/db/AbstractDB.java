package com.d.dmusic.module.greendao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.AbstractDaoSession;

/**
 * Created by D on 2017/5/2.
 */
public abstract class AbstractDB<M extends AbstractDaoMaster, S extends AbstractDaoSession, D> {
    protected SQLiteDatabase db;
    protected M daoMaster;
    protected S daoSession;
    protected AbstractDao daos[];
    protected Gson gson;

    protected AbstractDB(Context context) {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    /**
     * @param dao:dao
     * @param transaction:true-开启一次事务
     */
    protected void insertAll(final AbstractDao dao, final List<D> list, boolean transaction) {
        if (dao == null || list == null || list.size() <= 0) {
            return;
        }
        final int size = list.size();
        if (!transaction) {
            for (int i = 0; i < size; i++) {
                insert(dao, list.get(i));
            }
            return;
        }
        dao.insertInTx(list);
//        daoSession.runInTx(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < size; i++) {
//                    insert(dao, list.get(i));
//                }
//            }
//        });
    }

    /**
     * 插入一条记录
     */
    protected void insert(AbstractDao dao, Object o) {
        if (dao == null || o == null) {
            return;
        }
        dao.insert(o);
    }

    /**
     * @param dao:dao
     * @param transaction:true-开启一次事务
     */
    protected void insertOrReplace(final AbstractDao dao, final List<? extends D> list, boolean transaction) {
        if (dao == null || list == null || list.size() <= 0) {
            return;
        }
        final int size = list.size();
        if (!transaction) {
            for (int i = 0; i < size; i++) {
                insertOrReplace(dao, list.get(i));
            }
            return;
        }
        dao.insertOrReplaceInTx(list);
//        daoSession.runInTx(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < size; i++) {
//                    insertOrReplace(dao, list.get(i));
//                }
//            }
//        });
    }

    /**
     * 插入一条记录
     */
    protected void insertOrReplace(AbstractDao dao, Object o) {
        if (dao == null || o == null) {
            return;
        }
        dao.insertOrReplace(o);
    }

    /**
     * 删除表中所有记录
     */
    protected void deleteAll(AbstractDao dao) {
        if (dao == null) {
            return;
        }
        dao.deleteAll();
    }

    /**
     * 删除表中一条记录
     *
     * @param id:主键
     */
    protected void deleteById(AbstractDao dao, Long id) {
        if (dao == null) {
            return;
        }
        dao.deleteByKey(id);
    }

    protected void update(AbstractDao dao, Object o) {
        if (dao == null || o == null) {
            return;
        }
        dao.update(o);
    }

//    /**
//     * @param dao:dao
//     * @param updateKey:AbstractDao.Properties.?.columnName
//     * @param updateValue:updateValue
//     * @param where:AbstractDao.Properties.?.columnName     + " = ?"
//     * @param whereArgs:whereArgs
//     */
//    protected void update(AbstractDao dao, String updateKey, Integer updateValue, final String where, final String[] whereArgs) {
//        final ContentValues value = new ContentValues();
//        value.put(updateKey, updateValue);
//        daos.getDatabase().update(daos.getTablename(), value, where, whereArgs);
//    }

    /**
     * 查询表，默认条件：主键id递增
     *
     * @return List<D>：查询结果集
     */
    protected List<D> queryAll(AbstractDao dao) {
        if (dao == null) {
            return null;
        }
        return dao.loadAll();
    }

//    protected List<D> queryAll(AbstractDao dao, Long id) {
//        return daos.queryBuilder().where(AbstractDao.Properties.Id.eq(id)).list();
//    }

//    /**
//     * 查询表，默认条件：Time字段递减
//     *
//     * @return dao.queryBuilder().orderDesc(AbstractDao.Properties.Time).list()：查询结果集
//     */
//    protected abstract List<D> queryAllByTimeOrderDesc(AbstractDao dao);

    /**
     * 查询表，默认条件
     *
     * @param offset：偏移量
     * @param limit：从偏移量始，查询条数
     * @return List<D>：查询结果集
     */
    protected List<D> queryAllOrderDescByPage(AbstractDao dao, int offset, int limit) {
        if (dao == null) {
            return null;
        }
        return dao.queryBuilder().offset(offset).limit(limit).list();
    }
}
