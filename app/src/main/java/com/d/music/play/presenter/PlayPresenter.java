package com.d.music.play.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;
import com.d.music.play.view.IPlayView;
import com.d.music.utils.fileutil.FileUtil;
import com.d.music.view.lrc.DefaultLrcParser;
import com.d.music.view.lrc.LrcRow;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * PlayPresenter
 * Created by D on 2017/6/2.
 */
public class PlayPresenter extends MvpBasePresenter<IPlayView> {
    private String lrcUrl;

    public PlayPresenter(Context context) {
        super(context);
    }

    public void reLoad() {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = AppDBUtil.getIns(mContext).optMusic().queryAll(AppDB.MUSIC);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MusicModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MusicModel> list) {
                        if (getView() == null) {
                            return;
                        }
                        getView().reLoad(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getLrcRows(final MusicModel model) {
        if (getView() == null || model == null) {
            return;
        }
        final String path = !TextUtils.isEmpty(model.lrcUrl) ? model.lrcUrl
                : model.fileFolder + "/" + model.songName + ".lrc";
        lrcUrl = path;
        Observable.create(new ObservableOnSubscribe<List<LrcRow>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LrcRow>> e) throws Exception {
                List<LrcRow> list = null;
                if (FileUtil.isFileExist(path)) {
                    list = DefaultLrcParser.getInstance().getLrcRows(path);
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LrcRow>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<LrcRow> list) {
                        if (getView() == null || !TextUtils.equals(lrcUrl, path)) {
                            return;
                        }
                        getView().setLrcRows(path, list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
