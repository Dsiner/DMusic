package com.d.dmusic.mvp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.d.commen.base.BaseFragment;
import com.d.commen.mvp.MvpBasePresenter;
import com.d.commen.mvp.MvpView;
import com.d.dmusic.R;
import com.d.dmusic.commen.AlertDialogFactory;
import com.d.dmusic.module.greendao.music.LocalAllMusic;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.module.media.MusicFactory;
import com.d.dmusic.mvp.activity.ScanActivity;
import com.d.dmusic.utils.TaskManager;
import com.d.dmusic.utils.fileutil.FileUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by D on 2017/4/29.
 */
public class ScanFragment extends BaseFragment<MvpBasePresenter> implements MvpView {
    @Bind(R.id.btn_full_scan)
    Button btnFullScan;
    @Bind(R.id.btn_custom_scan)
    Button btnCustomScan;

    private Context context;
    private CustomScanFragment customScanFragment;
    private AlertDialog dialog;//进度提示dialog
    private int type;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_scan;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(this.getActivity().getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void init() {
        context = getActivity();
        ScanActivity activity = (ScanActivity) context;
        type = activity.getType();
    }

    @OnClick({R.id.btn_full_scan, R.id.btn_custom_scan})
    public void OnClickLister(View view) {
        switch (view.getId()) {
            case R.id.btn_full_scan:
                dialog = AlertDialogFactory.createFactory(context).getLoadingDialog();
                TaskManager.getIns().executeTask(new Runnable() {
                    @Override
                    public void run() {
                        List<String> paths = new ArrayList<String>();
                        paths.add(FileUtil.getRootPath());
                        List<LocalAllMusic> list = (List<LocalAllMusic>) MusicFactory.createFactory(context, type).getMusic(paths);
                        MusicDBUtil.getInstance(context).deleteAll(type);
                        MusicDBUtil.getInstance(context).insertOrReplaceMusicInTx(list, type);
                    }
                });
                break;
            case R.id.btn_custom_scan:
                if (customScanFragment == null) {
                    customScanFragment = new CustomScanFragment();
                }
                ScanActivity activity = (ScanActivity) getActivity();
                activity.replaceFragment(customScanFragment);
                break;
        }
    }
}
