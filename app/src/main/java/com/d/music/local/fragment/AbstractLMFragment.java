package com.d.music.local.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.module.loader.AbsLazyFragment;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.model.BaseModel;
import com.d.music.R;
import com.d.music.local.model.AlbumModel;
import com.d.music.local.model.FolderModel;
import com.d.music.local.model.SingerModel;
import com.d.music.local.presenter.LMMusicPresenter;
import com.d.music.local.view.ILMMusicView;
import com.d.music.module.greendao.bean.MusicModel;
import com.d.music.view.sort.SideBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * LazyLoad Fragment
 * Created by D on 2017/4/30.
 */
public abstract class AbstractLMFragment<M extends BaseModel> extends AbsLazyFragment<M, LMMusicPresenter> implements ILMMusicView {
    public final static int TYPE_SONG = 0;
    public final static int TYPE_SINGER = 1;
    public final static int TYPE_ALBUM = 2;
    public final static int TYPE_FOLDER = 3;

    @BindView(R.id.sb_sidebar)
    SideBar sbSideBar;

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_fragment_local_sort;
    }

    @Override
    protected int getDSLayoutRes() {
        return R.id.dsl_ds;
    }

    @Override
    public LMMusicPresenter getPresenter() {
        return new LMMusicPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onVisible();
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

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
