package com.d.music.mvp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.commen.module.mvp.base.BaseFragment;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.commen.module.mvp.MvpView;
import com.d.commen.view.DSLayout;
import com.d.lib.xrv.XRecyclerView;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.commen.Preferences;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.events.SortTypeEvent;
import com.d.music.module.global.MusicCst;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.module.service.MusicControl;
import com.d.music.module.service.MusicService;
import com.d.music.mvp.activity.HandleActivity;
import com.d.music.mvp.activity.ScanActivity;
import com.d.music.mvp.adapter.SongAdapter;
import com.d.music.mvp.presenter.SongPresenter;
import com.d.music.mvp.view.ISongView;
import com.d.music.view.SongHeaderView;
import com.d.music.view.TitleLayout;
import com.d.music.view.dialog.MenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * SongFragment
 * Created by D on 2017/4/30.
 */
public class SongFragment extends BaseFragment<SongPresenter> implements ISongView, SongHeaderView.OnHeaderListener {
    @BindView(R.id.tl_title)
    TitleLayout tlTitle;
    @BindView(R.id.dsl_ds)
    DSLayout dslDS;
    @BindView(R.id.xrv_list)
    XRecyclerView xrvList;

    private Context context;
    private int type;
    private int tab;//本地歌曲tab(0-3)
    private Preferences p;
    private String title;
    private SongHeaderView header;
    private SongAdapter adapter;
    private int orderType;
    private boolean isNeedReLoad;//为了同步收藏状态，需要重新加载数据
    private boolean isSubPull;//为了同步设置，需要重新刷新

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                MainActivity.popBackStack();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_song;
    }

    @Override
    public SongPresenter getPresenter() {
        return new SongPresenter(getActivity().getApplicationContext());
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
    }

    @Override
    protected void init() {
        p = Preferences.getInstance(getActivity().getApplicationContext());
        isSubPull = p.getIsSubPull();
        initTitle();
        adapter = new SongAdapter(getActivity(), new ArrayList<MusicModel>(), R.layout.adapter_song, type, this);
        adapter.setSubPull(isSubPull);
        header = new SongHeaderView(context);
        header.setVisibility(View.GONE);
        if (type == MusicDB.LOCAL_ALL_MUSIC) {
            header.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        }
        header.setOnHeaderListener(this);
        xrvList.showAsList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.addHeaderView(header);
        xrvList.setAdapter(adapter);
        orderType = MusicDBUtil.getInstance(context).queryCusListSoryType(type);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getSong(type, tab, title, orderType);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.getSong(type, tab, title, orderType);
        }
        if (isSubPull != p.getIsSubPull()) {
            isSubPull = !isSubPull;
            adapter.setSubPull(isSubPull);
            if (!isSubPull) {
                mPresenter.subPullUp(adapter.getDatas());
            }
        }
    }

    private void initTitle() {
        Bundle bundle = getArguments();
        title = "Song";
        if (bundle != null) {
            type = bundle.getInt("type");
            tab = bundle.getInt("tab");
            title = bundle.getString("title");
        }
        tlTitle.setText(R.id.tv_title_title, title);
        if (type == MusicDB.LOCAL_ALL_MUSIC || type == MusicDB.COLLECTION_MUSIC) {
            tlTitle.setVisibility(R.id.iv_title_right, View.GONE);
        }
        tlTitle.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {
                View name = v.findViewById(R.id.iv_sort_name_check);
                View time = v.findViewById(R.id.iv_sort_time_check);
                View custom = v.findViewById(R.id.iv_sort_custom_check);
                if (name != null && time != null && custom != null) {
                    name.setVisibility(orderType == MusicDB.ORDER_TYPE_NAME ? View.VISIBLE : View.INVISIBLE);
                    time.setVisibility(orderType == MusicDB.ORDER_TYPE_TIME ? View.VISIBLE : View.INVISIBLE);
                    custom.setVisibility(orderType == MusicDB.ORDER_TYPE_CUSTOM ? View.VISIBLE : View.INVISIBLE);
                }
            }

            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_sort_name:
                        orderType = MusicDB.ORDER_TYPE_NAME;
                        mPresenter.getSong(type, MusicDB.ORDER_TYPE_NAME);
                        break;
                    case R.id.menu_sort_time:
                        orderType = MusicDB.ORDER_TYPE_TIME;
                        mPresenter.getSong(type, MusicDB.ORDER_TYPE_TIME);
                        break;
                    case R.id.menu_sort_custom:
                        orderType = MusicDB.ORDER_TYPE_CUSTOM;
                        mPresenter.getSong(type, MusicDB.ORDER_TYPE_CUSTOM);
                        break;
                    case R.id.menu_scan:
                        Activity activity = getActivity();
                        Intent intent = new Intent(activity, ScanActivity.class);
                        intent.putExtra("type", type);
                        activity.startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public void setSong(List<MusicModel> models) {
        if (models.size() <= 0) {
            setDSState(DSLayout.STATE_EMPTY);
        } else {
            setDSState(View.GONE);
        }
        notifyDataCountChanged(models.size());
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
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
        List<MusicModel> datas = adapter.getDatas();
        if (datas == null || datas.size() <= 0) {
            return;
        }
        if (MusicCst.models == null) {
            MusicCst.models = new ArrayList<MusicModel>();
        }
        MusicCst.models.clear();
        MusicCst.models.addAll(datas);
        Intent intent = new Intent(getActivity(), HandleActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        getActivity().startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != type || mPresenter == null) {
            return;
        }
        setSong(event.list);
        mPresenter.setSong(event.list, type);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRefreshEvent(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type == type || event.event != RefreshEvent.SYNC_COLLECTIONG) {
            return;
        }
        isNeedReLoad = true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSortTypeEvent(SortTypeEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != type) {
            return;
        }
        orderType = event.orderType;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
