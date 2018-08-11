package com.d.music.local.fragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.module.mvp.base.BaseFragment;
import com.d.lib.common.module.permissioncompat.Permission;
import com.d.lib.common.module.permissioncompat.PermissionCompat;
import com.d.lib.common.module.permissioncompat.PermissionSchedulers;
import com.d.lib.common.module.permissioncompat.callback.PermissionCallback;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.utils.Util;
import com.d.music.R;
import com.d.music.local.activity.ScanActivity;
import com.d.music.local.model.FileModel;
import com.d.music.local.presenter.ScanPresenter;
import com.d.music.local.view.IScanView;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.utils.fileutil.FileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanFragment extends BaseFragment<ScanPresenter> implements IScanView {
    private int type;
    private CustomScanFragment customScanFragment;

    @OnClick({R.id.btn_full_scan, R.id.btn_custom_scan})
    public void OnClickLister(final View view) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_full_scan:
            case R.id.btn_custom_scan:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    PermissionCompat.with(mActivity).
                            requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                } else {
                    sw(view.getId());
                }
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_scan;
    }

    @Override
    public ScanPresenter getPresenter() {
        return new ScanPresenter(this.getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type");
        }
    }

    @Override
    protected void init() {

    }

    private void sw(int viewId) {
        switch (viewId) {
            case R.id.btn_full_scan:
                List<String> paths = new ArrayList<>();
                paths.add(FileUtil.getRootPath());
                mPresenter.scan(paths, type);
                return;
            case R.id.btn_custom_scan:
                goCustomScan();
                break;
        }
    }

    private void goCustomScan() {
        if (customScanFragment == null) {
            customScanFragment = new CustomScanFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", type);
            customScanFragment.setArguments(bundle);
        }
        ScanActivity activity = (ScanActivity) getActivity();
        activity.replaceFragment(customScanFragment);
    }

    @Override
    public void setDatas(List<FileModel> models) {

    }

    @Override
    public void setMusics(List<MusicModel> models) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }
}
