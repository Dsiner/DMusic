package com.d.music.local.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.component.loader.AbsFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.common.Constants;
import com.d.music.common.preferences.Preferences;
import com.d.music.component.events.MusicModelEvent;
import com.d.music.component.events.RefreshEvent;
import com.d.music.component.events.SortTypeEvent;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.component.greendao.db.AppDB;
import com.d.music.component.greendao.util.AppDBUtil;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.local.activity.HandleActivity;
import com.d.music.local.activity.ScanActivity;
import com.d.music.local.adapter.SongAdapter;
import com.d.music.local.presenter.SongPresenter;
import com.d.music.local.view.ISongView;
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
public class SongFragment extends AbsFragment<MusicModel, SongPresenter>
        implements ISongView, SongHeaderView.OnHeaderListener {
    public final static String ARG_TYPE = "type";
    public final static String ARG_TAB = "tab";
    public final static String ARG_TITLE = "title";

    @BindView(R.id.tl_title)
    TitleLayout tlTitle;

    private int type;
    private int tab; // 本地歌曲tab(0-3)
    private Preferences p;
    private String title;
    private SongHeaderView header;
    private int orderType;
    private boolean isNeedReLoad; // 为了同步收藏状态，需要重新加载数据
    private boolean isSubPull; // 为了同步设置，需要重新刷新

    public static SongFragment getInstance(int type, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putInt(ARG_TYPE, type);
        SongFragment fragment = new SongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SongFragment getInstance(int type, int tab, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        bundle.putInt(ARG_TAB, tab);
        bundle.putString(ARG_TITLE, title);
        SongFragment fragment = new SongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                MainActivity.getManger().popBackStack();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_fragment_song;
    }

    @Override
    protected int getDSLayoutRes() {
        return R.id.dsl_ds;
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        initTitle();
        p = Preferences.getIns(getActivity().getApplicationContext());
        isSubPull = p.getIsSubPull();
        orderType = AppDBUtil.getIns(mContext.getApplicationContext()).optCustomList().querySoryType(type);
        super.init();
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        SongAdapter adapter = new SongAdapter(getActivity(), new ArrayList<MusicModel>(),
                R.layout.module_local_adapter_song, type);
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
    protected void initList() {
        header = new SongHeaderView(mContext);
        header.setVisibility(View.GONE);
        if (type == AppDB.LOCAL_ALL_MUSIC) {
            header.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        }
        header.setOnHeaderListener(this);
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.addHeaderView(header);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSong(type, tab, title, orderType);
    }

    @Override
    public void setData(List<MusicModel> datas) {
        super.setData(datas);
        notifyDataCountChanged(commonLoader.getDatas().size());
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

    private void initTitle() {
        Bundle bundle = getArguments();
        title = getResources().getString(R.string.module_common_song);
        if (bundle != null) {
            type = bundle.getInt(ARG_TYPE);
            tab = bundle.getInt(ARG_TAB);
            title = bundle.getString(ARG_TITLE);
        }
        tlTitle.setText(R.id.tv_title_title, title);
        if (type == AppDB.LOCAL_ALL_MUSIC || type == AppDB.COLLECTION_MUSIC) {
            tlTitle.setVisibility(R.id.iv_title_right, View.GONE);
        }
        tlTitle.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {
                View name = v.findViewById(R.id.iv_sort_name_check);
                View time = v.findViewById(R.id.iv_sort_time_check);
                View custom = v.findViewById(R.id.iv_sort_custom_check);
                if (name != null && time != null && custom != null) {
                    name.setVisibility(orderType == AppDB.ORDER_TYPE_NAME ? View.VISIBLE : View.INVISIBLE);
                    time.setVisibility(orderType == AppDB.ORDER_TYPE_TIME ? View.VISIBLE : View.INVISIBLE);
                    custom.setVisibility(orderType == AppDB.ORDER_TYPE_CUSTOM ? View.VISIBLE : View.INVISIBLE);
                }
            }

            @Override
            public void onClick(View v) {
                if (ClickFast.isFastDoubleClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_sort_name:
                        orderType = AppDB.ORDER_TYPE_NAME;
                        mPresenter.getSong(type, AppDB.ORDER_TYPE_NAME);
                        break;
                    case R.id.menu_sort_time:
                        orderType = AppDB.ORDER_TYPE_TIME;
                        mPresenter.getSong(type, AppDB.ORDER_TYPE_TIME);
                        break;
                    case R.id.menu_sort_custom:
                        orderType = AppDB.ORDER_TYPE_CUSTOM;
                        mPresenter.getSong(type, AppDB.ORDER_TYPE_CUSTOM);
                        break;
                    case R.id.menu_scan:
                        ScanActivity.startActivity(getActivity(), type);
                        break;
                }
            }
        });
    }

    private void notifyDataCountChanged(int count) {
        header.setSongCount(count);
        header.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = adapter.getDatas();
        if (datas != null && datas.size() > 0) {
            MediaControler.getIns(mContext).init(datas, 0, true);
        }
    }

    @Override
    public void onHandle() {
        List<MusicModel> datas = adapter.getDatas();
        if (datas == null || datas.size() <= 0) {
            return;
        }
        if (Constants.Heap.models == null) {
            Constants.Heap.models = new ArrayList<>();
        }
        Constants.Heap.models.clear();
        Constants.Heap.models.addAll(datas);
        HandleActivity.startActivity(getActivity(), type, title);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != type || mPresenter == null) {
            return;
        }
        setData(event.list);
        mPresenter.setSong(event.list, type);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onEventRefresh(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type == type || event.event != RefreshEvent.SYNC_COLLECTIONG) {
            return;
        }
        isNeedReLoad = true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onEventSortType(SortTypeEvent event) {
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
