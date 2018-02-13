package com.d.commen.module.loader;

import android.os.Bundle;
import android.view.View;

import com.d.commen.module.mvp.MvpBasePresenter;
import com.d.commen.module.mvp.model.BaseModel;

/**
 * ViewPage-Fragment通用分页加载Fragment
 * Created by D on 2017/8/23.
 */
public abstract class AbsLazyFragment<M extends BaseModel, P extends MvpBasePresenter> extends AbsFragment<M, P> {
    protected boolean isVisibleToUser;
    protected boolean isLazyLoaded;
    protected boolean isPrepared;

    @Override
    protected void init() {
        isPrepared = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mPresenter != null) {
            mPresenter.attachView(getMvpView());
        }
        onVisible();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            this.isVisibleToUser = true;
            onVisible();
        } else {
            this.isVisibleToUser = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (!isPrepared || !isVisibleToUser) {
            return;
        }
        isPrepared = false;//仅仅懒加载加载一次
        isLazyLoaded = true;
        initList();
        getData();
    }

    protected void onInvisible() {

    }
}
