package com.d.lib.common.module.loader;

import com.d.lib.common.module.mvp.MvpView;

import java.util.List;

/**
 * 通用分页加载IView
 * Created by D on 2017/8/22.
 */
public interface IAbsView<T> extends MvpView {
    void getData();

    void setData(List<T> datas);

    void loadError();
}
