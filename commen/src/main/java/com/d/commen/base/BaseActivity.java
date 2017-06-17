package com.d.commen.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;

import butterknife.ButterKnife;
import cn.feng.skin.manager.base.BaseSkinActivity;

/**
 * BaseActivity
 * Created by D on 2017/4/27.
 */
public abstract class BaseActivity<T extends MvpBasePresenter> extends BaseSkinActivity {
    protected T mPresenter;
    protected View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        mPresenter = getPresenter();
        mPresenter.attachView(getMvpView());
        init();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView(false);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    public abstract T getPresenter();

    protected abstract MvpView getMvpView();

    protected abstract void init();
}
