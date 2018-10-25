package com.d.music.play.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.component.loader.AbsFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.view.ClearEditText;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.play.adapter.FlowTagAdapter;
import com.d.music.play.adapter.SearchAdapter;
import com.d.music.play.presenter.SearchPresenter;
import com.d.music.play.view.ISearchView;
import com.d.music.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * SearchFragment
 * Created by D on 2018/8/13.
 **/
public class SearchFragment extends AbsFragment<MusicModel, SearchPresenter> implements ISearchView {
    @BindView(R.id.cet_edit)
    ClearEditText cetEdit;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.fl_flow)
    FlowLayout flFlow;

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
        cetEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvSearch.setText(s.toString().length() > 0
                        ? getResources().getString(R.string.module_common_search)
                        : getResources().getString(R.string.lib_pub_cancel));
            }
        });
        flowTagAdapter = new FlowTagAdapter(mContext, new ArrayList<SearchHotRespModel.HotsBean>(),
                R.layout.module_play_adapter_search_tag);
        flFlow.setAdapter(flowTagAdapter);
        flowTagAdapter.notifyDataSetChanged();
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.search(cetEdit.getText().toString(), 0, 10);
            }
        });
    }

    @Override
    protected void initList() {
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        return new SearchAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_play_adapter_search_history);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSearchHot();
    }

    private List<MusicModel> getTsData() {
        List<MusicModel> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MusicModel model = new MusicModel();
            model.songName = "" + i;
            list.add(model);
        }
        return list;
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void getSearchHotSuccess(List<SearchHotRespModel.HotsBean> datas) {
        flowTagAdapter.setDatas(datas);
        flowTagAdapter.notifyDataSetChanged();
    }

    @Override
    public void getSearchHotError() {

    }
}
