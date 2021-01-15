package com.d.music.local.fragment;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.lib.common.component.mvp.app.v4.BaseFragment;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.ToastUtils;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.permissioncompat.Permission;
import com.d.lib.permissioncompat.PermissionCompat;
import com.d.lib.permissioncompat.PermissionSchedulers;
import com.d.lib.permissioncompat.callback.PermissionCallback;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.local.adapter.DirAdapter;
import com.d.music.local.model.FileModel;
import com.d.music.local.presenter.ScanPresenter;
import com.d.music.local.view.IScanView;
import com.d.music.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描歌曲-自定义扫描
 * Created by D on 2017/4/29.
 */
public class CustomScanFragment extends BaseFragment<ScanPresenter>
        implements IScanView, View.OnClickListener, DirAdapter.OnPathListener {
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    private final String rootPath = FileUtils.getRootPath();
    LinearLayout llyt_dir;
    LinearLayout llyt_scan_now;
    TextView tv_current_dir;
    RecyclerView rv_list;
    private int type;
    private String curPath;
    private DirAdapter adapter;
    private List<FileModel> models;

    public static CustomScanFragment getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        CustomScanFragment fragment = new CustomScanFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(final View view) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.llyt_dir:
            case R.id.llyt_scan_now:
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
                                    ToastUtils.toast(getActivity().getApplicationContext(), "Denied permission!");
                                } else {
                                    // Denied permission with ask never again
                                    // Need to go to the settings
                                    ToastUtils.toast(getActivity().getApplicationContext(), "Denied permission with ask never again!");
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
                    ToastUtils.toast(mContext, getResources().getString(R.string.module_common_please_select_scan_path));
                    return;
                }
                showLoadingDialog();
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
            type = bundle.getInt(EXTRA_TYPE);
        }
    }

    @Override
    protected void bindView(View rootView) {
        llyt_dir = rootView.findViewById(R.id.llyt_dir);
        llyt_scan_now = rootView.findViewById(R.id.llyt_scan_now);
        tv_current_dir = rootView.findViewById(R.id.tv_current_dir);
        rv_list = rootView.findViewById(R.id.rv_list);

        ViewHelper.setOnClickListener(rootView, this,
                R.id.llyt_dir, R.id.llyt_scan_now);
    }

    @Override
    protected void init() {
        models = new ArrayList<>();
        adapter = new DirAdapter(getActivity(), models, R.layout.module_local_adapter_dir);
        adapter.setOnPathListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onPath(rootPath);
    }

    @Override
    public void loadSuccess(List<FileModel> models) {
        this.models = models;
        adapter.setDatas(this.models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setMusics(List<MusicModel> models) {
        dismissLoadingDialog();
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    @Override
    public void onPath(String path) {
        curPath = path;
        tv_current_dir.setText(curPath);
        mPresenter.getFileModels(curPath);
    }

    public boolean onBackPressed() {
        if (!TextUtils.equals(curPath, rootPath)) {
            onPath(FileUtils.getParentPath(curPath));
            return true;
        }
        return false;
    }
}
