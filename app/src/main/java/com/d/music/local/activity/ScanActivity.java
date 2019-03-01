package com.d.music.local.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.d.lib.common.component.mvp.app.v4.BaseFragmentActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.lib.common.utils.log.ULog;
import com.d.lib.common.view.TitleLayout;
import com.d.music.R;
import com.d.music.local.fragment.CustomScanFragment;
import com.d.music.local.fragment.ScanFragment;
import com.d.music.utils.StatusBarCompat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * 扫描首页
 * Created by D on 2017/4/29.
 */
public class ScanActivity extends BaseFragmentActivity implements OnClickListener {
    public final static String ARG_TYPE = "type";

    @BindView(R.id.tl_title)
    TitleLayout tlTitle;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    public static void startActivity(Context context, int type) {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(ARG_TYPE, type);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_title_left})
    public void onClick(View v) {
        if (ClickFast.isFastDoubleClick()) {
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
    protected void init() {
        StatusBarCompat.compat(ScanActivity.this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
        int type = getIntent().getIntExtra(ARG_TYPE, 0);
        ULog.d("type" + type);
        initTitle();
        replaceFragment(ScanFragment.getInstance(type));
    }

    private void initTitle() {
        tlTitle.setText(R.id.tv_title_title, getResources().getString(R.string.module_common_scan_music));
    }

    public void replaceFragment(Fragment fragment) {
        this.fragment = fragment;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));
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
