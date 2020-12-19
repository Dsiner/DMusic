package com.d.music.local.presenter;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.component.media.media.MusicFactory;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.event.eventbus.SortTypeEvent;
import com.d.music.local.model.FileModel;
import com.d.music.local.view.IScanView;
import com.d.music.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.Comparator;
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
                List<FileModel> list = FileUtils.getFiles(path, false);
                Collections.sort(list, new Comparator<FileModel>() {
                    @Override
                    public int compare(FileModel o1, FileModel o2) {
                        return o1.name.compareTo(o2.name);
                    }
                });
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FileModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FileModel> list) {
                        if (getView() == null) {
                            return;
                        }
                        getView().loadSuccess(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 扫描音乐文件
     */
    public void scan(final List<String> paths, final int type) {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = MusicFactory.createFactory(mContext).query(paths);
                DBManager.getInstance(mContext).optMusic().deleteAll(type);
                DBManager.getInstance(mContext).optMusic().insertOrReplaceInTx(type, list);
                DBManager.getInstance(mContext).optCustomList().updateCount(type, list.size());
                DBManager.getInstance(mContext).optCustomList().updateortType(type, AppDatabase.ORDER_TYPE_TIME); // 默认按时间排序
                EventBus.getDefault().post(new SortTypeEvent(type, AppDatabase.ORDER_TYPE_TIME));

                // 更新首页自定义列表
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_INVALID, RefreshEvent.SYNC_CUSTOM_LIST));
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
                        EventBus.getDefault().post(new MusicModelEvent(type, list));

                        if (getView() == null) {
                            return;
                        }
                        getView().setMusics(list);
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
