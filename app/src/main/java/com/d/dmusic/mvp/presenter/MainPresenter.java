package com.d.dmusic.mvp.presenter;

import android.content.Context;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.mvp.view.IMainView;

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
public class MainPresenter extends MvpBasePresenter<IMainView> {
    public MainPresenter(Context context) {
        super(context);
    }

    public void getCustomList() {
        Observable.create(new ObservableOnSubscribe<List<CustomList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomList>> e) throws Exception {
                List<CustomList> list = MusicDBUtil.getInstance(mContext).queryAllCustomList();
                if (list == null) {
                    list = new ArrayList<CustomList>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CustomList>>() {
                    @Override
                    public void accept(@NonNull List<CustomList> list) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getView().setCustomList(list);
                    }
                });
    }
}
