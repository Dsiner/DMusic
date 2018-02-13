package com.d.music.mvp.view;


import com.d.commen.module.mvp.MvpView;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * ISongView
 * Created by D on 2016/6/4.
 */
public interface ISongView extends MvpView {
    void setSong(List<MusicModel> models);

    /**
     * 设置默认态显示
     */
    void setDSState(int state);

    void notifyDataCountChanged(int count);
}
