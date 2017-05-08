package com.d.dmusic.mvp.presenter;

import android.content.Context;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.dmusic.model.FileModel;
import com.d.dmusic.module.greendao.music.LocalAllMusic;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.media.MusicFactory;
import com.d.dmusic.mvp.view.IScanView;
import com.d.dmusic.utils.fileutil.FileUtil;

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

public class ScanPresenter extends MvpBasePresenter<IScanView> {
    public ScanPresenter(Context context) {
        super(context);
    }

    public void getFileModels(final String path) {
        Observable.create(new ObservableOnSubscribe<List<FileModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FileModel>> e) throws Exception {
                List<FileModel> list = FileUtil.getFiles(path, true);
                if (list == null) {
                    list = new ArrayList<FileModel>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FileModel>>() {
                    @Override
                    public void accept(@NonNull List<FileModel> list) throws Exception {
                        if (!isViewAttached() || list == null || list.size() <= 0) {
                            return;
                        }
                        getView().setDatas(list);
                    }
                });
    }

    public void getMusics(final List<String> paths, int type) {
        List<LocalAllMusic> list = (List<LocalAllMusic>) MusicFactory.createFactory(mContext, type).getMusic(paths);
        MusicDBUtil.getInstance(mContext).deleteAll(type);
        MusicDBUtil.getInstance(mContext).insertOrReplaceMusicInTx(list, type);
    }
}
