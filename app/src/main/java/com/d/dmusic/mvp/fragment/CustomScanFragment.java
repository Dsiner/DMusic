package com.d.dmusic.mvp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.commen.base.BaseFragment;
import com.d.dmusic.R;
import com.d.dmusic.commen.AlertDialogFactory;
import com.d.dmusic.model.FileModel;
import com.d.dmusic.module.events.MusicModelEvent;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.mvp.adapter.DirAdapter;
import com.d.dmusic.mvp.presenter.ScanPresenter;
import com.d.dmusic.mvp.view.IScanView;
import com.d.dmusic.utils.Util;
import com.d.dmusic.utils.fileutil.FileUtil;
import com.d.xrv.LRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * CustomScanFragment
 * Created by D on 2017/4/29.
 */
public class CustomScanFragment extends BaseFragment<ScanPresenter> implements IScanView, DirAdapter.OnPathListener {
    @Bind(R.id.llyt_dir)
    LinearLayout llytDir;
    @Bind(R.id.llyt_scan_now)
    LinearLayout llytScanNow;
    @Bind(R.id.tv_current_dir)
    TextView tvCurrentDir;
    @Bind(R.id.lrv_list)
    LRecyclerView lrvList;

    private Context context;
    private final String rootPath = FileUtil.getRootPath();
    private String curPath;
    private DirAdapter adapter;
    private List<FileModel> models;
    private AlertDialog dialog;//进度提示dialog
    private int type;

    @OnClick({R.id.llyt_dir, R.id.llyt_scan_now})
    public void OnClickLister(View view) {
        switch (view.getId()) {
            case R.id.llyt_dir:
                onBackPressed();
                break;
            case R.id.llyt_scan_now:
                List<String> paths = new ArrayList<String>();
                for (FileModel fileModel : models) {
                    if (fileModel.isChecked) {
                        paths.add(fileModel.absolutePath);
                    }
                }
                if (paths.size() > 0) {
                    mPresenter.getMusics(paths, type);
                } else {
                    Util.toast(context, "请先选择扫描路径");
                }
                break;
        }
    }

    static class WeakHandler extends Handler {
        WeakReference<CustomScanFragment> fragment;

        WeakHandler(CustomScanFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomScanFragment theFragment = this.fragment.get();
            if (theFragment == null || theFragment.getActivity() == null || theFragment.getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case 1:
                    theFragment.dialog.dismiss();
                    theFragment.getActivity().finish();
                    break;
            }
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
