package com.d.music.transfer.manager.pipe;

import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.App;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.TransferModel;

import java.util.List;

/**
 * SongPipe
 * Created by D on 2018/10/10.
 */
public class SongPipe extends Pipe {

    public SongPipe() {
        init();
    }

    @Override
    public void init() {
        List<List<TransferModel>> lists = DBManager.getInstance(App.getContext()).optTransfer()
                .queryAll(TransferModel.TRANSFER_TYPE_SONG);
        mDownloading.addAll(lists.get(0));
        mDownloaded.addAll(lists.get(1));
        mList.addAll(mDownloading);
        mList.addAll(mDownloaded);
    }

    @Override
    protected void notifyItemChanged(final TransferModel model) {
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance(App.getContext()).optTransfer().update(model);
            }
        });
    }

    @Override
    protected void notifyItemInserted(final TransferModel model) {
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance(App.getContext()).optTransfer().insertOrReplace(model);
            }
        });
    }

    @Override
    protected void notifyItemMoved(final TransferModel model) {
        TaskScheduler.executeSingle(new Runnable() {
            @Override
            public void run() {
                DBManager.getInstance(App.getContext()).optTransfer().delete(model);
            }
        });
    }
}
