package com.d.dmusic.mvp.view;


import com.d.commen.mvp.MvpView;
import com.d.dmusic.module.greendao.music.CustomList;

import java.util.List;

/**
 * Created by D on 2016/6/4.
 */
public interface IMainView extends MvpView {
    void setCustomList(List<CustomList> models);

    void setLocalAllCount(int count);

    void setCollectionCount(int count);
}
