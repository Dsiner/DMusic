package com.d.music.online.fragment;

import com.d.lib.common.component.loader.AbsLazyFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.online.adapter.BillAdapter;
import com.d.music.online.model.BillModel;
import com.d.music.online.presenter.BillPresenter;

import java.util.ArrayList;

/**
 * BillFragment
 * Created by D on 2018/8/11.
 */
public class BillFragment extends AbsLazyFragment<BillModel, BillPresenter> {

    @Override
    public BillPresenter getPresenter() {
        return new BillPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<BillModel> getAdapter() {
        return new BillAdapter(mContext, new ArrayList<BillModel>(), R.layout.module_online_adapter_bill);
    }

    @Override
    protected void initList() {
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getBill();
    }
}
