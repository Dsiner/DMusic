package com.d.music.mvp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.d.commen.base.BaseFragment;
import com.d.commen.mvp.MvpView;
import com.d.music.R;
import com.d.commen.commen.AlertDialogFactory;
import com.d.music.commen.Preferences;
import com.d.music.model.FileModel;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.music.mvp.activity.ScanActivity;
import com.d.music.mvp.presenter.ScanPresenter;
import com.d.music.mvp.view.IScanView;
import com.d.commen.utils.Util;
import com.d.music.utils.fileutil.FileUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
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
    @Bind(R.id.btn_full_scan)
    Button btnFullScan;
    @Bind(R.id.btn_custom_scan)
    Button btnCustomScan;

    private Context context;
    private int type;
    private CustomScanFragment customScanFragment;
    private AlertDialog dialog;//进度提示dialog
    private int[] cornerDrawable = new int[]{R.drawable.bg_corner_main
            , R.drawable.bg_corner_main0
            , R.drawable.bg_corner_main1
            , R.drawable.bg_corner_main2
            , R.drawable.bg_corner_main3
            , R.drawable.bg_corner_main4
            , R.drawable.bg_corner_main5
            , R.drawable.bg_corner_main6
            , R.drawable.bg_corner_main7
            , R.drawable.bg_corner_main8
            , R.drawable.bg_corner_main9
            , R.drawable.bg_corner_main10
            , R.drawable.bg_corner_main11
            , R.drawable.bg_corner_main12
            , R.drawable.bg_corner_main13
            , R.drawable.bg_corner_main14
            , R.drawable.bg_corner_main15
            , R.drawable.bg_corner_main16
            , R.drawable.bg_corner_main17
    };

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

    @Override
    public void onResume() {
        int cur = Preferences.getInstance(context.getApplicationContext()).getSkin() + 1;
        if (cur >= 0 && cur < cornerDrawable.length) {
            btnFullScan.setBackgroundResource(cornerDrawable[cur]);
            btnCustomScan.setBackgroundResource(cornerDrawable[cur]);
        }
        super.onResume();
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
