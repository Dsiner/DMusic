package com.d.music.data.database.greendao.operation;

import androidx.annotation.NonNull;

import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.data.database.greendao.dao.TransferModelDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

import static com.d.music.data.database.greendao.db.AppDatabase.TRANSFER;

/**
 * OpTransfer
 * Created by D on 2018/10/14.
 */
public class OpTransfer extends AbstractOp {
    private TransferModelDao dao;

    public OpTransfer(AbstractDao[] daos) {
        this.dao = (TransferModelDao) daos[TRANSFER];
    }

    public void insertOrReplace(TransferModel bean) {
        dao.insertOrReplace(bean);
    }

    public void insertOrReplaceInTx(List<TransferModel> list) {
        insertOrReplace(dao, list, true);
    }

    public void update(TransferModel model) {
        update(dao, model);
    }

    public List<TransferModel> queryAll() {
        return dao.queryBuilder().list();
    }

    @NonNull
    public List<List<TransferModel>> queryAll(int type) {
        List<List<TransferModel>> lists = new ArrayList<>();
        List<TransferModel> downloading = dao.queryBuilder()
                .where(TransferModelDao.Properties.TransferType.eq(type)
                        , TransferModelDao.Properties.TransferState.notEq(TransferModel.TRANSFER_STATE_DONE)
                ).list();
        List<TransferModel> downloaded = dao.queryBuilder()
                .where(TransferModelDao.Properties.TransferType.eq(type)
                        , TransferModelDao.Properties.TransferState.eq(TransferModel.TRANSFER_STATE_DONE)
                ).list();
        lists.add(downloading != null ? downloading : new ArrayList<TransferModel>());
        lists.add(downloaded != null ? downloaded : new ArrayList<TransferModel>());
        return lists;
    }

    public void delete(TransferModel model) {
        delete(dao, model);
    }

    public void deleteAll(final int type) {
        final String where = TransferModelDao.Properties.TransferType.columnName + " = ?";
        dao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                dao.getDatabase().delete(dao.getTablename(), where, new String[]{"" + type});
            }
        });
    }
}
