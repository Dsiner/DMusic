package com.d.lib.common.module.loader;

import android.content.Context;

import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.model.BaseModel;

/**
 * 通用分页加载Presenter
 * Created by D on 2017/8/22.
 */
public class AbsPresenter<M extends BaseModel> extends MvpBasePresenter<IAbsView<M>> {
    public AbsPresenter(Context context) {
        super(context);
    }
}
