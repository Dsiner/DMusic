package com.d.music.local.fragment;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.util.RefreshableCompat;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.local.adapter.SongAdapter;
import com.d.music.widget.SongHeaderView;
import com.d.music.widget.sort.SideBar;
import com.d.music.widget.sort.SortUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-歌曲
 * Created by D on 2017/4/29.
 */
public class LMSongFragment extends AbstractLMFragment<MusicModel> implements SongHeaderView.OnHeaderListener, SideBar.OnLetterChangedListener {
    private Preferences mPreferences;
    private SongHeaderView songHeaderView;
    private SortUtils mSortUtils;
    private boolean mIsNeedReLoad; // 为了同步收藏状态，需要重新加载数据
    private boolean mIsSubPull; // 为了同步设置，需要重新刷新

    @Override
    protected void init() {
        mPreferences = Preferences.getInstance(getActivity().getApplicationContext());
        mIsSubPull = mPreferences.getIsSubPull();
        mSortUtils = new SortUtils();
        sb_sidebar.setOnLetterChangedListener(this);
        super.init();
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        SongAdapter adapter = new SongAdapter(mContext, new ArrayList<MusicModel>(),
                R.layout.module_local_adapter_song, AppDatabase.LOCAL_ALL_MUSIC);
        adapter.setSubPull(mIsSubPull);
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
        mPresenter.getSong(AppDatabase.LOCAL_ALL_MUSIC, mSortUtils);
    }

    @Override
    protected void initList() {
        songHeaderView = new SongHeaderView(mContext);
        songHeaderView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lib_pub_color_bg_sub));
        songHeaderView.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        songHeaderView.setVisibility(View.GONE);
        songHeaderView.setOnHeaderListener(this);
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        RefreshableCompat.addHeaderView(mPullList, songHeaderView);
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
        if (mIsNeedReLoad) {
            mIsNeedReLoad = false;
            getData();
        }
        if (mIsSubPull != mPreferences.getIsSubPull()) {
            mIsSubPull = !mIsSubPull;
            ((SongAdapter) mAdapter).setSubPull(mIsSubPull);
            if (!mIsSubPull) {
                mPresenter.subPullUp(mAdapter.getDatas());
            }
        }
    }

    @Override
    public void setSong(List<MusicModel> models) {
        mCommonLoader.loadSuccess(models);
        notifyDataCountChanged(mCommonLoader.getDatas().size());
    }

    private void notifyDataCountChanged(int count) {
        songHeaderView.setSongCount(count);
        songHeaderView.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
        sb_sidebar.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = mCommonLoader.getDatas();
        if (datas != null && datas.size() > 0) {
            MediaControl.getInstance(mContext).init(datas, 0, true);
        }
    }

    @Override
    public void onHandle() {

    }

    @Override
    public void onChange(int index, String c) {
        mSortUtils.onChange(index, c, (RecyclerView) mPullList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != AppDatabase.LOCAL_ALL_MUSIC || mPresenter == null || !mIsLazyLoaded) {
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
        mIsNeedReLoad = true;
    }

    @Override
    public void onDestroy() {
        MainActivity.getManger().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onDestroy();
    }
}