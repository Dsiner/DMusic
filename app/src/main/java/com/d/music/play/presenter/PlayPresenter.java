package com.d.music.play.presenter;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.play.view.IPlayView;

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

    public PlayPresenter(Context context) {
        super(context);
    }

    public void overLoad() {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = DBManager.getInstance(mContext).optMusic().queryAll(AppDatabase.MUSIC);
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
                        getView().overLoad(list);
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
