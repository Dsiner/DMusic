package com.d.music.play.fragment;

import android.view.View;
import android.widget.TextView;

import com.d.lib.common.component.loader.AbsFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.play.adapter.FlowTagAdapter;
import com.d.music.play.adapter.SearchAdapter;
import com.d.music.play.presenter.SearchPresenter;
import com.d.music.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;

/**
 * SearchFragment
 * Created by D on 2018/8/13.
 **/
public class SearchFragment extends AbsFragment<MusicModel, SearchPresenter> {
    @BindView(R.id.fl_flow)
    FlowLayout flFlow;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    private FlowTagAdapter flowTagAdapter;

    @Override
    public SearchPresenter getPresenter() {
        return new SearchPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_play_fragment_search;
    }

    @Override
    protected void init() {
        super.init();
        flowTagAdapter = new FlowTagAdapter(mContext, new ArrayList<String>(), R.layout.module_play_adapter_tag);
        flFlow.setAdapter(flowTagAdapter);
        flowTagAdapter.setDatas(Arrays.asList("臧鸿 飞见面吧电台", "高圆圆", "SOLO", "月亮惹的祸", "杨超越",
                "灾", "never be alone"));
        flowTagAdapter.notifyDataSetChanged();
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flowTagAdapter.setDatas(Arrays.asList("Apple", "Orange", "Circle", "Big", "Apple",
                        "Apple", "Apple", "Apple", "Apple", "Apple", "Apple", "Apple", "Apple", "Apple", "Apple"));
                flowTagAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        return new SearchAdapter(mContext, new ArrayList<MusicModel>(), R.layout.module_online_adapter_bill);
    }

    @Override
    protected void onLoad(int page) {

    }

    public boolean onBackPressed() {
        return false;
    }
}
