package com.d.dmusic.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.mvp.view.ISongView;
import com.d.dmusic.utils.TaskManager;
import com.d.dmusic.view.DSLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by D on 2017/4/30.
 */
public class SongPresenter extends MvpBasePresenter<ISongView> {

    public SongPresenter(Context context) {
        super(context);
    }

    public void getSong(final int type, final int tab, final String sortBy) {
        if (isViewAttached()) {
            getView().setDSState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = null;
                if (type == MusicDB.LOCAL_ALL_MUSIC && tab > 0) {
                    switch (tab) {
                        case 1:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllBySinger(sortBy);
                            break;
                        case 2:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllByAlbum(sortBy);
                            break;
                        case 3:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllByFolder(sortBy);
                            break;
                    }
                } else {
                    list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllMusic(type);
                }
                if (list == null) {
                    list = new ArrayList<MusicModel>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        if (list.size() <= 0) {
                            getView().setDSState(DSLayout.STATE_EMPTY);
                        } else {
                            getView().setDSState(View.GONE);
                        }
                        getView().setSong(list);
                    }
                });
    }

    public void setSong(final List<MusicModel> models, final int type) {
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                for (MusicModel model : models) {
                    model.isSortChecked = false;
                }
                MusicDBUtil.getInstance(mContext).deleteAll(type);
                MusicDBUtil.getInstance(mContext).insertOrReplaceMusicInTx(MusicModel.clone(models, type), type);
                MusicDBUtil.getInstance(mContext).updateCusListCount(type, models != null ? models.size() : 0);
                MusicDBUtil.getInstance(mContext).updateCusListSoryByType(type, 2);//按自定义排序
            }
        });
    }
}
