package com.d.lib.common.module.loader;

import android.os.Bundle;
import android.view.View;

import com.d.lib.common.R;
import com.d.lib.common.module.mvp.MvpBasePresenter;
import com.d.lib.common.module.mvp.base.BaseFragment;
import com.d.lib.common.module.mvp.model.BaseModel;
import com.d.lib.common.utils.ViewHelper;
import com.d.lib.common.view.DSLayout;
import com.d.lib.xrv.XRecyclerView;
import com.d.lib.xrv.adapter.CommonAdapter;

import java.util.List;

/**
 * 通用分页加载Fragment
 * Created by D on 2017/8/23.
 */
public abstract class AbsFragment<M extends BaseModel, P extends MvpBasePresenter> extends BaseFragment<P> implements IAbsView<M>, View.OnClickListener {
    protected XRecyclerView xrvList;

    protected CommonAdapter<M> adapter;
    protected CommonLoader<M> commonLoader;

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.btn_dsl) {
            getData();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_fragment_abs;
    }

    @Override
    protected int getDSLayoutRes() {
        return R.id.dsl_ds;
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        xrvList = ViewHelper.findView(rootView, R.id.xrv_list);

        ViewHelper.setOnClick(rootView, this, R.id.btn_dsl);
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
        adapter = getAdapter();
        xrvList.showAsList();
        xrvList.setAdapter(adapter);
        commonLoader = new CommonLoader<M>(xrvList, adapter);
        commonLoader.setPageCount(CommonLoader.PAGE_COUNT);//每页数据数
        commonLoader.setOnLoaderListener(new CommonLoader.OnLoaderListener() {
            @Override
            public void onRefresh() {
                onLoad(commonLoader.page);
            }

            @Override
            public void onLoadMore() {
                onLoad(commonLoader.page);
            }

            @Override
            public void loadSuccess() {
                dslDs.setState(DSLayout.GONE);
                xrvList.setVisibility(View.VISIBLE);
            }

            @Override
            public void noContent() {
                dslDs.setState(DSLayout.STATE_EMPTY);
            }

            @Override
            public void loadError(boolean isEmpty) {
                dslDs.setState(isEmpty ? DSLayout.STATE_NET_ERROR : DSLayout.GONE);
            }
        });
    }

    @Override
    public void getData() {
        commonLoader.page = 1;
        xrvList.setVisibility(View.GONE);
        dslDs.setState(DSLayout.STATE_LOADING);
        onLoad(commonLoader.page);
    }

    @Override
    public void setData(List<M> datas) {
        commonLoader.setData(datas);
    }

    @Override
    public void loadError() {
        commonLoader.loadError();
    }

    /**
     * Return the adapter
     */
    protected abstract CommonAdapter<M> getAdapter();

    /**
     * Auto call this func to load data...
     *
     * @param page: from 1 to ...
     */
    protected abstract void onLoad(int page);
}
