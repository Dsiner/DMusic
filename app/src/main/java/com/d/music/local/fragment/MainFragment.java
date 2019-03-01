package com.d.music.local.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.permissioncompat.Permission;
import com.d.lib.permissioncompat.PermissionCompat;
import com.d.lib.permissioncompat.PermissionSchedulers;
import com.d.lib.permissioncompat.callback.PermissionCallback;
import com.d.lib.xrv.LRecyclerView;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.media.MusicFactory;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.local.adapter.CustomListAdapter;
import com.d.music.local.presenter.MainPresenter;
import com.d.music.local.view.IMainView;
import com.d.music.online.activity.OnlineActivity;
import com.d.music.play.activity.SearchActivity;
import com.d.music.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * MainFragment
 * Created by D on 2017/4/29.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements IMainView {
    @BindView(R.id.tv_title_middle_main)
    TextView tvTitle;
    @BindView(R.id.tv_local_all_count)
    TextView tvLocalAllCount;
    @BindView(R.id.pbr_loading)
    ProgressBar pbrLoading;
    @BindView(R.id.tv_collection_count)
    TextView tvCollectionCount;
    @BindView(R.id.lv_list)
    LRecyclerView lvList;

    private Preferences p;
    private CustomListAdapter adapter;

    // 为了同步收藏数，需要重新加载数据
    private boolean isNeedReLoad;
    // 为了同步设置，需要重新刷新
    private boolean isShowAdd;

    @OnClick({R.id.iv_title_right, R.id.rlyt_local, R.id.rlyt_collection, R.id.rlyt_online})
    public void onClickListener(View v) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_right:
                startActivity(new Intent(mActivity, SearchActivity.class));
                break;
            case R.id.rlyt_local:
                // 本地音乐
                MainActivity.getManger().replace(new LocalAllFragment());
                break;
            case R.id.rlyt_collection:
                // 我的收藏
                MainActivity.getManger().replace(SongFragment.getInstance(AppDB.COLLECTION_MUSIC, "我的收藏"));
                break;
            case R.id.rlyt_online:
                // 音乐馆
                getActivity().startActivity(new Intent(mActivity, OnlineActivity.class));
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_common_fragment_main;
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        p = Preferences.getIns(getActivity().getApplicationContext());
        isShowAdd = p.getIsShowAdd();
        adapter = new CustomListAdapter(getActivity(), new ArrayList<CustomListModel>(), new MultiItemTypeSupport<CustomListModel>() {
            @Override
            public int getLayoutId(int viewType) {
                switch (viewType) {
                    case -1:
                        return R.layout.module_local_adapter_custom_list_add;
                    default:
                        return R.layout.module_local_adapter_custom_list;
                }
            }

            @Override
            public int getItemViewType(int position, CustomListModel customListModel) {
                return customListModel.pointer;
            }
        });
        lvList.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (p.getIsFirst()) {
            p.putIsFirst(false);
            mPresenter.getCustomList(isShowAdd);
            scanAll();
            return;
        }
        mPresenter.getCustomList(isShowAdd);
        mPresenter.getLocalAllCount();
        mPresenter.getCollectionCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTitle.setText(p.getStroke());
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.getCollectionCount();
        }
        if (isShowAdd != p.getIsShowAdd()) {
            isShowAdd = !isShowAdd;
            mPresenter.getCustomList(isShowAdd);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.closeAllF();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing() || mPresenter == null) {
            return;
        }
        switch (event.event) {
            case RefreshEvent.SYNC_CUSTOM_LIST:
                mPresenter.getCustomList(isShowAdd);
                break;
            case RefreshEvent.SYNC_COLLECTIONG:
                isNeedReLoad = true;
                break;
        }
    }

    @Override
    public void setCustomList(List<CustomListModel> models) {
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setLocalAllCount(int count) {
        tvLocalAllCount.setText(String.format(mContext.getResources().getString(R.string.module_common_song_unit_format), count));
    }

    @Override
    public void setCollectionCount(int count) {
        tvCollectionCount.setText(String.format(mContext.getResources().getString(R.string.module_common_song_unit_format), count));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void scanAll() {
        PermissionCompat.with(getActivity()).
                requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET)
                .subscribeOn(PermissionSchedulers.io())
                .observeOn(PermissionSchedulers.mainThread())
                .requestPermissions(new PermissionCallback<Permission>() {
                    @Override
                    public void onNext(Permission permission) {
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        if (permission.granted) {
                            // `permission.name` is granted !
                            doInBackground(getActivity(), AppDB.LOCAL_ALL_MUSIC);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            Util.toast(getActivity().getApplicationContext(), "Denied permission!");
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            Util.toast(getActivity().getApplicationContext(), "Denied permission with ask never again!");
                        }
                    }
                });
    }

    private void doInBackground(final Context context, final int type) {
        if (tvLocalAllCount == null || pbrLoading == null) {
            return;
        }
        tvLocalAllCount.setVisibility(View.GONE);
        pbrLoading.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<String> paths = new ArrayList<>();
                paths.add(FileUtil.getRootPath());
                List<MusicModel> list = MusicFactory.createFactory(context).query(paths);
                AppDBUtil.getIns(context).optMusic().deleteAll(type);
                AppDBUtil.getIns(context).optMusic().insertOrReplaceInTx(type, list);
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MusicModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MusicModel> list) {
                        if (tvLocalAllCount != null && pbrLoading != null) {
                            tvLocalAllCount.setVisibility(View.VISIBLE);
                            pbrLoading.setVisibility(View.GONE);
                            setLocalAllCount(list.size());
                        }
                        EventBus.getDefault().post(new MusicModelEvent(type, list));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
