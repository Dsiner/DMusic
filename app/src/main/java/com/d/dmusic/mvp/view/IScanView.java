package com.d.dmusic.mvp.view;

import com.d.commen.mvp.MvpView;
import com.d.dmusic.model.FileModel;

import java.util.List;

/**
 * Created by D on 2017/4/30.
 */

public interface IScanView extends MvpView {
    void setDatas(List<FileModel> models);
}
