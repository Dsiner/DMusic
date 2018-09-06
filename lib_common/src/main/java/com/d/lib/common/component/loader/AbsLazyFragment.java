package com.d.lib.common.component.loader;

import android.os.Bundle;
import android.view.View;

import com.d.lib.common.component.mvp.MvpBasePresenter;

/**
 * Auto-Loader - ViewPager Fragment
 * Created by D on 2017/8/23.
 */
public abstract class AbsLazyFragment<M, P extends MvpBasePresenter> extends AbsFragment<M, P> {
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
        if (isLazyLoaded || !isPrepared || !isVisibleToUser) {
            return;
        }
        isLazyLoaded = true;//仅仅懒加载加载一次
        initList();
        getData();
    }

    protected void onInvisible() {

    }
}
