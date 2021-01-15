package com.d.music.local.presenter;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.taskscheduler.TaskScheduler;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.local.fragment.AbstractLMFragment;
import com.d.music.local.view.ISongView;

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
 * SongPresenter
 * Created by D on 2017/4/30.
 */
public class SongPresenter extends MvpBasePresenter<ISongView> {

    public SongPresenter(Context context) {
        super(context);
    }

    public void getSong(final int type, final int tab, final String sortKey, final int orderType) {
        if (type >= AppDatabase.CUSTOM_MUSIC_INDEX && type < AppDatabase.CUSTOM_MUSIC_INDEX + AppDatabase.CUSTOM_MUSIC_COUNT) {
            // 自定义歌曲
            getSong(type, orderType);
            return;
        }
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = null;
                if (type == AppDatabase.LOCAL_ALL_MUSIC && tab > AbstractLMFragment.TYPE_SONG) {
                    switch (tab) {
                        case AbstractLMFragment.TYPE_SINGER:
                            list = DBManager.getInstance(mContext).optMusic().queryLocalAllBySinger(sortKey);
                            break;
                        case AbstractLMFragment.TYPE_ALBUM:
                            list = DBManager.getInstance(mContext).optMusic().queryLocalAllByAlbum(sortKey);
                            break;
                        case AbstractLMFragment.TYPE_FOLDER:
                            list = DBManager.getInstance(mContext).optMusic().queryLocalAllByFolder(sortKey);
                            break;
                    }
                } else {
                    list = DBManager.getInstance(mContext).optMusic().queryAll(type);
                }
                list = list != null ? list : new ArrayList<MusicModel>();
                e.onNext(list);
                e.onComplete();
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
     * 获取歌曲
     *
     * @param type:      仅限自定义歌曲
     * @param orderType: 排序类型:按名称、时间、自定义
     */
    public void getSong(final int type, final int orderType) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                DBManager.getInstance(mContext).optCustomList().updateortType(type, orderType);
                List<MusicModel> list = DBManager.getInstance(mContext).optCustomList()
                        .queryAllCustomMusic(type, orderType);
                list = list != null ? list : new ArrayList<MusicModel>();
                e.onNext(list);
                e.onComplete();
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

    public void setSong(final List<MusicModel> models, final int type) {
        if (models == null) {
            return;
        }
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                for (MusicModel model : models) {
                    model.exIsSortChecked = false;
                }
                DBManager.getInstance(mContext).optMusic().deleteAll(type);
                DBManager.getInstance(mContext).optMusic().insertOrReplaceInTx(type, models);
                DBManager.getInstance(mContext).optCustomList().updateCount(type, models.size());
                DBManager.getInstance(mContext).optCustomList().updateortType(type, AppDatabase.ORDER_TYPE_CUSTOM); // 按自定义排序
            }
        });
    }

    /**
     * 所有下拉菜单，收起
     */
    public void subPullUp(@androidx.annotation.NonNull final List<MusicModel> datas) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = new ArrayList<>(datas);
                for (MusicModel m : list) {
                    if (m != null) {
                        m.exIsChecked = false;
                    }
                }
                e.onNext(list);
                e.onComplete();
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
}
