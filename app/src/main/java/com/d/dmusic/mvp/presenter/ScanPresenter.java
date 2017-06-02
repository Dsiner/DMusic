package com.d.dmusic.mvp.presenter;

import android.content.Context;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.dmusic.model.FileModel;
import com.d.dmusic.module.events.MusicModelEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.media.MusicFactory;
import com.d.dmusic.mvp.view.IScanView;
import com.d.dmusic.utils.fileutil.FileUtil;

import org.greenrobot.eventbus.EventBus;

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
 * ScanPresenter
 * Created by D on 2017/4/30.
 */
public class ScanPresenter extends MvpBasePresenter<IScanView> {
    public ScanPresenter(Context context) {
        super(context);
    }

    public void getFileModels(final String path) {
        Observable.create(new ObservableOnSubscribe<List<FileModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FileModel>> e) throws Exception {
                List<FileModel> list = FileUtil.getFiles(path, false);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FileModel>>() {
                    @Override
                    public void accept(@NonNull List<FileModel> list) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getView().setDatas(list);
                    }
                });
    }

    public void getMusics(final List<String> paths, final int type) {
        if (isViewAttached()) {
            getView().showLoading();
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = (List<MusicModel>) MusicFactory.createFactory(mContext, type).getMusic(paths);
                MusicDBUtil.getInstance(mContext).deleteAll(type);
                MusicDBUtil.getInstance(mContext).insertOrReplaceMusicInTx(list, type);
                MusicDBUtil.getInstance(mContext).updateCusListCount(type, list != null ? list.size() : 0);
                MusicDBUtil.getInstance(mContext).updateCusListSoryByType(type, MusicDB.ORDER_TYPE_CUSTOM);//默认按自定义排序

                //更新收藏字段
                List<MusicModel> c = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllMusic(MusicDB.COLLECTION_MUSIC);
                MusicDBUtil.getInstance(mContext).insertOrReplaceMusicInTx(MusicModel.clone(c, type), type);

                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        MusicModelEvent event = new MusicModelEvent(type, list);
                        EventBus.getDefault().post(event);

                        if (!isViewAttached()) {
                            return;
                        }
                        getView().closeLoading();
                        getView().setMusics(list);
                    }
                });
    }
}
