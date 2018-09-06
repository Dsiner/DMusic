package com.d.music.setting.activity;

import android.view.View;

import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.common.component.mvp.MvpView;
import com.d.lib.common.component.mvp.base.BaseActivity;
import com.d.lib.common.component.repeatclick.ClickFast;
import com.d.music.R;
import com.d.music.utils.StatusBarCompat;

import butterknife.OnClick;
import cn.feng.skin.manager.loader.SkinManager;

/**
 * AboutActivity
 * Created by D on 2017/6/13.
 */
public class AboutActivity extends BaseActivity<MvpBasePresenter> implements MvpView {

    @OnClick({R.id.iv_title_left})
    public void onClickListener(View v) {
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
    protected void init() {
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }

    @Override
    public void onThemeUpdate() {
        super.onThemeUpdate();
        StatusBarCompat.compat(this, SkinManager.getInstance().getColor(R.color.lib_pub_color_main));//沉浸式状态栏
    }
}
