package com.d.music.local.view;


import com.d.lib.common.component.mvp.MvpBaseView;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.model.FolderModel;
import com.d.music.local.model.SingerModel;

import java.util.List;

/**
 * ILMMusicView
 * Created by D on 2016/6/4.
 */
public interface ILMMusicView extends MvpBaseView {
    void setSong(List<MusicModel> models);

    void setSinger(List<SingerModel> models);

    void setAlbum(List<AlbumModel> models);

    void setFolder(List<FolderModel> models);
}
