package com.d.music.local.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.d.lib.common.component.loader.v4.BaseLoaderFragment;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.pulllayout.Pullable;
import com.d.lib.pulllayout.rv.PullRecyclerView;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.event.eventbus.SortTypeEvent;
import com.d.music.local.activity.HandleActivity;
import com.d.music.local.activity.ScanActivity;
import com.d.music.local.adapter.SongAdapter;
import com.d.music.local.presenter.SongPresenter;
import com.d.music.local.view.ISongView;
import com.d.music.widget.SongHeaderView;
import com.d.music.widget.TitleLayout;
import com.d.music.widget.dialog.MenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * SongFragment
 * Created by D on 2017/4/30.
 */
public class SongFragment extends BaseLoaderFragment<MusicModel, SongPresenter>
        implements ISongView, View.OnClickListener, SongHeaderView.OnHeaderListener {
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_TAB = "tab";
    public static final String EXTRA_TITLE = "title";

    TitleLayout tl_title;

    private int mType;
    private int mTab; // 本地歌曲tab(0-3)
    private Preferences mPreferences;
    private String mTitle;
    private SongHeaderView mSongHeaderView;
    private int mOrderType;
    private boolean mIsNeedReLoad; // 为了同步收藏状态，需要重新加载数据
    private boolean mIsSubPull; // 为了同步设置，需要重新刷新

    public static SongFragment getInstance(int type, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putInt(EXTRA_TYPE, type);
        SongFragment fragment = new SongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SongFragment getInstance(int type, int tab, String title) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        bundle.putInt(EXTRA_TAB, tab);
        bundle.putString(EXTRA_TITLE, title);
        SongFragment fragment = new SongFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
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
    protected void bindView(View rootView) {
        super.bindView(rootView);
        tl_title = rootView.findViewById(R.id.tl_title);

        ViewHelper.setOnClickListener(rootView, this,
                R.id.iv_title_left);
    }

    @Override
    protected void init() {
        initTitle();
        mPreferences = Preferences.getInstance(getActivity().getApplicationContext());
        mIsSubPull = mPreferences.getIsSubPull();
        mOrderType = DBManager.getInstance(mContext.getApplicationContext()).optCustomList().querySoryType(mType);
        super.init();
    }

    @Override
    protected CommonAdapter<MusicModel> getAdapter() {
        SongAdapter adapter = new SongAdapter(getActivity(), new ArrayList<MusicModel>(),
                R.layout.module_local_adapter_song, mType);
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
    protected void initList() {
        mSongHeaderView = new SongHeaderView(mContext);
        mSongHeaderView.setVisibility(View.GONE);
        if (mType == AppDatabase.LOCAL_ALL_MUSIC) {
            mSongHeaderView.setVisibility(R.id.flyt_header_song_handler, View.GONE);
        }
        mSongHeaderView.setOnHeaderListener(this);
        ((Pullable) mPullList).setCanPullDown(false);
        ((Pullable) mPullList).setCanPullUp(false);
        ((PullRecyclerView) mPullList).addHeaderView(mSongHeaderView);
        super.initList();
    }

    @Override
    protected void onLoad(int page) {
        mPresenter.getSong(mType, mTab, mTitle, mOrderType);
    }

    @Override
    public void loadSuccess(List<MusicModel> datas) {
        super.loadSuccess(datas);
        notifyDataCountChanged(mCommonLoader.getDatas().size());
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

    private void initTitle() {
        Bundle bundle = getArguments();
        mTitle = getResources().getString(R.string.module_common_song);
        if (bundle != null) {
            mType = bundle.getInt(EXTRA_TYPE);
            mTab = bundle.getInt(EXTRA_TAB);
            mTitle = bundle.getString(EXTRA_TITLE);
        }
        tl_title.setText(R.id.tv_title_title, mTitle);
        if (mType == AppDatabase.LOCAL_ALL_MUSIC || mType == AppDatabase.COLLECTION_MUSIC) {
            tl_title.setVisibility(R.id.iv_title_right, View.GONE);
        }
        tl_title.setOnMenuListener(new MenuDialog.OnMenuListener() {
            @Override
            public void onRefresh(View v) {
                View name = v.findViewById(R.id.iv_sort_name_check);
                View time = v.findViewById(R.id.iv_sort_time_check);
                View custom = v.findViewById(R.id.iv_sort_custom_check);
                if (name != null && time != null && custom != null) {
                    name.setVisibility(mOrderType == AppDatabase.ORDER_TYPE_NAME ? View.VISIBLE : View.INVISIBLE);
                    time.setVisibility(mOrderType == AppDatabase.ORDER_TYPE_TIME ? View.VISIBLE : View.INVISIBLE);
                    custom.setVisibility(mOrderType == AppDatabase.ORDER_TYPE_CUSTOM ? View.VISIBLE : View.INVISIBLE);
                }
            }

            @Override
            public void onClick(View v) {
                if (QuickClick.isQuickClick()) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.menu_sort_name:
                        mOrderType = AppDatabase.ORDER_TYPE_NAME;
                        mPresenter.getSong(mType, AppDatabase.ORDER_TYPE_NAME);
                        break;
                    case R.id.menu_sort_time:
                        mOrderType = AppDatabase.ORDER_TYPE_TIME;
                        mPresenter.getSong(mType, AppDatabase.ORDER_TYPE_TIME);
                        break;
                    case R.id.menu_sort_custom:
                        mOrderType = AppDatabase.ORDER_TYPE_CUSTOM;
                        mPresenter.getSong(mType, AppDatabase.ORDER_TYPE_CUSTOM);
                        break;
                    case R.id.menu_scan:
                        ScanActivity.openActivity(getActivity(), mType);
                        break;
                }
            }
        });
    }

    private void notifyDataCountChanged(int count) {
        mSongHeaderView.setSongCount(count);
        mSongHeaderView.setVisibility(count <= 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = mAdapter.getDatas();
        if (datas != null && datas.size() > 0) {
            MediaControl.getInstance(mContext).init(datas, 0, true);
        }
    }

    @Override
    public void onHandle() {
        List<MusicModel> datas = mAdapter.getDatas();
        if (datas == null || datas.size() <= 0) {
            return;
        }
        Constants.Heap.sModels.clear();
        Constants.Heap.sModels.addAll(datas);
        HandleActivity.openActivity(getActivity(), mType, mTitle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != mType || mPresenter == null) {
            return;
        }
        loadSuccess(event.list);
        mPresenter.setSong(event.list, mType);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onEventRefresh(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type == mType || event.event != RefreshEvent.SYNC_COLLECTIONG) {
            return;
        }
        mIsNeedReLoad = true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onEventSortType(SortTypeEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != mType) {
            return;
        }
        mOrderType = event.orderType;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
