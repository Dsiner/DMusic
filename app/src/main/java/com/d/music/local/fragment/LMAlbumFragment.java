package com.d.music.local.fragment;

import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.local.adapter.AlbumAdapter;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.presenter.LMMusicPresenter;

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
        return new AlbumAdapter(mContext, new ArrayList<AlbumModel>(), R.layout.module_local_adapter_album);
    }

    @Override
    protected void initList() {
        super.initList();
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getAlbum();
    }

    @Override
    public void setAlbum(List<AlbumModel> models) {
        mCommonLoader.loadSuccess(models);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDatabase.LOCAL_ALL_MUSIC || mPresenter == null || !mIsLazyLoaded) {
            return;
        }
        mPresenter.getAlbum();
    }
}