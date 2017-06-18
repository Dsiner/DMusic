package com.d.music.mvp.fragment;

import android.content.Context;

import com.d.music.R;
import com.d.music.model.AlbumModel;
import com.d.music.model.FolderModel;
import com.d.music.model.SingerModel;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.mvp.adapter.AlbumAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-专辑
 * Created by D on 2017/4/29.
 */
public class LMAlbumFragment extends AbstractLMFragment {
    private Context context;
    private AlbumAdapter adapter;

    @Override
    protected void init() {
        super.init();
        context = getActivity();
    }

    @Override
    protected void lazyLoad() {
        List<AlbumModel> datas = new ArrayList<>();
        adapter = new AlbumAdapter(context, datas, R.layout.adapter_album);
        xrvList.showAsList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.setAdapter(adapter);
        mPresenter.getAlbum();
    }

    @Override
    public void setSong(List<MusicModel> models) {

    }

    @Override
    public void setSinger(List<SingerModel> models) {

    }

    @Override
    public void setAlbum(List<AlbumModel> models) {
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setFolder(List<FolderModel> models) {

    }

    @Override
    public void setDSState(int state) {
        dslDS.setState(state);
    }

    @Override
    public void notifyDataCountChanged(int count) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != MusicDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        mPresenter.getAlbum();
    }
}