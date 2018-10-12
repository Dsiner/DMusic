package com.d.music.local.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.eventbus.MusicModelEvent;
import com.d.music.data.eventbus.RefreshEvent;
import com.d.music.data.preferences.Preferences;
import com.d.music.local.adapter.SongAdapter;
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
public class LMSongFragment extends AbstractLMFragment<MusicModel> implements SongHeaderView.OnHeaderListener, SideBar.OnLetterChangedListener {
    private Preferences p;
    private SongHeaderView header;
    private SortUtil sortUtil;
    private boolean isNeedReLoad; // 为了同步收藏状态，需要重新加载数据
    private boolean isSubPull; // 为了同步设置，需要重新刷新

    @Override
    protected void init() {
        p = Preferences.getIns(getActivity().getApplicationContext());
        isSubPull = p.getIsSubPull();
        sortUtil = new SortUtil();
        sbSideBar.setOnLetterChangedListener(this);
        super.init();
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        SongAdapter adapter = new SongAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_local_adapter_song, AppDB.LOCAL_ALL_MUSIC);
        adapter.setSubPull(isSubPull);
        adapter.setOnDataChangedListener(new SongAdapter.OnDataChangedListener() {
            @Override
            public void onChange(int count) {
                notifyDataCountChanged(count);
            }
        });
        return adapter;
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSong(AppDB.LOCAL_ALL_MUSIC, sortUtil);
    }

    @Override
    protected void initList() {
        header = new SongHeaderView(mContext);
        header.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lib_pub_color_bg_sub));
        header.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(this);
        xrvList.addHeaderView(header);
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        super.initList();
    }

    @Override
    protected void onVisible() {
        MainActivity.getManger().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        super.onVisible();
    }

    @Override
    protected void onInvisible() {
        MainActivity.getManger().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onInvisible();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            getData();
        }
        if (isSubPull != p.getIsSubPull()) {
            isSubPull = !isSubPull;
            ((SongAdapter) adapter).setSubPull(isSubPull);
            if (!isSubPull) {
                mPresenter.subPullUp(adapter.getDatas());
            }
        }
    }

    @Override
    public void setSong(List<MusicModel> models) {
        commonLoader.setData(models);
        notifyDataCountChanged(commonLoader.getDatas().size());
    }

    private void notifyDataCountChanged(int count) {
        header.setSongCount(count);
        header.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
        sbSideBar.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = commonLoader.getDatas();
        if (datas != null && datas.size() > 0) {
            MediaControler.getIns(mContext).init(datas, 0, true);
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
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        setSong(event.list);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onEventRefresh(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        isNeedReLoad = true;
    }

    @Override
    public void onDestroy() {
        MainActivity.getManger().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onDestroy();
    }
}