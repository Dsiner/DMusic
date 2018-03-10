package com.d.music.local.fragment;

import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.local.adapter.AlbumAdapter;
import com.d.music.local.presenter.LMMusicPresenter;
import com.d.music.model.AlbumModel;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.greendao.db.MusicDB;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-专辑
 * Created by D on 2017/4/29.
 */
public class LMAlbumFragment extends AbstractLMFragment<AlbumModel> {

    @Override
    public LMMusicPresenter getPresenter() {
        return new LMMusicPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected CommonAdapter<AlbumModel> getAdapter() {
        return new AlbumAdapter(mContext, new ArrayList<AlbumModel>(), R.layout.adapter_album);
    }

    @Override
    protected void initList() {
        super.initList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getAlbum();
    }

    @Override
    public void setAlbum(List<AlbumModel> models) {
        commonLoader.setData(models);
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