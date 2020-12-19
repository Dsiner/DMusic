package com.d.lib.common.component.loader.v4;

import android.os.Bundle;
import android.view.View;

import com.d.lib.common.R;
import com.d.lib.common.component.loader.MvpBaseLoaderView;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.pulllayout.Refreshable;
import com.d.lib.pulllayout.loader.CommonLoader;
import com.d.lib.pulllayout.loader.RecyclerAdapter;
import com.d.lib.pulllayout.util.RefreshableCompat;

import java.util.List;

/**
 * Auto-Loader - Fragment
 * Created by D on 2017/8/23.
 */
public abstract class BaseLoaderFragment<M, P extends MvpBasePresenter>
        extends BaseFragment<P>
        implements MvpBaseLoaderView<M>, View.OnClickListener {

    protected Refreshable mPullList;
    protected RecyclerAdapter<M> mAdapter;
    protected CommonLoader<M> mCommonLoader;

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.btn_dsl) {
            getData();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_fragment_loader;
    }

    @Override
    protected int getDSLayoutRes() {
        return R.id.dsl_ds;
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        mPullList = ViewHelper.findViewById(rootView, R.id.pull_list);

        ViewHelper.setOnClickListener(rootView, this, R.id.btn_dsl);
    }

    @Override
    protected void init() {
        initList();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    protected void initList() {
        mAdapter = getAdapter();
        RefreshableCompat.setAdapter(mPullList, mAdapter);
        mCommonLoader = new CommonLoader<>(mPullList, mAdapter);
        mCommonLoader.setOnLoaderListener(new CommonLoader.OnLoaderListener() {
            @Override
            public void onRefresh() {
                onLoad(mCommonLoader.page);
            }

            @Override
            public void onLoadMore() {
                onLoad(mCommonLoader.page);
            }

            @Override
            public void loadSuccess() {
                mDslDs.setState(DSLayout.GONE);
                mPullList.setVisibility(View.VISIBLE);
            }

            @Override
            public void noContent() {
                mDslDs.setState(DSLayout.STATE_EMPTY);
            }

            @Override
            public void loadError(boolean isEmpty) {
                mDslDs.setState(isEmpty ? DSLayout.STATE_NET_ERROR : DSLayout.GONE);
            }
        });
    }

    @Override
    public void getData() {
        mCommonLoader.page = 1;
        mPullList.setVisibility(View.GONE);
        mDslDs.setState(DSLayout.STATE_LOADING);
        onLoad(mCommonLoader.page);
    }

    @Override
    public void loadSuccess(List<M> datas) {
        mCommonLoader.loadSuccess(datas);
    }

    @Override
    public void loadError() {
        mCommonLoader.loadError();
    }

    /**
     * Return the adapter
     */
    protected abstract RecyclerAdapter<M> getAdapter();

    /**
     * Auto call this func to load data...
     *
     * @param page: from 1 to ...
     */
    protected abstract void onLoad(int page);
}
