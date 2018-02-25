package com.d.music.mvp.view;

import com.d.lib.common.module.mvp.MvpView;
import com.d.music.model.FileModel;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * IScanView
 * Created by D on 2017/4/30.
 */
public interface IScanView extends MvpView {
    void setDatas(List<FileModel> models);

    void setMusics(List<MusicModel> models);
}
