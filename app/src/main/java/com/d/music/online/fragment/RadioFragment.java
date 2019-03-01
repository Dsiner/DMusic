package com.d.music.online.fragment;

import com.d.lib.common.component.loader.v4.AbsLazyFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.online.adapter.RadioAdapter;
import com.d.music.online.model.RadioModel;
import com.d.music.online.presenter.RadioPresenter;

import java.util.ArrayList;

/**
 * RadioFragment
 * Created by D on 2018/8/11.
 */
public class RadioFragment extends AbsLazyFragment<RadioModel, RadioPresenter> {

    @Override
    public RadioPresenter getPresenter() {
        return new RadioPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<RadioModel> getAdapter() {
        return new RadioAdapter(mContext, new ArrayList<RadioModel>(), R.layout.module_online_adapter_radio);
    }

    @Override
    protected void initList() {
        mXrvList.setCanRefresh(false);
        mXrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getRadio();
    }
}
