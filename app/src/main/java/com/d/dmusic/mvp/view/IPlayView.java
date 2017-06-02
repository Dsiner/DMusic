package com.d.dmusic.mvp.view;


import com.d.commen.mvp.MvpView;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.view.lrc.LrcRow;

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
