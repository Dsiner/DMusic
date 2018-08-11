package com.d.music.local.presenter;

import android.content.Context;
import android.database.Cursor;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.common.view.DSLayout;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.model.FolderModel;
import com.d.music.local.model.SingerModel;
import com.d.music.local.view.ILMMusicView;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.view.sort.SortUtil;

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
 * LMMusicPresenter
 * Created by D on 2017/4/30.
 */
public class LMMusicPresenter extends MvpBasePresenter<ILMMusicView> {

    public LMMusicPresenter(Context context) {
        super(context);
    }

    public void getSong(final int type, final SortUtil sortUtil) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = (List<MusicModel>) MusicDBUtil.getInstance(mContext).queryAllMusic(type);
                if (list == null) {
                    list = new ArrayList<MusicModel>();
                }
                if (sortUtil != null) {
                    sortUtil.sortDatas(list);//重新排序
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

    public void getSinger() {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<SingerModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<SingerModel>> e) throws Exception {
                List<SingerModel> list = new ArrayList<SingerModel>();
                Cursor cursor = MusicDBUtil.getInstance(mContext).queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY SINGER");
                if (null != cursor && cursor.moveToFirst()) {
                    do {
                        int indexSinger = cursor.getColumnIndex("SINGER");
                        int indexCount = cursor.getColumnIndex("COUNT(*)");
                        if (indexSinger != -1 && indexCount != -1) {
                            SingerModel model = new SingerModel();
                            String singer = cursor.getString(indexSinger);
                            int count = cursor.getInt(indexCount);
                            model.singer = singer;
                            model.count = count;
                            list.add(model);
                            ULog.d("Singer----" + "singer:" + singer + "-count:" + count);
                        }
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SingerModel>>() {
                    @Override
                    public void accept(@NonNull List<SingerModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        if (list.size() <= 0) {
                            getView().setState(DSLayout.STATE_EMPTY);
                        } else {
                            getView().setState(DSLayout.GONE);
                        }
                        getView().setSinger(list);
                    }
                });
    }

    public void getAlbum() {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<AlbumModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<AlbumModel>> e) throws Exception {
                List<AlbumModel> list = new ArrayList<AlbumModel>();
                Cursor cursor = MusicDBUtil.getInstance(mContext).queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY ALBUM");
                if (null != cursor && cursor.moveToFirst()) {
                    do {
                        int indexAlbum = cursor.getColumnIndex("ALBUM");
                        int indexCount = cursor.getColumnIndex("COUNT(*)");
                        if (indexAlbum != -1 && indexCount != -1) {
                            AlbumModel albumModel = new AlbumModel();
                            String album = cursor.getString(indexAlbum);
                            int count = cursor.getInt(indexCount);
                            albumModel.album = album;
                            albumModel.count = count;
                            list.add(albumModel);
                        }
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AlbumModel>>() {
                    @Override
                    public void accept(@NonNull List<AlbumModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        if (list.size() <= 0) {
                            getView().setState(DSLayout.STATE_EMPTY);
                        } else {
                            getView().setState(DSLayout.GONE);
                        }
                        getView().setAlbum(list);
                    }
                });
    }

    public void getFolder() {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<FolderModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FolderModel>> e) throws Exception {
                List<FolderModel> list = new ArrayList<FolderModel>();
                Cursor cursor = MusicDBUtil.getInstance(mContext).queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY FOLDER");
                if (null != cursor && cursor.moveToFirst()) {
                    do {
                        int indexFolder = cursor.getColumnIndex("FOLDER");
                        int indexCount = cursor.getColumnIndex("COUNT(*)");
                        if (indexFolder != -1 && indexCount != -1) {
                            FolderModel model = new FolderModel();
                            String folder = cursor.getString(indexFolder);
                            int count = cursor.getInt(indexCount);
                            model.folder = folder;
                            model.count = count;
                            list.add(model);
                        }
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FolderModel>>() {
                    @Override
                    public void accept(@NonNull List<FolderModel> list) throws Exception {
                        if (getView() == null) {
                            return;
                        }
                        if (list.size() <= 0) {
                            getView().setState(DSLayout.STATE_EMPTY);
                        } else {
                            getView().setState(DSLayout.GONE);
                        }
                        getView().setFolder(list);
                    }
                });
    }

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