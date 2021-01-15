package com.d.music.play.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.loader.v4.BaseLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.util.GsonUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.util.keyboard.KeyboardHelper;
import com.d.lib.common.widget.ClearEditText;
import com.d.lib.common.widget.DSLayout;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.Refreshable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.util.RefreshableCompat;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.preferences.Preferences;
import com.d.music.online.model.SearchHotRespModel;
import com.d.music.play.adapter.SearchAdapter;
import com.d.music.play.adapter.SearchHistoryAdapter;
import com.d.music.play.presenter.SearchPresenter;
import com.d.music.play.view.ISearchView;
import com.d.music.widget.SearchHeaderView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchFragment
 * Created by D on 2018/8/13.
 **/
public class SearchFragment extends BaseLoaderFragment<MusicModel, SearchPresenter>
        implements ISearchView {

    ClearEditText cet_edit;
    TextView tv_search;
    View llyt_float_search_history;
    View flyt_float_search;
    RecyclerView list_history;

    private SearchHeaderView mSearchHeaderView;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private HistoryQueue mHistoryQueue;
    private String mSearchTag;

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.tv_search) {
            if (getResources().getString(R.string.module_common_search)
                    .equals(tv_search.getText().toString())) {
                search(cet_edit.getText().toString());
            } else if (getResources().getString(R.string.lib_pub_cancel)
                    .equals(tv_search.getText().toString())) {
                if (isSearching()) {
                    switchMode(false);
                } else {
                    getActivity().finish();
                }
            }
        }
    }

    private void search(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            cet_edit.setText(tag);
            cet_edit.setSelection(cet_edit.getText().toString().length());
            tv_search.setText(getResources().getString(R.string.lib_pub_cancel));
        }

        switchMode(true);
        loadSuccess(new ArrayList<MusicModel>());
        getData();
        KeyboardHelper.hideKeyboard(cet_edit);
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
    protected CommonAdapter<MusicModel> getAdapter() {
        return new SearchAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_play_adapter_search);
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        cet_edit = rootView.findViewById(R.id.cet_edit);
        tv_search = rootView.findViewById(R.id.tv_search);
        llyt_float_search_history = rootView.findViewById(R.id.llyt_float_search_history);
        flyt_float_search = rootView.findViewById(R.id.flyt_float_search);
        list_history = rootView.findViewById(R.id.pull_list_history);

        ViewHelper.setOnClickListener(rootView, this, R.id.tv_search);
    }

    @Override
    protected void initList() {
        ((Pullable) mPullList).setCanPullDown(false);
        super.initList();
    }

    @Override
    protected void init() {
        super.init();
        mHistoryQueue = new HistoryQueue(mContext);
        cet_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_search.setText(s.toString().length() > 0
                        ? getResources().getString(R.string.module_common_search)
                        : getResources().getString(R.string.lib_pub_cancel));
            }
        });

        initHistory();
    }

    private void initHistory() {
        ((Pullable) list_history).setCanPullDown(false);
        ((Pullable) list_history).setCanPullUp(false);
        mSearchHeaderView = new SearchHeaderView(mContext);
        mSearchHistoryAdapter = new SearchHistoryAdapter(mContext, new ArrayList<String>(),
                R.layout.module_play_adapter_search_history);
        RefreshableCompat.addHeaderView((Refreshable) list_history, mSearchHeaderView);
        list_history.setAdapter(mSearchHistoryAdapter);
        mSearchHeaderView.setOnHeaderListener(new SearchHeaderView.OnHeaderListener() {
            @Override
            public void onClick(View v, String tag) {
                search(tag);
            }

            @Override
            public void onSweepHistory() {
                mHistoryQueue.clear();
                mSearchHistoryAdapter.setDatas(mHistoryQueue.list());
                mSearchHistoryAdapter.notifyDataSetChanged();
            }
        });

        String json = Preferences.getInstance(mContext).getSearchHot();
        if (!TextUtils.isEmpty(json)) {
            List<SearchHotRespModel.HotsBean> datas = GsonUtils.getInstance().fromJson(json,
                    new TypeToken<List<SearchHotRespModel.HotsBean>>() {
                    }.getType());
            if (datas != null && datas.size() > 0) {
                mSearchHeaderView.setDatas(datas);
            }
        }

        mSearchHistoryAdapter.setOnClickListener(new SearchHistoryAdapter.OnClickListener() {
            @Override
            public void onClick(int position, String item) {
                mHistoryQueue.put(item);
                search(item);
            }

            @Override
            public void onDelete(int position, String item) {
                mHistoryQueue.delete(item);
                mSearchHistoryAdapter.setDatas(mHistoryQueue.list());
                mSearchHistoryAdapter.notifyDataSetChanged();
            }
        });
        mSearchHistoryAdapter.setDatas(mHistoryQueue.list());
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getSearchHot();
    }

    private boolean isSearching() {
        return flyt_float_search.getVisibility() == View.VISIBLE;
    }

    private void switchMode(boolean searching) {
        if (!searching) {
            cet_edit.setText("");
            mSearchTag = "";
        } else {
            mSearchTag = cet_edit.getText().toString();
            mHistoryQueue.put(mSearchTag);
            mSearchHistoryAdapter.setDatas(mHistoryQueue.list());
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
        llyt_float_search_history.setVisibility(searching ? View.GONE : View.VISIBLE);
        flyt_float_search.setVisibility(searching ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onLoad(int page) {
        if (TextUtils.isEmpty(mSearchTag)) {
            setState(DSLayout.STATE_EMPTY);
            return;
        }
        int start = page - 1;
        int count = 15;
        mPresenter.search(mSearchTag, start, count);
    }

    @Override
    public void getSearchHotSuccess(List<SearchHotRespModel.HotsBean> datas) {
        if (datas.size() > 0) {
            String json = GsonUtils.getInstance().toJson(datas);
            Preferences.getInstance(mContext).putSearchHot(json);
        }
        mSearchHeaderView.setDatas(datas);
    }

    @Override
    public void getSearchHotError() {

    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        mHistoryQueue.save();
        super.onDestroyView();
    }

    static class HistoryQueue {
        private Context context;
        private List<String> history = new ArrayList<>();

        HistoryQueue(Context context) {
            this.context = context;
            init();
        }

        private void init() {
            history = new ArrayList<>();
            String json = Preferences.getInstance(context).getSearchHistory();
            if (!TextUtils.isEmpty(json)) {
                List<String> datas = GsonUtils.getInstance().fromJson(json,
                        new TypeToken<List<String>>() {
                        }.getType());
                if (datas != null && datas.size() > 0) {
                    history.addAll(datas);
                }
            }
        }

        void delete(String item) {
            history.remove(item);
        }

        void put(String item) {
            if (history.size() > 50) {
                history.remove(history.size() - 1);
            }
            history.remove(item);
            history.add(0, item);
        }

        @NonNull
        List<String> list() {
            return history;
        }

        void clear() {
            history.clear();
        }

        void save() {
            String json = GsonUtils.getInstance().toJson(history);
            Preferences.getInstance(context).putSearchHistory(json);
        }
    }
}
