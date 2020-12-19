package com.d.music.setting.activity;

import android.view.View;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.app.BaseActivity;
import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.component.statusbarcompat.StatusBarCompat;
import com.d.lib.common.util.ViewHelper;
import com.d.music.R;

import cn.feng.skin.manager.loader.SkinManager;

/**
 * AboutActivity
 * Created by D on 2017/6/13.
 */
public class AboutActivity extends BaseActivity<MvpBasePresenter>
        implements MvpView, View.OnClickListener {

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
        return R.layout.module_setting_activity_about;
    }

    @Override
    public MvpBasePresenter getPresenter() {
        return new MvpBasePresenter(getApplicationContext());
    }

    @Override
    protected MvpView getMvpView() {
        return this;
    }

    @Override
    protected void bindView() {
        super.bindView();
        ViewHelper.setOnClickListener(this, this,
                R.id.iv_title_left);
    }

    @Override
    protected void init() {
        StatusBarCompat.setStatusBarColor(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }

//    @Override
//    public void onThemeUpdate() {
//        super.onThemeUpdate();
//        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
//    }
}
