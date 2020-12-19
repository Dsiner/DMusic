package com.d.music.online.fragment;

import com.d.lib.common.component.loader.v4.BaseLazyLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.online.adapter.MVAdapter;
import com.d.music.online.model.MVModel;
import com.d.music.online.presenter.MVPresenter;

import java.util.ArrayList;

/**
 * MVFragment
 * Created by D on 2018/8/11.
 */
public class MVFragment extends BaseLazyLoaderFragment<MVModel, MVPresenter> {

    @Override
    public MVPresenter getPresenter() {
        return new MVPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<MVModel> getAdapter() {
        return new MVAdapter(mContext, new ArrayList<MVModel>(), R.layout.module_online_adapter_mv);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getMV(page);
    }
}
