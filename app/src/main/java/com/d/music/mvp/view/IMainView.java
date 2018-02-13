package com.d.music.mvp.view;


import com.d.commen.module.mvp.MvpView;
import com.d.music.module.greendao.music.CustomList;

import java.util.List;

/**
 * IMainView
 * Created by D on 2016/6/4.
 */
public interface IMainView extends MvpView {
    void setCustomList(List<CustomList> models);

    void setLocalAllCount(int count);

    void setCollectionCount(int count);
}
