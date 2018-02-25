package com.d.music.mvp.presenter;

import android.content.Context;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.utils.TaskManager;
import com.d.lib.common.view.DSLayout;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.mvp.view.ISongView;

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
 * SongPresenter
 * Created by D on 2017/4/30.
 */
public class SongPresenter extends MvpBasePresenter<ISongView> {

    public SongPresenter(Context context) {
        super(context);
    }

    public void getSong(final int type, final int tab, final String sortKey, final int orderType) {
        if (type >= MusicDB.CUSTOM_MUSIC_INDEX && type < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT) {
            //自定义歌曲
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
                if (type == MusicDB.LOCAL_ALL_MUSIC && tab > 0) {
                    switch (tab) {
                        case 1:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllBySinger(sortKey);
                            break;
                        case 2:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllByAlbum(sortKey);
                            break;
                        case 3:
                            list = MusicDBUtil.getInstance(mContext).queryLocalAllByFolder(sortKey);
                            break;
                    }
                } else {
                    list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllMusic(type);
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setSong(list);
                    }
                });
    }

    /**
     * 获取歌曲
     *
     * @param type:仅限自定义歌曲
     * @param orderType:排序类型:按名称、时间、自定义
     */
    public void getSong(final int type, final int orderType) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                MusicDBUtil.getInstance(mContext).updateCusListSoryByType(type, orderType);
                List<MusicModel> list = null;
                list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllCustomMusic(type, orderType);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setSong(list);
                    }
                });
    }

    public void setSong(final List<MusicModel> models, final int type) {
        if (models == null) {
            return;
        }
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                for (MusicModel model : models) {
                    model.isSortChecked = false;
                }
                MusicDBUtil.getInstance(mContext).deleteAll(type);
                MusicDBUtil.getInstance(mContext).insertOrReplaceMusicInTx(MusicModel.clone(models, type), type);
                MusicDBUtil.getInstance(mContext).updateCusListCount(type, models.size());
                MusicDBUtil.getInstance(mContext).updateCusListSoryByType(type, MusicDB.ORDER_TYPE_CUSTOM);//按自定义排序
            }
        });
    }

    /**
     * 所有下拉菜单，收起
     */
    public void subPullUp(final List<MusicModel> datas) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = new ArrayList<MusicModel>();
                list.addAll(datas);
                for (MusicModel m : list) {
                    if (m != null) {
                        m.isChecked = false;
                    }
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        getView().setSong(list);
                    }
                });
    }
}
