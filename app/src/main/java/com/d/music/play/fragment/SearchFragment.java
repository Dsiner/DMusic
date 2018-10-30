package com.d.music.play.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.component.loader.AbsFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.utils.Util;
import com.d.lib.common.utils.ViewHelper;
import com.d.lib.common.view.ClearEditText;
import com.d.lib.common.view.DSLayout;
import com.d.lib.xrv.XRecyclerView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.preferences.Preferences;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.play.adapter.FlowTagAdapter;
import com.d.music.play.adapter.SearchAdapter;
import com.d.music.play.adapter.SearchHistoryAdapter;
import com.d.music.play.presenter.SearchPresenter;
import com.d.music.play.view.ISearchView;
import com.d.music.view.flowlayout.FlowLayout;
import com.google.gson.reflect.TypeToken;

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
    @BindView(R.id.llyt_float_search_history)
    View llytFloatSearchHistory;
    @BindView(R.id.flyt_float_search)
    View flytFloatSearch;
    @BindView(R.id.xrv_list_history)
    XRecyclerView xrvListHistory;

    private FlowTagAdapter flowTagAdapter;
    private String tag;

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.tv_search) {
            if (getResources().getString(R.string.module_common_search)
                    .equals(tvSearch.getText().toString())) {
                swithMode(true);
                setData(new ArrayList<MusicModel>());
                getData();
            } else if (getResources().getString(R.string.lib_pub_cancel)
                    .equals(tvSearch.getText().toString())) {
                if (isSearching()) {
                    swithMode(false);
                } else {
                    getActivity().finish();
                }
            }
        }
    }

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
    protected void bindView(View rootView) {
        super.bindView(rootView);
        ViewHelper.setOnClick(rootView, this, R.id.tv_search);
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
        flowTagAdapter.setOnClickListener(new FlowTagAdapter.OnClickListener() {
            @Override
            public void onClick(View v, String tag) {
                cetEdit.setText(tag);
                cetEdit.setSelection(cetEdit.getText().toString().length());
                tvSearch.setText(getResources().getString(R.string.lib_pub_cancel));
                swithMode(true);
                setData(new ArrayList<MusicModel>());
                getData();
            }
        });
        flFlow.setAdapter(flowTagAdapter);

        String json = Preferences.getIns(mContext).getSearchHot();
        if (!TextUtils.isEmpty(json)) {
            List<SearchHotRespModel.HotsBean> datas = Util.getGsonIns().fromJson(json,
                    new TypeToken<List<SearchHotRespModel.HotsBean>>() {
                    }.getType());
            if (datas != null && datas.size() > 0) {
                flowTagAdapter.setDatas(datas);
                flowTagAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initList() {
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(true);
        super.initList();
        xrvListHistory.setCanRefresh(false);
        xrvListHistory.setCanLoadMore(false);
        xrvListHistory.setAdapter(new SearchHistoryAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_play_adapter_search_history));
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        return new SearchAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_play_adapter_search);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getSearchHot();
    }

    private boolean isSearching() {
        return flytFloatSearch.getVisibility() == View.VISIBLE;
    }

    private void swithMode(boolean searching) {
        if (!searching) {
            cetEdit.setText("");
        }
        tag = searching ? cetEdit.getText().toString() : "";
        llytFloatSearchHistory.setVisibility(searching ? View.GONE : View.VISIBLE);
        flytFloatSearch.setVisibility(searching ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onLoad(int page) {
        if (TextUtils.isEmpty(tag)) {
            setState(DSLayout.STATE_EMPTY);
            return;
        }
        int start = page - 1;
        int count = 15;
        mPresenter.search(tag, start, count);
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
        if (datas.size() > 0) {
            String json = Util.getGsonIns().toJson(datas);
            Preferences.getIns(mContext).putSearchHot(json);
        }
        flowTagAdapter.setDatas(datas);
        flowTagAdapter.notifyDataSetChanged();
    }

    @Override
    public void getSearchHotError() {

    }
}
