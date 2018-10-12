package com.d.music.play.view;


import com.d.lib.common.component.mvp.MvpView;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

/**
 * IMainView
 * Created by D on 2017/6/2.
 */
public interface IPlayView extends MvpView {
    void overLoad(List<MusicModel> list);
}
