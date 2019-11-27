package com.d.music.local.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.Util;
import com.d.lib.permissioncompat.Permission;
import com.d.lib.permissioncompat.PermissionCompat;
import com.d.lib.permissioncompat.PermissionSchedulers;
import com.d.lib.permissioncompat.callback.PermissionCallback;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.local.activity.ScanActivity;
import com.d.music.local.model.FileModel;
import com.d.music.local.presenter.ScanPresenter;
import com.d.music.local.view.IScanView;
import com.d.music.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanFragment extends BaseFragment<ScanPresenter> implements IScanView {
    public final static String ARG_TYPE = "type";

    private int type;
    private CustomScanFragment customScanFragment;

    public static ScanFragment getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        ScanFragment fragment = new ScanFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick({R.id.btn_full_scan, R.id.btn_custom_scan})
    public void OnClickLister(final View view) {
        if (ClickFast.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_full_scan:
            case R.id.btn_custom_scan:
                PermissionCompat.with(mActivity)
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

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_fragment_scan;
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
            type = bundle.getInt(ARG_TYPE);
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
                showLoading();
                mPresenter.scan(paths, type);
                return;
            case R.id.btn_custom_scan:
                goCustomScan();
                break;
        }
    }

    private void goCustomScan() {
        if (customScanFragment == null) {
            customScanFragment = CustomScanFragment.getInstance(type);
        }
        ScanActivity activity = (ScanActivity) getActivity();
        activity.replaceFragment(customScanFragment);
    }

    @Override
    public void setDatas(List<FileModel> models) {

    }

    @Override
    public void setMusics(List<MusicModel> models) {
        closeLoading();
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }
}
