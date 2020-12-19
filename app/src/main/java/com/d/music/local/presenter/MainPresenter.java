package com.d.music.local.presenter;

import android.content.Context;
import android.database.Cursor;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.local.view.IMainView;

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
 * MainPresenter
 * Created by D on 2017/4/30.
 */
public class MainPresenter extends MvpBasePresenter<IMainView> {
    public MainPresenter(Context context) {
        super(context);
    }

    public void getCustomList(final boolean isShowAdd) {
        Observable.create(new ObservableOnSubscribe<List<CustomListModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CustomListModel>> e) throws Exception {
                List<CustomListModel> list = new ArrayList<>();
                List<CustomListModel> query = DBManager.getInstance(mContext).optCustomList().queryAll();
                if (query != null) {
                    list.addAll(query);
                }
                if (isShowAdd) {
                    CustomListModel add = new CustomListModel();
                    add.pointer = -1; // -1: Add type
                    list.add(add);
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CustomListModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CustomListModel> list) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setCustomList(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void getLocalAllCount() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Cursor cursor = DBManager.getInstance(mContext).optMusic().queryBySQL("SELECT COUNT(*) FROM LOCAL_ALL_MUSIC");
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
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setLocalAllCount(count);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void getCollectionCount() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Cursor cursor = DBManager.getInstance(mContext).optMusic().queryBySQL("SELECT COUNT(*) FROM COLLECTION_MUSIC");
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
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setCollectionCount(count);
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
