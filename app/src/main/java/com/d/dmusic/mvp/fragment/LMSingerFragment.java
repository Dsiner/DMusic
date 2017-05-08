package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.d.dmusic.R;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.model.SingerModel;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.mvp.adapter.SingerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/29.
 */
public class LMSingerFragment extends AbstractLMFragment {
    private Context context;
    private SingerAdapter adapter;
    private List<SingerModel> datas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    protected void lazyLoad() {
        datas = new ArrayList<>();
        adapter = new SingerAdapter(context, datas, R.layout.adapter_singer);
        xrvList.showAsList();
        xrvList.setAdapter(adapter);
        mPresenter.getSinger();
    }

    @Override
    public void setSong(List<MusicModel> models) {

    }

    @Override
    public void setSinger(List<SingerModel> models) {
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
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
}