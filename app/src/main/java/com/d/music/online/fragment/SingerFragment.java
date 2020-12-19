package com.d.music.online.fragment;

import com.d.lib.common.component.loader.v4.BaseLazyLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.online.adapter.SingerAdapter;
import com.d.music.online.model.SingerModel;
import com.d.music.online.presenter.SingerPresenter;

import java.util.ArrayList;

/**
 * SingerFragment
 * Created by D on 2018/8/11.
 */
public class SingerFragment extends BaseLazyLoaderFragment<SingerModel, SingerPresenter> {

    @Override
    public SingerPresenter getPresenter() {
        return new SingerPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<SingerModel> getAdapter() {
        return new SingerAdapter(mContext, new ArrayList<SingerModel>(), R.layout.module_online_adapter_singer);
    }

    @Override
    protected void initList() {
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSinger();
    }
}
