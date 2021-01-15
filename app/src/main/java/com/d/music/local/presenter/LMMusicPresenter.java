package com.d.music.local.presenter;

import android.content.Context;
import android.database.Cursor;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.util.log.ULog;
import com.d.lib.common.widget.DSLayout;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.model.FolderModel;
import com.d.music.local.model.SingerModel;
import com.d.music.local.view.ILMMusicView;
import com.d.music.widget.sort.SortUtils;

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
 * LMMusicPresenter
 * Created by D on 2017/4/30.
 */
public class LMMusicPresenter extends MvpBasePresenter<ILMMusicView> {

    public LMMusicPresenter(Context context) {
        super(context);
    }

    public void getSong(final int type, final SortUtils sortUtil) {
        if (getView() != null) {
            getView().setState(DSLayout.STATE_LOADING);
        }
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = DBManager.getInstance(mContext).optMusic()
                        .queryAll(type);
                if (list == null) {
                    list = new ArrayList<>();
                }
                if (sortUtil != null) {
                    sortUtil.sortDatas(list); // 重新排序
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
                        getView().setSong(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

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
                List<SingerModel> list = new ArrayList<>();
                Cursor cursor = DBManager.getInstance(mContext).optMusic()
                        .queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY ARTIST_NAME");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int indexSinger = cursor.getColumnIndex("ARTIST_NAME");
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
                .subscribe(new Observer<List<SingerModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<SingerModel> list) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setSinger(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

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
                List<AlbumModel> list = new ArrayList<>();
                Cursor cursor = DBManager.getInstance(mContext).optMusic()
                        .queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY ALBUM_NAME");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int indexAlbum = cursor.getColumnIndex("ALBUM_NAME");
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
                .subscribe(new Observer<List<AlbumModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AlbumModel> list) {
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

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

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
                List<FolderModel> list = new ArrayList<>();
                Cursor cursor = DBManager.getInstance(mContext).optMusic()
                        .queryBySQL("SELECT *,COUNT(*) FROM LOCAL_ALL_MUSIC GROUP BY FILE_FOLDER");
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int indexFolder = cursor.getColumnIndex("FILE_FOLDER");
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
                .subscribe(new Observer<List<FolderModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FolderModel> list) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setFolder(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

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
                        getView().setSong(list);
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