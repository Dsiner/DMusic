package com.d.music.local.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.lib.common.util.log.ULog;
import com.d.lib.common.widget.TitleLayout;
import com.d.music.R;
import com.d.music.local.fragment.CustomScanFragment;
import com.d.music.local.fragment.ScanFragment;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanActivity extends BaseFragmentActivity implements View.OnClickListener {
    public static final String EXTRA_TYPE = "type";

    TitleLayout tl_title;

    private Fragment mFragment;
    private FragmentManager mFragmentManager;

    public static void openActivity(Context context, int type) {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_title_left:
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.module_local_activity_scan;
    }

    @Override
    protected void bindView() {
        tl_title = findViewById(R.id.tl_title);

        ViewHelper.setOnClickListener(this, this, R.id.iv_title_left);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(ScanActivity.this,
                SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        int type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        ULog.d("type" + type);
        initTitle();
        replaceFragment(ScanFragment.getInstance(type));
    }

    private void initTitle() {
        tl_title.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_scan_music));
    }

    public void replaceFragment(Fragment fragment) {
        this.mFragment = fragment;
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
//    }

    @Override
    public void onBackPressed() {
        if (mFragment instanceof CustomScanFragment
                && ((CustomScanFragment) mFragment).onBackPressed()) {
            return;
        }
        if (mFragmentManager.getBackStackEntryCount() <= 1) {
            finish();
        } else {
            mFragmentManager.popBackStack();
        }
    }
}
