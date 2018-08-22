package com.d.music.play.view;


import com.d.lib.common.module.mvp.MvpView;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.view.lrc.LrcRow;

import java.util.List;

/**
 * IMainView
 * Created by D on 2017/6/2.
 */
public interface IPlayView extends MvpView {
    void overLoad(List<MusicModel> list);

    void setLrcRows(List<LrcRow> lrcRows);

    void seekTo(int progress);
}
