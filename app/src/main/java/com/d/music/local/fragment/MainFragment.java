package com.d.music.local.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ToastUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.permissioncompat.Permission;
import com.d.lib.permissioncompat.PermissionCompat;
import com.d.lib.permissioncompat.PermissionSchedulers;
import com.d.lib.permissioncompat.callback.PermissionCallback;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.media.MusicFactory;
import com.d.music.data.database.greendao.DBManager;
import com.d.music.data.database.greendao.bean.CustomListModel;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicModelEvent;
import com.d.music.event.eventbus.RefreshEvent;
import com.d.music.local.adapter.CustomListAdapter;
import com.d.music.local.presenter.MainPresenter;
import com.d.music.local.view.IMainView;
import com.d.music.online.activity.OnlineActivity;
import com.d.music.play.activity.SearchActivity;
import com.d.music.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
public class MainFragment extends BaseFragment<MainPresenter>
        implements IMainView, View.OnClickListener {
    TextView tv_title_middle_main;
    TextView tv_local_all_count;
    ProgressBar pbr_loading;
    TextView tv_collection_count;
    RecyclerView lv_list;

    private Preferences mPreferences;
    private CustomListAdapter mCustomListAdapter;

    // 为了同步收藏数，需要重新加载数据
    private boolean mIsNeedReLoad;
    // 为了同步设置，需要重新刷新
    private boolean mIsShowAdd;

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_right:
                startActivity(new Intent(mActivity, SearchActivity.class));
                break;

            case R.id.rlyt_local:
                // Local
                MainActivity.getManger().replace(new LocalAllFragment());
                break;

            case R.id.rlyt_collection:
                // Collection
                MainActivity.getManger().replace(SongFragment.getInstance(AppDatabase.COLLECTION_MUSIC,
                        mContext.getResources().getString(R.string.module_common_my_collection)));
                break;

            case R.id.rlyt_online:
                // Online
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
    protected void bindView(View rootView) {
        tv_title_middle_main = rootView.findViewById(R.id.tv_title_middle_main);
        tv_local_all_count = rootView.findViewById(R.id.tv_local_all_count);
        pbr_loading = rootView.findViewById(R.id.pbr_loading);
        tv_collection_count = rootView.findViewById(R.id.tv_collection_count);
        lv_list = rootView.findViewById(R.id.lv_list);

        ViewHelper.setOnClickListener(rootView, this,
                R.id.iv_title_right,
                R.id.rlyt_local,
                R.id.rlyt_collection,
                R.id.rlyt_online);
    }

    @Override
    protected void init() {
        mPreferences = Preferences.getInstance(getActivity().getApplicationContext());
        mIsShowAdd = mPreferences.getIsShowAdd();
        mCustomListAdapter = new CustomListAdapter(getActivity(), new ArrayList<CustomListModel>(),
                new MultiItemTypeSupport<CustomListModel>() {
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_list.setLayoutManager(layoutManager);
        lv_list.setAdapter(mCustomListAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPreferences.getIsFirst()) {
            mPreferences.putIsFirst(false);
            mPresenter.getCustomList(mIsShowAdd);
            scanAll();
            return;
        }
        mPresenter.getCustomList(mIsShowAdd);
        mPresenter.getLocalAllCount();
        mPresenter.getCollectionCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_title_middle_main.setText(mPreferences.getStroke());
        if (mIsNeedReLoad) {
            mIsNeedReLoad = false;
            mPresenter.getCollectionCount();
        }
        if (mIsShowAdd != mPreferences.getIsShowAdd()) {
            mIsShowAdd = !mIsShowAdd;
            mPresenter.getCustomList(mIsShowAdd);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCustomListAdapter != null) {
            mCustomListAdapter.closeAllF();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(RefreshEvent event) {
        if (event == null || getActivity() == null
                || getActivity().isFinishing() || mPresenter == null) {
            return;
        }
        switch (event.event) {
            case RefreshEvent.SYNC_CUSTOM_LIST:
                mPresenter.getCustomList(mIsShowAdd);
                break;

            case RefreshEvent.SYNC_COLLECTIONG:
                mIsNeedReLoad = true;
                break;
        }
    }

    @Override
    public void setCustomList(List<CustomListModel> models) {
        mCustomListAdapter.setDatas(models);
        mCustomListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLocalAllCount(int count) {
        tv_local_all_count.setText(String.format(mContext.getResources().getString(R.string.module_common_song_unit_format), count));
    }

    @Override
    public void setCollectionCount(int count) {
        tv_collection_count.setText(String.format(mContext.getResources().getString(R.string.module_common_song_unit_format), count));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void scanAll() {
        PermissionCompat.with(getActivity())
                .requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                            doInBackground(getActivity(), AppDatabase.LOCAL_ALL_MUSIC);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            ToastUtils.toast(getActivity().getApplicationContext(), "Denied permission!");
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            ToastUtils.toast(getActivity().getApplicationContext(), "Denied permission with ask never again!");
                        }
                    }
                });
    }

    private void doInBackground(final Context context, final int type) {
        if (tv_local_all_count == null || pbr_loading == null) {
            return;
        }
        tv_local_all_count.setVisibility(View.GONE);
        pbr_loading.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<String> paths = new ArrayList<>();
                paths.add(FileUtils.getRootPath());
                List<MusicModel> list = MusicFactory.createFactory(context).query(paths);
                DBManager.getInstance(context).optMusic().deleteAll(type);
                DBManager.getInstance(context).optMusic().insertOrReplaceInTx(type, list);
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
                        if (tv_local_all_count != null && pbr_loading != null) {
                            tv_local_all_count.setVisibility(View.VISIBLE);
                            pbr_loading.setVisibility(View.GONE);
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
