package com.d.dmusic.mvp.view;

import com.d.commen.mvp.MvpView;
import com.d.dmusic.model.FileModel;
import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * IScanView
 * Created by D on 2017/4/30.
 */
public interface IScanView extends MvpView {
    void setDatas(List<FileModel> models);

    void setMusics(List<MusicModel> models);

    void showLoading();

    void closeLoading();
}
