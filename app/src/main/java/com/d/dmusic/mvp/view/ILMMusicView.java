package com.d.dmusic.mvp.view;


import com.d.commen.mvp.MvpView;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.model.SingerModel;
import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * Created by D on 2016/6/4.
 */
public interface ILMMusicView extends MvpView {
    void setSong(List<MusicModel> models);

    void setSinger(List<SingerModel> models);

    void setAlbum(List<AlbumModel> models);

    void setFolder(List<FolderModel> models);

    /**
     * 设置默认态显示
     */
    void setDSState(int state);
}
