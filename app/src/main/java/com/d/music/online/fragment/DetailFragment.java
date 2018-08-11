package com.d.music.online.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.d.lib.common.module.loader.AbsFragment;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.view.TitleLayout;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.online.activity.DetailActivity;
import com.d.music.online.adapter.DetailAdapter;
import com.d.music.online.presenter.MusicPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * DetailFragment
 * Created by D on 2018/8/12.
 */
public class DetailFragment extends AbsFragment<MusicModel, MusicPresenter> {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;

    private int type;
    private String title, args;

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                getActivity().finish();
                break;
        }
    }

    @Override
    public MusicPresenter getPresenter() {
        return new MusicPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_activity_abs;
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        return new DetailAdapter(mContext, new ArrayList<MusicModel>(), R.layout.module_online_adapter_music);
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type", DetailActivity.TYPE_BILL);
            args = bundle.getString("args");
            title = bundle.getString("title");
        }
        tlTitle.setText(R.id.tv_title_title, !TextUtils.isEmpty(title) ? title : "音乐");
        super.init();
    }

    @Override
    protected void initList() {
        xrvList.setCanRefresh(false);
        if (type == DetailActivity.TYPE_RADIO) {
            xrvList.setCanLoadMore(false);
        }
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        if (type == DetailActivity.TYPE_BILL) {
            mPresenter.getBillSongs(args, page);
        } else if (type == DetailActivity.TYPE_RADIO) {
            mPresenter.getRadioSongs(args, page);
        }
    }
}
