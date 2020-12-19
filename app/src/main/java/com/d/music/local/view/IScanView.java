package com.d.music.local.view;

import com.d.lib.common.component.mvp.MvpView;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.local.model.FileModel;

import java.util.List;

/**
 * IScanView
 * Created by D on 2017/4/30.
 */
public interface IScanView extends MvpView {
    void loadSuccess(List<FileModel> models);

    void setMusics(List<MusicModel> models);
}
