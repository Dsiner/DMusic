package com.d.music.mvp.fragment;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.commen.Preferences;
import com.d.music.model.AlbumModel;
import com.d.music.model.FolderModel;
import com.d.music.model.SingerModel;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.service.MusicControl;
import com.d.music.module.service.MusicService;
import com.d.music.mvp.adapter.SongAdapter;
import com.d.commen.view.DSLayout;
import com.d.music.view.SongHeaderView;
import com.d.music.view.sort.SideBar;
import com.d.music.view.sort.SortUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-歌曲
 * Created by D on 2017/4/29.
 */
public class LMSongFragment extends AbstractLMFragment implements SongHeaderView.OnHeaderListener, SideBar.OnLetterChangedListener {
    private Context context;
    private Preferences p;
    private SongHeaderView header;
    private SongAdapter adapter;
    private SortUtil sortUtil;
    private boolean isNeedReLoad;//为了同步收藏状态，需要重新加载数据
    private boolean isSubPull;//为了同步设置，需要重新刷新

    @Override
    protected void init() {
        super.init();
        context = getActivity();
        p = Preferences.getInstance(getActivity().getApplicationContext());
        isSubPull = p.getIsSubPull();
        sortUtil = new SortUtil();
        adapter = new SongAdapter(context, new ArrayList<MusicModel>(), R.layout.adapter_song, MusicDB.LOCAL_ALL_MUSIC, this);
        adapter.setSubPull(isSubPull);
        header = new SongHeaderView(context);
        header.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(this);
    }

    @Override
    protected void onVisible() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        super.onVisible();
    }

    @Override
    protected void onInvisible() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onInvisible();
    }

    @Override
    protected void lazyLoad() {
        xrvList.showAsList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.addHeaderView(header);
        xrvList.setAdapter(adapter);
        sbSideBar.setOnLetterChangedListener(this);
        mPresenter.getSong(MusicDB.LOCAL_ALL_MUSIC, sortUtil);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.getSong(MusicDB.LOCAL_ALL_MUSIC, sortUtil);
        }
        if (isSubPull != p.getIsSubPull()) {
            isSubPull = !isSubPull;
            adapter.setSubPull(isSubPull);
            if (!isSubPull) {
                mPresenter.subPullUp(adapter.getDatas());
            }
        }
    }

    @Override
    public void setSong(List<MusicModel> models) {
        if (models.size() <= 0) {
            setDSState(DSLayout.STATE_EMPTY);
            sbSideBar.setVisibility(View.GONE);
        } else {
            setDSState(View.GONE);
            sbSideBar.setVisibility(View.VISIBLE);
        }
        notifyDataCountChanged(models.size());
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
    public void notifyDataCountChanged(int count) {
        if (count <= 0) {
            header.setVisibility(View.GONE);
        } else {
            header.setSongCount(count);
            header.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = adapter.getDatas();
        if (datas != null && datas.size() > 0) {
            MusicControl control = MusicService.getControl(getActivity().getApplicationContext());
            control.init(context, datas, 0, true);
        }
    }

    @Override
    public void onHandle() {

    }

    @Override
    public void onChange(int index, String c) {
        sortUtil.onChange(index, c, xrvList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != MusicDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        setSong(event.list);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRefreshEvent(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        isNeedReLoad = true;
    }

    @Override
    public void onDestroy() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onDestroy();
    }
}