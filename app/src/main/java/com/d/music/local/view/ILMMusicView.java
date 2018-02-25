package com.d.music.local.view;


import com.d.music.model.AlbumModel;
import com.d.music.model.FolderModel;
import com.d.music.model.SingerModel;

import java.util.List;

/**
 * ILMMusicView
 * Created by D on 2016/6/4.
 */
public interface ILMMusicView extends ISongView {
    void setSinger(List<SingerModel> models);

    void setAlbum(List<AlbumModel> models);

    void setFolder(List<FolderModel> models);
}
