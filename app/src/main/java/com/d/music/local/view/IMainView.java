package com.d.music.local.view;


import com.d.lib.common.module.mvp.MvpView;
import com.d.music.module.greendao.bean.CustomListModel;

import java.util.List;

/**
 * IMainView
 * Created by D on 2016/6/4.
 */
public interface IMainView extends MvpView {
    void setCustomList(List<CustomListModel> models);

    void setLocalAllCount(int count);

    void setCollectionCount(int count);
}
