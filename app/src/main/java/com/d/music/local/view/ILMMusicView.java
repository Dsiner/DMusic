package com.d.music.local.view;


import com.d.lib.common.component.mvp.MvpView;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.model.FolderModel;
import com.d.music.local.model.SingerModel;
import com.d.music.component.greendao.bean.MusicModel;

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
