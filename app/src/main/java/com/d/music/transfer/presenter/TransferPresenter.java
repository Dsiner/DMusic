package com.d.music.transfer.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.lib.taskscheduler.callback.Observer;
import com.d.lib.taskscheduler.callback.Task;
import com.d.lib.taskscheduler.schedule.Schedulers;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.d.music.transfer.fragment.TransferFragment;
import com.d.music.transfer.manager.TransferManager;
import com.d.music.transfer.view.ITransferView;

import java.util.ArrayList;
import java.util.List;

/**
 * TransferPresenter
 * Created by D on 2018/8/25.
 */
public class TransferPresenter extends MvpBasePresenter<ITransferView> {
    private List<TransferModel> mDatas = new ArrayList<>();
    private TransferModel mHead0, mHead1;

    public TransferPresenter(Context context) {
        super(context);
    }

    public void load(final int type) {
        TaskScheduler.create(new Task<List<List<TransferModel>>>() {
            @Override
            public List<List<TransferModel>> run() {
                return type == TransferFragment.TYPE_MV ?
                        TransferManager.getInstance().optMV().pipe().lists()
                        : TransferManager.getInstance().optSong().pipe().lists();
            }
        }).subscribeOn(Schedulers.mainThread())
                .observeOn(Schedulers.mainThread())
                .subscribe(new Observer<List<List<TransferModel>>>() {
                    @Override
                    public void onNext(@NonNull List<List<TransferModel>> result) {
                        if (getView() == null) {
                            return;
                        }
                        getView().notifyDataSetChanged(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setState(DSLayout.STATE_EMPTY);
                    }
                });
    }

    @NonNull
    public List<TransferModel> getDatas(int type) {
        List<List<TransferModel>> lists = type == TransferFragment.TYPE_MV ?
                TransferManager.getInstance().optMV().pipe().lists()
                : TransferManager.getInstance().optSong().pipe().lists();
        return getDatas(lists);
    }

    @NonNull
    public List<TransferModel> getDatas(@NonNull List<List<TransferModel>> lists) {
        mDatas.clear();
        List<TransferModel> download = lists.get(0);
        List<TransferModel> downloaded = lists.get(1);
        if (download != null && download.size() > 0) {
            if (mHead0 == null) {
                mHead0 = new TransferModel();
                mHead0.transferType = TransferModel.TRANSFER_TYPE_HEAD_NOT;
            }
            mDatas.add(mHead0);
            mDatas.addAll(download);
        }
        if (downloaded != null && downloaded.size() > 0) {
            if (mHead1 == null) {
                mHead1 = new TransferModel();
                mHead1.transferType = TransferModel.TRANSFER_TYPE_HEAD_DONE;
            }
            mDatas.add(mHead1);
            mDatas.addAll(downloaded);
        }
        return mDatas;
    }
}
