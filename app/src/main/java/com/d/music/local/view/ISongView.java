package com.d.music.local.view;


import com.d.lib.common.module.mvp.MvpView;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * ISongView
 * Created by D on 2016/6/4.
 */
public interface ISongView extends MvpView {
    void setSong(List<MusicModel> models);

    void notifyDataCountChanged(int count);
}
