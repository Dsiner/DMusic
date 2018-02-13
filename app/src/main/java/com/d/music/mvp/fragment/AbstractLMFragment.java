package com.d.music.mvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.commen.module.mvp.base.BaseFragment;
import com.d.commen.module.mvp.MvpView;
import com.d.lib.xrv.XRecyclerView;
import com.d.music.R;
import com.d.music.mvp.presenter.LMMusicPresenter;
import com.d.music.mvp.view.ILMMusicView;
import com.d.commen.view.DSLayout;
import com.d.music.view.sort.SideBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;

/**
 * LazyLoad Fragment
 * Created by D on 2017/4/30.
 */
public abstract class AbstractLMFragment extends BaseFragment<LMMusicPresenter> implements ILMMusicView {
    @Bind(R.id.dsl_ds)
    DSLayout dslDS;
    @Bind(R.id.xrv_list)
    XRecyclerView xrvList;
    @Bind(R.id.sb_sidebar)
    SideBar sbSideBar;

    protected boolean isVisibleToUser;
    protected boolean isLazyLoaded;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
        isLazyLoaded = true;
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
