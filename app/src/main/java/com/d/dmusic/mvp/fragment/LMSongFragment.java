package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.model.SingerModel;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.activity.ListHandleActivity;
import com.d.dmusic.mvp.adapter.SongAdapter;
import com.d.dmusic.view.SongHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/29.
 */
public class LMSongFragment extends AbstractLMFragment implements SongHeaderView.OnHeaderListener {
    private Context context;
    private SongHeaderView header;
    private SongAdapter adapter;
    private List<MusicModel> datas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        datas = new ArrayList<>();
        adapter = new SongAdapter(context, datas, R.layout.adapter_song, MusicDB.LOCAL_ALL_MUSIC);
        header = new SongHeaderView(context);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(this);
    }

    @Override
    protected void lazyLoad() {
        xrvList.showAsList();
        xrvList.addHeaderView(header);
        xrvList.setAdapter(adapter);
        mPresenter.getSong(MusicDB.LOCAL_ALL_MUSIC);
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
    public void setSinger(List<SingerModel> models) {

    }

    @Override
    public void setAlbum(List<AlbumModel> models) {

    }

    @Override
    public void setFolder(List<FolderModel> models) {

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
        getActivity().startActivity(new Intent(getActivity(), ListHandleActivity.class));
    }
}