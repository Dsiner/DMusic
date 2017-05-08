package com.d.dmusic.mvp.presenter;

import android.content.Context;
import android.view.View;

import com.d.commen.mvp.MvpBasePresenter;
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

    public void getSong(final int type) {
        if (isViewAttached()) {
            getView().setDSState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllMusic(type);
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

    public void refresh(final int id, final int type, final int sortBy) {
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                List<MusicModel> list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllCustomMusic(type, sortBy);
                int count = 0;
                if (list != null) {
                    count = list.size();
                }
                MusicDBUtil.getInstance(mContext).updateCusListCount(id, count);//更新数据库自定义列表歌曲数量
            }
        });
    }

    public void sortBy(final int id, final int sortBy) {
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                MusicDBUtil.getInstance(mContext).updateCusListSoryByType(id, sortBy);
            }
        });
    }
}
