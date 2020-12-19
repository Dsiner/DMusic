package com.d.lib.common.component.loader;

import android.content.Context;

import com.d.lib.common.component.mvp.MvpBasePresenter;

/**
 * Auto-Loader - Presenter
 * Created by D on 2017/8/22.
 */
public class MvpBaseLoaderPresenter<M> extends MvpBasePresenter<MvpBaseLoaderView<M>> {
    public MvpBaseLoaderPresenter(Context context) {
        super(context);
    }
}
