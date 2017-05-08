package com.d.dmusic.mvp.fragment;

import android.os.Bundle;
import android.view.View;

import com.d.commen.base.BaseFragment;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.mvp.presenter.LMMusicPresenter;
import com.d.dmusic.mvp.view.ILMMusicView;
import com.d.dmusic.view.DSLayout;
import com.d.xrv.XRecyclerView;

import butterknife.Bind;

/**
 * Created by D on 2017/4/30.
 */
public abstract class AbstractLMFragment extends BaseFragment<LMMusicPresenter> implements ILMMusicView {
    @Bind(R.id.dsl_ds)
    DSLayout dslDS;
    @Bind(R.id.xrv_list)
    XRecyclerView xrvList;

    protected boolean isVisibleToUser;
    private boolean isPrepared;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_local_sort;
    }

    @Override
    public LMMusicPresenter getPresenter() {
        return new LMMusicPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        isPrepared = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }
}
