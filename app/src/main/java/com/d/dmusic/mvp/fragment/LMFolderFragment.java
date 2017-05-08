package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.d.dmusic.R;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.model.SingerModel;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.mvp.adapter.FolderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/29.
 */
public class LMFolderFragment extends AbstractLMFragment {
    private Context context;
    private FolderAdapter adapter;
    private List<FolderModel> datas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    protected void lazyLoad() {
        datas = new ArrayList<>();
        adapter = new FolderAdapter(context, datas, R.layout.adapter_folder);
        xrvList.showAsList();
        xrvList.setAdapter(adapter);
        mPresenter.getFolder();
    }

    @Override
    public void setSong(List<MusicModel> models) {

    }

    @Override
    public void setSinger(List<SingerModel> models) {

    }

    @Override
    public void setAlbum(List<AlbumModel> models) {

    }

    @Override
    public void setFolder(List<FolderModel> models) {
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setDSState(int state) {
        dslDS.setState(state);
    }
}