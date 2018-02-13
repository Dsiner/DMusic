package com.d.music.mvp.activity;

import android.view.View;

import com.d.commen.module.mvp.MvpBasePresenter;
import com.d.commen.module.mvp.MvpView;
import com.d.commen.module.mvp.base.BaseActivity;
import com.d.commen.module.repeatclick.ClickUtil;
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
        if (ClickUtil.isFastDoubleClick()) {
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
        return R.layout.activity_about;
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
