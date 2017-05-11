package com.d.dmusic.mvp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.d.commen.base.BaseFragmentActivity;
import com.d.dmusic.R;
import com.d.dmusic.mvp.fragment.CustomScanFragment;
import com.d.dmusic.mvp.fragment.ScanFragment;
import com.d.dmusic.utils.StatusBarCompat;
import com.d.dmusic.utils.log.ULog;
import com.d.dmusic.view.TitleLayout;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanActivity extends BaseFragmentActivity implements OnClickListener {
    @Bind(R.id.tl_title)
    TitleLayout tlTitle;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    @OnClick({R.id.iv_title_left})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_scan;
    }

    @Override
    protected void init() {
        int type = getIntent().getIntExtra("type", 0);
        ULog.v("type" + type);
        StatusBarCompat.compat(ScanActivity.this, 0xffff0000);//沉浸式状态栏

        initTitle();

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        Fragment scanFragment = new ScanFragment();
        scanFragment.setArguments(bundle);
        replaceFragment(scanFragment);
    }

    private void initTitle() {
        tlTitle.setText(R.id.tv_title_title, "扫描歌曲");
    }

    public void replaceFragment(Fragment fragment) {
        this.fragment = fragment;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (fragment instanceof CustomScanFragment && ((CustomScanFragment) fragment).onBackPressed()) {
            return;
        }
        if (fragmentManager.getBackStackEntryCount() <= 1) {
            finish();
        } else {
            fragmentManager.popBackStack();
        }
    }
}
