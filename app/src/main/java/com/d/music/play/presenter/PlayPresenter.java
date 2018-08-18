package com.d.music.play.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.module.greendao.db.AppDB;
import com.d.music.module.greendao.util.AppDBUtil;
import com.d.music.play.view.IPlayView;
import com.d.music.utils.fileutil.FileUtil;
import com.d.music.view.lrc.DefaultLrcParser;
import com.d.music.view.lrc.LrcRow;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private String lrcUrl;

    public PlayPresenter(Context context) {
        super(context);
    }

    public void reLoad() {
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
                        getView().reLoad(list);
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
        final String path = !TextUtils.isEmpty(model.lrcUrl) ? model.lrcUrl
                : model.fileFolder + "/" + model.songName + ".lrc";
        lrcUrl = path;
        Observable.create(new ObservableOnSubscribe<List<LrcRow>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LrcRow>> e) throws Exception {
                List<LrcRow> list = null;
                if (FileUtil.isFileExist(path)) {
                    list = DefaultLrcParser.getInstance().getLrcRows(converfile(path));
                }
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LrcRow>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<LrcRow> list) {
                        if (getView() == null || !TextUtils.equals(lrcUrl, path)) {
                            return;
                        }
                        getView().setLrcRows(path, list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String converfile(String path) {
        File file = new File(path);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedReader reader;
        String text = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            bis.mark(4);
            byte[] first3bytes = new byte[3];
            //找到文档的前三个字节并自动判断文档类型
            bis.read(first3bytes);
            bis.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB && first3bytes[2] == (byte) 0xBF) {
                reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
                reader = new BufferedReader(new InputStreamReader(bis, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(bis, "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(bis, "utf-16le"));
            } else {
                reader = new BufferedReader(new InputStreamReader(bis, "GBK"));
            }
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }
}
