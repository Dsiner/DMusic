package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.base.BaseFragment;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.CustomList;
import com.d.dmusic.mvp.adapter.CustomListAdapter;
import com.d.dmusic.mvp.presenter.MainPresenter;
import com.d.dmusic.mvp.view.IMainView;
import com.d.dmusic.view.TitleLayout;
import com.d.xrv.LRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by D on 2017/4/29.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements IMainView {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;
    @Bind(R.id.llyt_local)
    LinearLayout llytLocal;// 本地音乐
    @Bind(R.id.tv_local_all_count)
    TextView tvLocalAllCount;// 本地歌曲数
    @Bind(R.id.llyt_collection)
    LinearLayout llytColletion;// 我的收藏
    @Bind(R.id.tv_collection_count)
    TextView tvCollectionCount;// 收藏歌曲数
    @Bind(R.id.lv_list)
    LRecyclerView lvList;

    private Context context;
    private CustomListAdapter adapter;

    @OnClick({R.id.llyt_local, R.id.llyt_collection})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.llyt_local:
                //本地音乐
                LocalAllMusicFragment lFragment = new LocalAllMusicFragment();
                MainActivity.fManger.beginTransaction().replace(R.id.framement, lFragment)
                        .addToBackStack(null).commitAllowingStateLoss();
                break;
            case R.id.llyt_collection:
                //我的收藏
                Bundle cb = new Bundle();
                cb.putString("title", "我的收藏");
                cb.putInt("type", MusicDB.COLLECTION_MUSIC);
                CollectionMusicFragment cFragment = new CollectionMusicFragment();
                cFragment.setArguments(cb);

                MainActivity.fManger.beginTransaction().replace(R.id.framement, cFragment)
                        .addToBackStack(null).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_main;
    }

    @Override
    public MainPresenter getPresenter() {
        return new MainPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        EventBus.getDefault().register(this);
        adapter = new CustomListAdapter(context, new ArrayList<CustomList>());
    }

    @Override
    protected void init() {
        initTitle();
        lvList.setAdapter(adapter);
    }

    private void initTitle() {
        tlTitle.setText(R.id.tv_title_title, "   首页");
        tlTitle.setVisibility(R.id.iv_title_back, View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getCustomList();
        mPresenter.getLocalAllCount();
        mPresenter.getCollectionCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshEvent event) {
        if (event == null || event.event != RefreshEvent.SYNC_CUSTOM_LIST) {
            return;
        }
        mPresenter.getCustomList();
    }

    @Override
    public void setCustomList(List<CustomList> models) {
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setLocalAllCount(int count) {
        tvLocalAllCount.setText(count + "首");
    }

    @Override
    public void setCollectionCount(int count) {
        tvCollectionCount.setText(count + "首");
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}