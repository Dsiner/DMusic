package com.d.music.local.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseFragment;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.lib.xrv.LRecyclerView;
import com.d.lib.xrv.adapter.MultiItemTypeSupport;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.common.Preferences;
import com.d.music.local.adapter.CustomListAdapter;
import com.d.music.local.presenter.MainPresenter;
import com.d.music.local.view.IMainView;
import com.d.music.module.events.MusicModelEvent;
import com.d.music.module.events.RefreshEvent;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.CustomList;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.module.media.MusicFactory;
import com.d.music.utils.fileutil.FileUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * MainFragment
 * Created by D on 2017/4/29.
 */
public class MainFragment extends BaseFragment<MainPresenter> implements IMainView {
    @BindView(R.id.tv_title_middle_main)
    TextView tvTitle;
    @BindView(R.id.llyt_local)
    LinearLayout llytLocal;//本地音乐
    @BindView(R.id.tv_local_all_count)
    TextView tvLocalAllCount;//本地歌曲数
    @BindView(R.id.pbr_loading)
    ProgressBar pbrLoading;
    @BindView(R.id.llyt_collection)
    LinearLayout llytColletion;//我的收藏
    @BindView(R.id.tv_collection_count)
    TextView tvCollectionCount;//收藏歌曲数
    @BindView(R.id.lv_list)
    LRecyclerView lvList;

    private Preferences p;
    private CustomListAdapter adapter;
    private boolean isNeedReLoad;//为了同步收藏数，需要重新加载数据
    private boolean isShowAdd;//为了同步设置，需要重新刷新

    @OnClick({R.id.llyt_local, R.id.llyt_collection, R.id.llyt_net})
    public void onClickListener(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.llyt_local:
                //本地音乐
                LocalAllFragment lFragment = new LocalAllFragment();
                MainActivity.replace(lFragment);
                break;
            case R.id.llyt_collection:
                //我的收藏
                Bundle bundle = new Bundle();
                bundle.putString("title", "我的收藏");
                bundle.putInt("type", MusicDB.COLLECTION_MUSIC);
                SongFragment sFragment = new SongFragment();
                sFragment.setArguments(bundle);
                MainActivity.replace(sFragment);
                break;
            case R.id.llyt_net:
                //音乐馆

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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        p = Preferences.getInstance(getActivity().getApplicationContext());
        isShowAdd = p.getIsShowAdd();
        adapter = new CustomListAdapter(getActivity(), new ArrayList<CustomList>(), new MultiItemTypeSupport<CustomList>() {
            @Override
            public int getLayoutId(int viewType) {
                switch (viewType) {
                    case -1:
                        return R.layout.adapter_custom_list_add;
                    default:
                        return R.layout.adapter_custom_list;
                }
            }

            @Override
            public int getItemViewType(int position, CustomList customList) {
                return customList.pointer;
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

    private void scanAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(@NonNull Permission permission) throws Exception {
                            if (getActivity() == null || getActivity().isFinishing()) {
                                return;
                            }
                            if (permission.granted) {
                                // `permission.name` is granted !
                                doInBackground(getActivity(), MusicDB.LOCAL_ALL_MUSIC);
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
        } else {
            doInBackground(getActivity(), MusicDB.LOCAL_ALL_MUSIC);
        }
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
                List<MusicModel> list = (List<MusicModel>) MusicFactory.createFactory(context, type).getMusic(paths);
                MusicDBUtil.getInstance(context).deleteAll(type);
                MusicDBUtil.getInstance(context).insertOrReplaceMusicInTx(list, type);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (tvLocalAllCount != null && pbrLoading != null) {
                            tvLocalAllCount.setVisibility(View.VISIBLE);
                            pbrLoading.setVisibility(View.GONE);
                            setLocalAllCount(list.size());
                        }
                        MusicModelEvent event = new MusicModelEvent(type, list);
                        EventBus.getDefault().post(event);
                    }
                });
    }
}
