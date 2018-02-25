package com.d.music.play.view;


import com.d.lib.common.module.mvp.MvpView;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.view.lrc.LrcRow;

import java.util.List;

/**
 * IMainView
 * Created by D on 2017/6/2.
 */
public interface IPlayView extends MvpView {
    void reLoad(List<MusicModel> list);

    void setLrcRows(String path, List<LrcRow> lrcRows);

    void seekTo(int progress);
}
