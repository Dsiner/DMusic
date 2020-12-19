package com.d.lib.common.component.loader.v4;

import android.os.Bundle;
import android.view.View;

import com.d.lib.common.component.mvp.MvpBasePresenter;

/**
 * Auto-Loader - ViewPager Fragment
 * Created by D on 2017/8/23.
 */
public abstract class BaseLazyLoaderFragment<M, P extends MvpBasePresenter>
        extends BaseLoaderFragment<M, P> {

    protected boolean mIsVisibleToUser;
    protected boolean mIsLazyLoaded;

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
            this.mIsVisibleToUser = true;
            onVisible();
        } else {
            this.mIsVisibleToUser = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (mIsLazyLoaded || !mIsPrepared || !mIsVisibleToUser) {
            return;
        }
        mIsLazyLoaded = true; // Just lazy loading once
        getData();
    }

    protected void onInvisible() {

    }
}
