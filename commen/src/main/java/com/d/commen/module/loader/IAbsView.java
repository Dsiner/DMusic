package com.d.commen.module.loader;

import com.d.commen.module.mvp.MvpView;

import java.util.ArrayList;

/**
 * 通用分页加载IView
 * Created by D on 2017/8/22.
 */
public interface IAbsView<T> extends MvpView {
    void getData();

    void setData(ArrayList<T> datas);

    void loadError();
}
