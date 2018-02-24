package com.d.music.mvp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.d.lib.common.module.mvp.base.BaseFragment;
import com.d.lib.common.common.AlertDialogFactory;
import com.d.lib.common.module.repeatclick.ClickUtil;
import com.d.lib.common.module.mvp.MvpView;
import com.d.lib.common.utils.Util;
import com.d.music.R;
import com.d.music.model.FileModel;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.mvp.activity.ScanActivity;
import com.d.music.mvp.presenter.ScanPresenter;
import com.d.music.mvp.view.IScanView;
import com.d.music.utils.fileutil.FileUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanFragment extends BaseFragment<ScanPresenter> implements IScanView {
    private Context context;
    private int type;
    private CustomScanFragment customScanFragment;
    private AlertDialog dialog;//进度提示dialog

    @OnClick({R.id.btn_full_scan, R.id.btn_custom_scan})
    public void OnClickLister(final View view) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_full_scan:
            case R.id.btn_custom_scan:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    RxPermissions rxPermissions = new RxPermissions((Activity) context);
                    rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Permission>() {
                                @Override
                                public void accept(@NonNull Permission permission) throws Exception {
                                    if (context == null || getActivity() == null || getActivity().isFinishing()) {
                                        return;
                                    }
                                    if (permission.granted) {
                                        // `permission.name` is granted !
                                        sw(view.getId());
                                    } else if (permission.shouldShowRequestPermissionRationale) {
                                        // Denied permission without ask never again
                                        Util.toast(context, "Denied permission!");
                                    } else {
                                        // Denied permission with ask never again
                                        // Need to go to the settings
                                        Util.toast(context, "Denied permission with ask never again!");
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
        context = getActivity();
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

    @Override
    public void showLoading() {
        if (dialog == null) {
            dialog = AlertDialogFactory.createFactory(getActivity()).getLoadingDialog();
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void closeLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
