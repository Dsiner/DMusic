package com.d.music.mvp.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.module.mvp.base.BaseFragment;
import com.d.music.R;
import com.d.commen.commen.AlertDialogFactory;
import com.d.music.model.FileModel;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.music.mvp.adapter.DirAdapter;
import com.d.music.mvp.presenter.ScanPresenter;
import com.d.music.mvp.view.IScanView;
import com.d.commen.utils.Util;
import com.d.music.utils.fileutil.FileUtil;
import com.d.lib.xrv.LRecyclerView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 扫描歌曲-自定义扫描
 * Created by D on 2017/4/29.
 */
public class CustomScanFragment extends BaseFragment<ScanPresenter> implements IScanView, DirAdapter.OnPathListener {
    @BindView(R.id.llyt_dir)
    LinearLayout llytDir;
    @BindView(R.id.llyt_scan_now)
    LinearLayout llytScanNow;
    @BindView(R.id.tv_current_dir)
    TextView tvCurrentDir;
    @BindView(R.id.lrv_list)
    LRecyclerView lrvList;

    private Context context;
    private final String rootPath = FileUtil.getRootPath();
    private String curPath;
    private DirAdapter adapter;
    private List<FileModel> models;
    private AlertDialog dialog;//进度提示dialog
    private int type;

    @OnClick({R.id.llyt_dir, R.id.llyt_scan_now})
    public void OnClickLister(final View view) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.llyt_dir:
            case R.id.llyt_scan_now:
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
                    Util.toast(context, "请先选择扫描路径");
                    return;
                }
                mPresenter.scan(paths, type);
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_custom_scan;
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
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type");
        }
    }

    @Override
    protected void init() {
        models = new ArrayList<>();
        adapter = new DirAdapter(getActivity(), models, R.layout.adapter_dir);
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
