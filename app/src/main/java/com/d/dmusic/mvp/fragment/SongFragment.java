package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.commen.base.BaseFragment;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.activity.ListHandleActivity;
import com.d.dmusic.mvp.adapter.SongAdapter;
import com.d.dmusic.mvp.presenter.SongPresenter;
import com.d.dmusic.mvp.view.ISongView;
import com.d.dmusic.view.DSLayout;
import com.d.dmusic.view.SongHeaderView;
import com.d.dmusic.view.TitleLayout;
import com.d.xrv.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by D on 2017/4/30.
 */
public class SongFragment extends BaseFragment<SongPresenter> implements ISongView, SongHeaderView.OnHeaderListener {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.dsl_ds)
    DSLayout dslDS;
    @Bind(R.id.xrv_list)
    XRecyclerView xrvList; // 列表

    private Context context;
    private int type;
    private SongHeaderView header;
    private SongAdapter adapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_song;
    }

    @Override
    public SongPresenter getPresenter() {
        return new SongPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    protected void init() {
        initTitle();
        adapter = new SongAdapter(getActivity(), new ArrayList<MusicModel>(), R.layout.adapter_song, type);
        header = new SongHeaderView(context);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(this);
        xrvList.showAsList();
        xrvList.addHeaderView(header);
        xrvList.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getSong(type);
    }

    private void initTitle() {
        Bundle bundle = getArguments();
        String title = "Song";
        if (bundle != null) {
            type = bundle.getInt("type");
            title = bundle.getString("title");
        }
        tlTitle.setType(type);
        tlTitle.setText(R.id.tv_title_title, title);
        tlTitle.setVisibility(R.id.iv_title_more, View.VISIBLE);
    }

    @Override
    public void setSong(List<MusicModel> models) {
        if (models.size() <= 0) {
            header.setVisibility(View.GONE);
        } else {
            header.setSongCount(models.size());
            header.setVisibility(View.VISIBLE);
        }
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setDSState(int state) {
        dslDS.setState(state);
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = adapter.getDatas();
        if (datas != null && datas.size() > 0) {
            MusicControl control = MusicService.getControl();
            control.init((List<MusicModel>) MusicModel.clone(datas, MusicDB.MUSIC), 0);
        }
    }

    @Override
    public void onHandle() {
        MusciCst.models.clear();
        List<MusicModel> datas = adapter.getDatas();
        if (datas != null && datas.size() > 0) {
            int size = datas.size();
            for (int i = 0; i < size; i++) {
                MusicModel model = datas.get(i);
                MusciCst.models.add(model.clone(new MusicModel()));
            }
        }

        Intent intent = new Intent(getActivity(), ListHandleActivity.class);
        intent.putExtra("type", type);
        getActivity().startActivity(intent);
    }
}
