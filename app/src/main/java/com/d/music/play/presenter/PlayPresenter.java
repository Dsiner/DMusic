package com.d.music.play.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.music.common.Constants;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;
import com.d.music.module.media.HitTarget;
import com.d.music.module.media.controler.MediaControler;
import com.d.music.module.media.controler.MediaPlayerManager;
import com.d.music.play.view.IPlayView;
import com.d.music.utils.FileUtil;
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

    public PlayPresenter(Context context) {
        super(context);
    }

    public void overLoad() {
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

    public void getLrcRows(final MusicModel model) {
        if (getView() == null || model == null) {
            return;
        }
        final String path = HitTarget.hitLrc(model);
        Observable.create(new ObservableOnSubscribe<LrcModel>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LrcModel> e) throws Exception {
                LrcModel lrcModel = new LrcModel();
                lrcModel.model = model;
                lrcModel.lrcRows = new ArrayList<>();
                if (FileUtil.isFileExist(path)) {
                    lrcModel.lrcRows = DefaultLrcParser.getLrcRows(path);
                }
                lrcModel.lrcRows = lrcModel.lrcRows != null ? lrcModel.lrcRows : new ArrayList<LrcRow>();
                e.onNext(lrcModel);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LrcModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LrcModel lrcModel) {
                        if (getView() == null || lrcModel.model != model) {
                            return;
                        }
                        MediaControler control = MediaControler.getIns(mContext);
                        MediaPlayerManager mediaManager = control.getMediaManager();
                        final int status = control.getStatus();
                        if (mediaManager != null && (status == Constants.PlayStatus.PLAY_STATUS_PLAYING
                                || status == Constants.PlayStatus.PLAY_STATUS_PAUSE)) {
                            final int currentPosition = mediaManager.getCurrentPosition();
                            getView().setLrcRows(lrcModel.lrcRows, currentPosition);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @android.support.annotation.NonNull
    private String hitLrc(MusicModel model, String tempPath) {
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.lrcUrl;
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = model.fileFolder + "/" + model.songName + ".lrc";
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.download + model.songName + ".lrc";
        }
        if (TextUtils.isEmpty(tempPath) || !FileUtil.isFileExist(tempPath)) {
            tempPath = Constants.Path.cache + model.songName + ".lrc";
        }
        return tempPath;
    }

    static class LrcModel {
        MusicModel model;
        List<LrcRow> lrcRows;
    }
}
