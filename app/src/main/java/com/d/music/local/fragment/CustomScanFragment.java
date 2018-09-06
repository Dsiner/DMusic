package com.d.music.local.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.common.component.mvp.base.BaseFragment;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.permissioncompat.Permission;
import com.d.lib.permissioncompat.PermissionCompat;
import com.d.lib.permissioncompat.PermissionSchedulers;
import com.d.lib.permissioncompat.callback.PermissionCallback;
import com.d.lib.xrv.LRecyclerView;
import com.d.music.R;
import com.d.music.local.adapter.DirAdapter;
import com.d.music.local.model.FileModel;
import com.d.music.local.presenter.ScanPresenter;
import com.d.music.local.view.IScanView;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 扫描歌曲-自定义扫描
 * Created by D on 2017/4/29.
 */
public class CustomScanFragment extends BaseFragment<ScanPresenter> implements IScanView, DirAdapter.OnPathListener {
    public final static String ARG_TYPE = "type";

    @BindView(R.id.llyt_dir)
    LinearLayout llytDir;
    @BindView(R.id.llyt_scan_now)
    LinearLayout llytScanNow;
    @BindView(R.id.tv_current_dir)
    TextView tvCurrentDir;
    @BindView(R.id.lrv_list)
    LRecyclerView lrvList;

    private int type;
    private final String rootPath = FileUtil.getRootPath();
    private String curPath;
    private DirAdapter adapter;
    private List<FileModel> models;

    public static CustomScanFragment getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        CustomScanFragment fragment = new CustomScanFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.llyt_dir, R.id.llyt_scan_now})
    public void OnClickLister(final View view) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.llyt_dir:
            case R.id.llyt_scan_now:
                PermissionCompat.with(mActivity).requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                                    sw(view.getId());
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
                break;
        }
    }

    private void sw(int viewId) {
        switch (viewId) {
            case R.id.llyt_dir:
                onBackPressed();
                break;
            case R.id.llyt_scan_now:
                final List<String> paths = new ArrayList<>();
                for (FileModel fileModel : models) {
                    if (fileModel.isChecked) {
                        paths.add(fileModel.absolutePath);
                    }
                }
                if (paths.size() <= 0) {
                    Util.toast(mContext, getResources().getString(R.string.module_common_please_select_scan_path));
                    return;
                }
                showLoading();
                mPresenter.scan(paths, type);
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_fragment_scan_custom;
    }

    @Override
    public ScanPresenter getPresenter() {
        return new ScanPresenter(getActivity().getApplicationContext());
    }

    @Override
    protected IScanView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt(ARG_TYPE);
        }
    }

    @Override
    protected void init() {
        models = new ArrayList<>();
        adapter = new DirAdapter(getActivity(), models, R.layout.module_local_adapter_dir);
        adapter.setOnPathListener(this);
        lrvList.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onPath(rootPath);
    }

    @Override
    public void setDatas(List<FileModel> models) {
        this.models = models;
        adapter.setDatas(this.models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setMusics(List<MusicModel> models) {
        closeLoading();
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    @Override
    public void onPath(String path) {
        curPath = path;
        tvCurrentDir.setText(curPath);
        mPresenter.getFileModels(curPath);
    }

    public boolean onBackPressed() {
        if (!TextUtils.equals(curPath, rootPath)) {
            onPath(FileUtil.getParentPath(curPath));
            return true;
        }
        return false;
    }
}
