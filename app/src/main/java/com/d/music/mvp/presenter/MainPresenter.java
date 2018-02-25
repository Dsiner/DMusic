package com.d.music.mvp.presenter;

import android.content.Context;
import android.database.Cursor;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.music.module.greendao.music.CustomList;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.mvp.view.IMainView;

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
 * MainPresenter
 * Created by D on 2017/4/30.
 */
public class MainPresenter extends MvpBasePresenter<IMainView> {
    public MainPresenter(Context context) {
        super(context);
    }

    public void getCustomList(final boolean isShowAdd) {
        Observable.create(new ObservableOnSubscribe<List<CustomList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomList>> e) throws Exception {
                List<CustomList> list = new ArrayList<CustomList>();
                List<CustomList> query = MusicDBUtil.getInstance(mContext).queryAllCustomList();
                if (query != null) {
                    list.addAll(query);
                }
                if (isShowAdd) {
                    CustomList add = new CustomList();
                    add.pointer = -1;//-1:add type
                    list.add(add);
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CustomList>>() {
                    @Override
                    public void accept(@NonNull List<CustomList> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setCustomList(list);
                    }
                });
    }


    public void getLocalAllCount() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Cursor cursor = MusicDBUtil.getInstance(mContext).queryBySQL("SELECT COUNT(*) FROM LOCAL_ALL_MUSIC");
                Integer count = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    int indexCount = cursor.getColumnIndex("COUNT(*)");
                    if (indexCount != -1) {
                        count = cursor.getInt(indexCount);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(count);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer count) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setLocalAllCount(count);
                    }
                });
    }


    public void getCollectionCount() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Cursor cursor = MusicDBUtil.getInstance(mContext).queryBySQL("SELECT COUNT(*) FROM COLLECTION_MUSIC");
                Integer count = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    int indexCount = cursor.getColumnIndex("COUNT(*)");
                    if (indexCount != -1) {
                        count = cursor.getInt(indexCount);
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(count);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer count) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setCollectionCount(count);
                    }
                });
    }
}
