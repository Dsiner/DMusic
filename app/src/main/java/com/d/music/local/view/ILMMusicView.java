package com.d.music.local.view;


import com.d.lib.common.module.mvp.MvpView;
import com.d.music.model.AlbumModel;
import com.d.music.model.FolderModel;
import com.d.music.model.SingerModel;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * ILMMusicView
 * Created by D on 2016/6/4.
 */
public interface ILMMusicView extends MvpView {
    void setSong(List<MusicModel> models);

    void setSinger(List<SingerModel> models);

    void setAlbum(List<AlbumModel> models);

    void setFolder(List<FolderModel> models);
}
